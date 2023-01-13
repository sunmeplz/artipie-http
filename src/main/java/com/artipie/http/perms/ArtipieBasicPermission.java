/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import java.security.Permission;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Artipie basic permission. This permission takes into account repository name and
 * the set of actions. Both parameters are required, repository name is composite in the
 * case of ORG layout {user_name}/{repo_name}.
 * Supported actions are: read, write, delete. Wildcard * is also supported and means,
 * that any actions is allowed.
 * This permission implies another permission if permissions names are equal and
 * this permission allows all actions from another permission.
 * @since 1.2
 * @checkstyle DesignForExtensionCheck (500 lines)
 */
public class ArtipieBasicPermission extends Permission {

    /**
     * Required serial.
     */
    private static final long serialVersionUID = -2916496571451236071L;

    /**
     * Actions set.
     */
    private final Set<String> actions;

    /**
     * Ctor.
     * @param repo Repository name
     * @param strings Actions set
     */
    public ArtipieBasicPermission(final String repo, final Set<String> strings) {
        super(repo);
        this.actions = strings;
    }

    /**
     * Ctor, for usage in adapters, where usually one action is required for
     * specific operation.
     * @param repo Repository name
     * @param action Action
     */
    public ArtipieBasicPermission(final String repo, final String action) {
        this(repo, Collections.singleton(action));
    }

    /**
     * Constructs a permission from configuration.
     *
     * @param config Permission configuration
     */
    public ArtipieBasicPermission(final PermissionConfig config) {
        this(config.name(), config.sequence(config.name()));
    }

    @Override
    public boolean implies(final Permission permission) {
        throw new NotImplementedException("Not implemented yet");
    }

    @Override
    public boolean equals(final Object obj) {
        throw new NotImplementedException("to be implemented");
    }

    @Override
    public int hashCode() {
        throw new NotImplementedException("Not ready yet");
    }

    @Override
    public String getActions() {
        return Arrays.toString(this.actions.toArray());
    }

}
