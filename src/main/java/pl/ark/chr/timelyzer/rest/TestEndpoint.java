package pl.ark.chr.timelyzer.rest;

import ratpack.func.Action;
import ratpack.handling.Chain;

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
                            handler.render("Hello " + id);
                        })
                );
    }
}
