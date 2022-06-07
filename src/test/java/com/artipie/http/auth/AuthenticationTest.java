/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.auth;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests for {@link Authentication}.
 *
 * @since 0.15
 */
final class AuthenticationTest {

    @Test
    void wrapDelegatesToOrigin() {
        final String user = "user";
        final String pass = "pass";
        final Optional<Authentication.User> result = Optional.of(new Authentication.User("result"));
        MatcherAssert.assertThat(
            "Result is forwarded from delegate without modification",
            new TestAuthentication(
                (username, password) -> {
                    MatcherAssert.assertThat(
                        "Username is forwarded to delegate without modification",
                        username,
                        new IsEqual<>(user)
                    );
                    MatcherAssert.assertThat(
                        "Password is forwarded to delegate without modification",
                        password,
                        new IsEqual<>(pass)
                    );
                    return result;
                }
            ).user(user, pass),
            new IsEqual<>(result)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "Aladdin,OpenSesame,true",
        "Aladdin,qwerty,false",
        "JohnDoe,12345,false"
    })
    void singleAuthenticatesAsExpected(
        final String username,
        final String password,
        final boolean expected
    ) {
        MatcherAssert.assertThat(
            new Authentication.Single("Aladdin", "OpenSesame")
                .user(username, password)
                .isPresent(),
            new IsEqual<>(expected)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "Alice,LetMeIn,Alice",
        "Bob,iamgod,Bob"
    })
    void joinedAuthenticatesAsExpected(
        final String username,
        final String password,
        final String expected
    ) {
        MatcherAssert.assertThat(
            new Authentication.Joined(
                new Authentication.Single("Alice", "LetMeIn"),
                new Authentication.Single("Bob", "iamgod")
            ).user(username, password),
            new IsEqual<>(Optional.of(new Authentication.User(expected)))
        );
    }

    @ParameterizedTest
    @CsvSource({
        "Alice,staff,admin",
        "Bob,wheel",
        "Jeff,wheel,vip"
    })
    void userHasProperToString(
        final String username,
        @AggregateWith(AuthenticationTest.AsCollection.class)
        final Collection<String> groups
    ) {
        MatcherAssert.assertThat(
            new Authentication.User(username, groups),
            Matchers.hasToString(
                Matchers.allOf(
                    Matchers.containsString(username),
                    Matchers.stringContainsInOrder(groups)
                )
            )
        );
    }

    @Test
    void emptyOptionalIfNotPresent() {
        MatcherAssert.assertThat(
            new Authentication.Joined(
                new Authentication.Single("Alan", "123"),
                new Authentication.Single("Mark", "0000")
            ).user("Smith", "abc"),
            new IsEqual<>(Optional.empty())
        );
    }

    /**
     * Authentication for testing Authentication.Wrap.
     *
     * @since 0.15
     */
    private static class TestAuthentication extends Authentication.Wrap {

        /**
         * Ctor.
         *
         * @param auth Origin authentication.
         */
        protected TestAuthentication(final Authentication auth) {
            super(auth);
        }
    }

    /**
     * Aggregate trailing parameters.
     *
     * @since 0.5.2
     */
    private static final class AsCollection implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(final ArgumentsAccessor accessor,
            final ParameterContext context) throws ArgumentsAggregationException {
            return IntStream.range(context.getIndex(), accessor.size())
                .mapToObj(accessor::getString)
                .collect(Collectors.toList());
        }
    }
}
