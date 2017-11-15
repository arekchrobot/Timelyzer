package pl.ark.chr.timelyzer.rest;

import org.bson.types.ObjectId;
import pl.ark.chr.timelyzer.persistence.Project;
import pl.ark.chr.timelyzer.util.JsonConverter;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.pac4j.RatpackPac4j;

import static ratpack.jackson.Jackson.json;

/**
 * Created by Arek on 2017-06-18.
 */
public class TestEndpoint implements RestEndpoint {

    @Override
    public String getApiPrefix() {
        return "test";
    }

    @Override
    public Action<Chain> defineActions() {
        return chain -> chain
                .prefix("get", ctx ->
                        ctx.get(":id", handler -> {
                            String id = handler.getPathTokens().get("id");
                            Project p = Project.builder().id(new ObjectId()).build();
                            RatpackPac4j.userProfile(handler)
                                    .then((userProfile -> handler.render("Hello " + id + " " + userProfile.get().getId())));
                        })
                ).post("post", handler ->
                        handler.getRequest().getBody().then(typedData -> {
                            System.out.println(typedData.getText());
                            Project project = JsonConverter.toPojo(typedData.getText(), Project.class);
                            System.out.println(project);
                            handler.render("OK");
                        })
                ).get("getJson", handler -> {
                    Project p = Project.builder().id(new ObjectId()).build();
                    handler.render(json(p));
                });
    }
}
