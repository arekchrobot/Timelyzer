package pl.ark.chr.timelyzer;

import pl.ark.chr.timelyzer.auth.MongoUsernamePasswordAuthenticator;
import pl.ark.chr.timelyzer.config.Server;
import pl.ark.chr.timelyzer.factory.EndpointFactory;
import pl.ark.chr.timelyzer.factory.ServiceFactory;
import pl.ark.chr.timelyzer.rest.TestEndpoint;

/**
 * Created by Arek on 2017-06-17.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        MongoUsernamePasswordAuthenticator authenticator = ServiceFactory.mongoUsernamePasswordAuthenticator();
        Server server = new Server(authenticator, new TestEndpoint(), EndpointFactory.projectEndpoint());
        server.start();
    }
}
