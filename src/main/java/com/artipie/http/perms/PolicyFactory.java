/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package com.artipie.http.perms;

/**
 * Factory to create {@link Policy} instance.
 * @since 1.2
 */
public interface PolicyFactory {

    /**
     * Create {@link Policy} from provided {@link PolicyConfig}.
     * @param config Configuration
     * @return Instance of {@link Policy}
     */
    Policy<?> getPolicy(PolicyConfig config);

}
