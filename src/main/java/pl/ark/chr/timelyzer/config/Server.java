package pl.ark.chr.timelyzer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import javaslang.control.Try;
import javaslang.jackson.datatype.JavaslangModule;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import pl.ark.chr.timelyzer.auth.MongoUsernamePasswordAuthenticator;
import pl.ark.chr.timelyzer.auth.Security;
import pl.ark.chr.timelyzer.repository.UserRepository;
import pl.ark.chr.timelyzer.rest.RestEndpoint;
import pl.ark.chr.timelyzer.util.AppProps;
import ratpack.error.ClientErrorHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.guice.Guice;
import ratpack.pac4j.RatpackPac4j;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerSpec;
import ratpack.server.ServerConfig;
import ratpack.session.SessionModule;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Arek on 2017-06-18.
 */
public class Server {

    private final RatpackServer ratpackServer;

    public Server(RestEndpoint... endpoints) {
        this.ratpackServer = Try.of(() -> createServer(endpoints)).get();
    }

    public void start() {
        Try.run(this.ratpackServer::start);
    }

    public void stop() {
        Try.run(this.ratpackServer::stop);
    }

    private RatpackServer createServer(RestEndpoint... endpoints) throws Exception {
        return RatpackServer.of(server -> createEmptyServer(server)
                .handlers(chain -> {
                    DirectFormClient directFormClient = new DirectFormClient(new MongoUsernamePasswordAuthenticator(new UserRepository()));

                    HeaderClient headerClient = new HeaderClient(new JwtAuthenticator(AppProps.instance().getJwtSalt()));
                    headerClient.setHeaderName(AppProps.instance().getAuthHeader());
                    headerClient.setPrefixHeader(AppProps.instance().getAuthPrefixHeader());

                    chain
                            .all(new CORSHandler())
                            .all(RatpackPac4j.authenticator("callback", directFormClient, headerClient))
                            .get("auth", ctx -> RatpackPac4j.login(ctx, DirectFormClient.class).then(p -> {
                                final JwtGenerator generator = new JwtGenerator(AppProps.instance().getJwtSalt());
                                final String token = generator.generate(p);
                                ctx.render("{" + "\"token\":" + "\"" + token + "\"}");
                            }))
                            .post("logout", logout -> {
                                RatpackPac4j.logout(logout);
                                logout.render("Success");
                            })
                            .prefix("api", Security.auth(HeaderClient.class, endpoints));
                }));
    }

    private RatpackServerSpec createEmptyServer(RatpackServerSpec server) throws URISyntaxException {
        final Path currentRelativePath = Paths.get("").toAbsolutePath();

        return server
                .serverConfig(
                        ServerConfig
                                .builder()
                                .baseDir(currentRelativePath)
                                .publicAddress(new URI(AppProps.instance().getServerAddress()))
                                .port(AppProps.instance().getServerPort())
                                .threads(AppProps.instance().getServerThreads())
                )
                .registry(Guice.registry(b -> b
                                .bindInstance(ServerErrorHandler.class, (ctx, error) -> {
                                    error.printStackTrace(System.err);
                                    ctx.render("500 ERROR");
                                })
                                .bindInstance(ClientErrorHandler.class, (ctx, statusCode) -> {
                                    ctx.getResponse().status(statusCode);
                                    ctx.render(statusCode + " ERROR");
                                })
                                .module(SessionModule.class)
                                .add(configureJacksonMapping())
                        )
                );
    }

    private ObjectMapper configureJacksonMapping() {
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new JavaslangModule());
    }
}
