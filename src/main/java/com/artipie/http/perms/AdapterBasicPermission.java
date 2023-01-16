/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

import com.artipie.ArtipieException;
import java.security.Permission;
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
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
public class AdapterBasicPermission extends Permission {

    /**
     * Required serial.
     */
    private static final long serialVersionUID = -2916496571451236071L;

    /**
     * Actions set.
     */
    private final String actions;

    /**
     * Ctor.
     * @param repo Repository name
     * @param strings Actions set
     */
    public AdapterBasicPermission(final String repo, final String strings) {
        super(repo);
        this.actions = strings;
    }

    /**
     * Constructs a permission from configuration.
     *
     * @param config Permission configuration
     */
    public AdapterBasicPermission(final PermissionConfig config) {
        this(
            config.name(), config.sequence(config.name()).stream().reduce(String::concat)
                .orElseThrow(
                    () -> new ArtipieException(
                        "Actions list for AdapterBasicPermission cannot be empty"
                    )
                )
        );
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
        throw new NotImplementedException(
            "Implement properly to return 'canonical string representation' of the actions"
        );
    }

}
