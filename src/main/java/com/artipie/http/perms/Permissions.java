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
import java.security.Permission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

/**
 * Instantiate permission factories object.
 * @since 1.2
 */
public final class Permissions {

    /**
     * Environment parameter to define packages to find permission factories.
     * Package names should be separated with semicolon ';'.
     */
    public static final String SCAN_PACK = "PERM_FACTORY_SCAN_PACKAGES";

    /**
     * Default package to find permissions factories.
     */
    private static final String DEFAULT_PACKAGE = "com.artipie.http";

    /**
     * Permissions factories: name <-> factory.
     */
    private final Map<String, PermissionFactory> factories;

    /**
     * Ctor to obtain factories according to env.
     */
    public Permissions() {
        this(System.getenv());
    }

    /**
     * Ctor.
     * @param env Environment
     */
    public Permissions(final Map<String, String> env) {
        this.factories = init(env);
    }

    /**
     * Obtain permission by type.
     *
     * @param type Permission type
     * @param config Permission configuration
     * @return Permission instance
     * @throws ArtipieException If permission with given type does not exist
     */
    public Permission newPermission(final String type, final PermissionConfig config) {
        final PermissionFactory factory = this.factories.get(type);
        if (factory == null) {
            throw new ArtipieException(String.format("Permission type %s is not found", type));
        }
        return factory.newPermission(config);
    }

    /**
     * Finds and initiates annotated classes in default and env packages.
     * @param env Environment parameters
     * @return Map of {@link PermissionFactory}
     */
    private static Map<String, PermissionFactory> init(final Map<String, String> env) {
        final List<String> pkgs = Lists.newArrayList(Permissions.DEFAULT_PACKAGE);
        final String pgs = env.get(Permissions.SCAN_PACK);
        if (!Strings.isNullOrEmpty(pgs)) {
            pkgs.addAll(Arrays.asList(pgs.split(";")));
        }
        final Map<String, PermissionFactory> res = new HashMap<>();
        pkgs.forEach(
            pkg -> new Reflections(pkg)
                .get(Scanners.TypesAnnotated.with(ArtipiePermissionFactory.class).asClass())
                .forEach(
                    clazz -> {
                        final String type = Arrays.stream(clazz.getAnnotations())
                            .filter(ArtipiePermissionFactory.class::isInstance)
                            .map(inst -> ((ArtipiePermissionFactory) inst).value())
                            .findFirst()
                            .orElseThrow(
                                // @checkstyle LineLengthCheck (1 lines)
                                () -> new ArtipieException("Annotation 'ArtipiePermissionFactory' should have a not empty value")
                            );
                        final PermissionFactory existed = res.get(type);
                        if (existed != null) {
                            throw new ArtipieException(
                                String.format(
                                    "Storage factory with type '%s' already exists [class=%s].",
                                    type, existed.getClass().getSimpleName()
                                )
                            );
                        }
                        try {
                            res.put(
                                type,
                                (PermissionFactory) clazz.getDeclaredConstructor().newInstance()
                            );
                            Logger.info(
                                Permissions.class,
                                "Initiated permission factory [type=%s, class=%s]",
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
