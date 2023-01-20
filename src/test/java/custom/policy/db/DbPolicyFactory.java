/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package custom.policy.db;

import com.artipie.security.policy.ArtipiePolicyFactory;
import com.artipie.security.policy.PoliciesLoaderTest;
import com.artipie.security.policy.Policy;
import com.artipie.security.policy.PolicyConfig;
import com.artipie.security.policy.PolicyFactory;
import java.security.Permissions;

/**
 * Test policy.
 * @since 1.2
 */
@ArtipiePolicyFactory("db-policy")
public final class DbPolicyFactory implements PolicyFactory {
    @Override
    public Policy<Permissions> getPolicy(final PolicyConfig config) {
        return new PoliciesLoaderTest.TestPolicy();
    }
}
