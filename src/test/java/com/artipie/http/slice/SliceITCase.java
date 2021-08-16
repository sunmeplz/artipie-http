/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/npm-adapter/LICENSE.txt
 */
package com.artipie.http.slice;

import com.artipie.http.Slice;
import com.artipie.http.auth.Action;
import com.artipie.http.auth.Authentication;
import com.artipie.http.auth.BasicAuthSlice;
import com.artipie.http.auth.Permission;
import com.artipie.http.auth.Permissions;
import com.artipie.http.misc.RandomFreePort;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rs.common.RsJson;
import com.artipie.http.rt.ByMethodsRule;
import com.artipie.http.rt.RtRulePath;
import com.artipie.http.rt.SliceRoute;
import com.artipie.vertx.VertxSliceServer;
import com.jcabi.log.Logger;
import io.vertx.reactivex.core.Vertx;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

/**
 * Slices integration tests.
 * @since 0.20
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class SliceITCase {

    /**
     * Vertx instance.
     */
    private static final Vertx VERTX = Vertx.vertx();

    /**
     * Vertx slice server instance.
     */
    private VertxSliceServer server;

    /**
     * Container.
     */
    private GenericContainer<?> cntn;

    /**
     * Application port.
     */
    private int port;

    @BeforeEach
    void init() throws Exception {
        this.port = new RandomFreePort().get();
        this.server = new VertxSliceServer(
            SliceITCase.VERTX, new LoggingSlice(new TestSlice()), this.port
        );
        this.server.start();
        Testcontainers.exposeHostPorts(this.port);
        this.cntn = new GenericContainer<>("alpine:3.11")
            .withCommand("tail", "-f", "/dev/null").withWorkingDirectory("/w");
        this.cntn.start();
        this.exec("apk", "add", "--no-cache", "curl");
    }

    @Test
    void singleRequestWorks() throws Exception {
        this.getRequest();
    }

    @Test
    @Disabled
    void doubleRequestWorks() throws Exception {
        this.getRequest();
        this.getRequest();
    }

    @AfterEach
    void stop() {
        this.server.stop();
        this.cntn.stop();
    }

    private void getRequest() throws Exception {
        MatcherAssert.assertThat(
            this.exec(
                "curl", "-v",
                String.format("http://host.testcontainers.internal:%s/any", this.port)
            ),
            new StringContains("{\"any\":\"any\"}")
        );
    }

    private String exec(final String... command) throws Exception {
        final Container.ExecResult res = this.cntn.execInContainer(command);
        Logger.debug(this, "Command:\n%s\nResult:\n%s", String.join(" ", command), res.toString());
        return res.toString();
    }

    /**
     * Test slice implementation.
     * @since 0.20
     */
    private static final class TestSlice extends Slice.Wrap {

        /**
         * Ctor.
         */
        protected TestSlice() {
            super(
                new SliceRoute(
                    new RtRulePath(
                        new ByMethodsRule(RqMethod.GET),
                        new BasicAuthSlice(
                            new SliceSimple(
                                new RsJson(
                                    () -> Json.createObjectBuilder().add("any", "any").build()
                                )
                            ),
                            Authentication.ANONYMOUS,
                            new Permission.ByName(Permissions.FREE, Action.Standard.READ)
                        )
                    )
                )
            );
        }
    }

}
