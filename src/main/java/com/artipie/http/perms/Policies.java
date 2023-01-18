/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import com.artipie.ArtipieException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jcabi.log.Logger;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

/**
 * Create existing instances of {@link PolicyFactory} implementations.
 * @since 1.2
 */
public final class Policies {

    /**
     * Environment parameter to define packages to find policies factories.
     * Package names should be separated with semicolon ';'.
     */
    public static final String SCAN_PACK = "POLICY_FACTORY_SCAN_PACKAGES";

    /**
     * Default package to find policies factories.
     */
    private static final String DEFAULT_PACKAGE = "com.artipie.http";

    /**
     * Policies factories: name <-> factory.
     */
    private final Map<String, PolicyFactory> factories;

    /**
     * Ctor.
     * @param env Environment map
     */
    public Policies(final Map<String, String> env) {
        this.factories = init(env);
    }

    /**
     * Create policies from env.
     */
    public Policies() {
        this(System.getenv());
    }

    /**
     * Obtain policy by type.
     *
     * @param type Policy type
     * @param config Policy configuration
     * @return Policy instance
     * @throws ArtipieException If policy with given type does not exist
     */
    public Policy<?> newPolicy(final String type, final PolicyConfig config) {
        final PolicyFactory factory = this.factories.get(type);
        if (factory == null) {
            throw new ArtipieException(String.format("Policy type %s is not found", type));
        }
        return factory.getPolicy(config);
    }

    /**
     * Finds and initiates annotated classes in default and env packages.
     * @param env Environment parameters
     * @return Map of {@link PolicyFactory}
     */
    private static Map<String, PolicyFactory> init(final Map<String, String> env) {
        final List<String> pkgs = Lists.newArrayList(Policies.DEFAULT_PACKAGE);
        final String pgs = env.get(Policies.SCAN_PACK);
        if (!Strings.isNullOrEmpty(pgs)) {
            pkgs.addAll(Arrays.asList(pgs.split(";")));
        }
        final Map<String, PolicyFactory> res = new HashMap<>();
        pkgs.forEach(
            pkg -> new Reflections(pkg)
                .get(Scanners.TypesAnnotated.with(ArtipiePolicyFactory.class).asClass())
                .forEach(
                    clazz -> {
                        final String type = Arrays.stream(clazz.getAnnotations())
                            .filter(ArtipiePolicyFactory.class::isInstance)
                            .map(inst -> ((ArtipiePolicyFactory) inst).value())
                            .findFirst()
                            .orElseThrow(
                                // @checkstyle LineLengthCheck (1 lines)
                                () -> new ArtipieException("Annotation 'ArtipiePolicyFactory' should have a not empty value")
                            );
                        final PolicyFactory existed = res.get(type);
                        if (existed != null) {
                            throw new ArtipieException(
                                String.format(
                                    "Policy factory with type '%s' already exists [class=%s].",
                                    type, existed.getClass().getSimpleName()
                                )
                            );
                        }
                        try {
                            res.put(
                                type,
                                (PolicyFactory) clazz.getDeclaredConstructor().newInstance()
                            );
                            Logger.info(
                                Policies.class,
                                "Initiated policy factory [type=%s, class=%s]",
                                type, clazz.getSimpleName()
                            );
                        } catch (final InstantiationException | IllegalAccessException
                            | InvocationTargetException | NoSuchMethodException err) {
                            throw new ArtipieException(err);
                        }
                    }
                )
        );
        return res;
    }
}
