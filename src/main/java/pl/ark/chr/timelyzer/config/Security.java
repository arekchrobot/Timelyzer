package pl.ark.chr.timelyzer.config;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import pl.ark.chr.timelyzer.rest.RestEndpoint;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.pac4j.RatpackPac4j;

/**
 * Created by Arek on 2017-06-18.
 */
public class Security {

    public static <C extends Credentials, U extends UserProfile> Action<Chain> auth(Class<? extends Client<C, U>> clientClass) {
        return chain -> chain.all(RatpackPac4j.requireAuth(clientClass));
    }

    public static <C extends Credentials, U extends UserProfile> Action<Chain> auth(Class<? extends Client<C, U>> clientClass, Authorizer<? super U>... authorizers) {
        return chain -> chain.all(RatpackPac4j.requireAuth(clientClass, authorizers));
    }

    public static <C extends Credentials, U extends UserProfile> Action<Chain> auth(Class<? extends Client<C, U>> clientClass, RestEndpoint[] endpoints) {
        return chain -> {
            chain = chain.all(RatpackPac4j.requireAuth(clientClass));

            for (RestEndpoint endpoint : endpoints) {
                chain = chain.prefix(endpoint.getApiPrefix(), endpoint.defineActions());
            }
        };
    }

    public static <C extends Credentials, U extends UserProfile> Action<Chain> auth(Class<? extends Client<C, U>> clientClass,  RestEndpoint[] endpoints, Authorizer<? super U>... authorizers) {
        return chain -> {
            chain = chain.all(RatpackPac4j.requireAuth(clientClass, authorizers));

            for (RestEndpoint endpoint : endpoints) {
                chain = chain.prefix(endpoint.getApiPrefix(), endpoint.defineActions());
            }
        };
    }
}
