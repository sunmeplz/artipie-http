/*
 * The MIT License (MIT) Copyright (c) 2020-2022 artipie.com
 * https://github.com/artipie/http/blob/master/LICENSE.txt
 */
package custom.policy.db;

import com.artipie.http.perms.ArtipiePolicyFactory;
import com.artipie.http.perms.PoliciesTest;
import com.artipie.http.perms.Policy;
import com.artipie.http.perms.PolicyConfig;
import com.artipie.http.perms.PolicyFactory;
import java.security.Permissions;

/**
 * Test policy.
 * @since 1.2
 */
@ArtipiePolicyFactory("db-policy")
public final class DbPolicyFactory implements PolicyFactory {
    @Override
    public Policy<Permissions> getPolicy(final PolicyConfig config) {
        return new PoliciesTest.TestPolicy();
    }
}
