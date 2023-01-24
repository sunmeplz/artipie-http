/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.security.perms;

import java.security.Permission;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Artipie basic permission. This permission takes into account repository name and
 * the set of actions. Both parameters are required, repository name is composite in the
 * case of ORG layout {user_name}/{repo_name}.
 * Supported actions are: read, write, delete. Wildcard * is also supported and means,
 * that any actions is allowed.
 * This permission implies another permission if permissions names are equal and
 * this permission allows all actions from another permission.
 * @since 1.2
 */
public final class AdapterBasicPermission extends Permission {

    /**
     * Required serial.
     */
    private static final long serialVersionUID = -2916496571451236071L;

    /**
     * Canonical action representation. Is initialized once on request
     * by {@link AdapterBasicPermission#getActions()} method.
     */
    private String actions;

    /**
     * Action mask.
     */
    private final transient int mask;

    /**
     * Primary ctor.
     * @param name Perm name
     * @param mask Mask
     */
    private AdapterBasicPermission(final String name, final int mask) {
        super(name);
        this.mask = mask;
    }

    /**
     * Ctor.
     * @param repo Repository name
     * @param strings Actions set
     */
    public AdapterBasicPermission(final String repo, final String strings) {
        this(repo, Set.of(strings.split(",")));
    }

    /**
     * Ctor.
     * @param repo Repository name
     * @param action Action
     */
    public AdapterBasicPermission(final String repo, final Action action) {
        this(repo, action.mask());
    }

    /**
     * Ctor.
     * @param repo Repository name
     * @param strings Actions set
     */
    public AdapterBasicPermission(final String repo, final Set<String> strings) {
        this(repo, AdapterBasicPermission.maskFromActions(strings));
    }

    /**
     * Constructs a permission from configuration.
     *
     * @param config Permission configuration
     */
    public AdapterBasicPermission(final PermissionConfig config) {
        this(config.name(), config.sequence(config.name()));
    }

    @Override
    public boolean implies(final Permission permission) {
        final boolean res;
        if (permission instanceof AdapterBasicPermission) {
            final AdapterBasicPermission that = (AdapterBasicPermission) permission;
            res = (this.mask & that.mask) == that.mask && this.impliesIgnoreMask(that);
        } else {
            res = false;
        }
        return res;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean res;
        if (obj == this) {
            res = true;
        } else if (obj instanceof AdapterBasicPermission) {
            final AdapterBasicPermission that = (AdapterBasicPermission) obj;
            res = that.mask == this.mask && Objects.equals(that.getName(), this.getName());
        } else {
            res = false;
        }
        return res;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String getActions() {
        if (this.actions == null) {
            final StringJoiner joiner = new StringJoiner(",");
            if ((this.mask & Action.Standard.READ.mask()) == Action.Standard.READ.mask()) {
                joiner.add(Action.Standard.READ.name().toLowerCase(Locale.ROOT));
            }
            if ((this.mask & Action.Standard.WRITE.mask()) == Action.Standard.WRITE.mask()) {
                joiner.add(Action.Standard.WRITE.name().toLowerCase(Locale.ROOT));
            }
            if ((this.mask & Action.Standard.DELETE.mask()) == Action.Standard.DELETE.mask()) {
                joiner.add(Action.Standard.DELETE.name().toLowerCase(Locale.ROOT));
            }
            this.actions = joiner.toString();
        }
        return this.actions;
    }

    /**
     * Check if this action implies another action ignoring mask. That is true if
     * permissions names are equal or this permission has wildcard name.
     * @param perm Permission to check for imply
     * @return True when implies
     */
    private boolean impliesIgnoreMask(final AdapterBasicPermission perm) {
        final boolean res;
        if (this.getName().equals("*")) {
            res = true;
        } else {
            res = this.getName().equalsIgnoreCase(perm.getName());
        }
        return res;
    }

    /**
     * Calculate mask from action.
     * @param actions The set of actions
     * @return Integer mask
     */
    private static int maskFromActions(final Set<String> actions) {
        int res = Action.NONE.mask();
        if (actions.isEmpty()) {
            res = Action.NONE.mask();
        } else if (actions.contains(Action.ALL.names().iterator().next())) {
            res = Action.ALL.mask();
        } else {
            for (final String item : actions) {
                res |= Action.Standard.maskByAction(item);
            }
        }
        return res;
    }
}
