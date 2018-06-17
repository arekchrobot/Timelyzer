package pl.ark.chr.timelyzer.filters;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.pac4j.RatpackPac4j;

public class SameUserAccessHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        if(ctx.getRequest().getMethod().isOptions()) {
            ctx.next();
        } else {
            String username = ctx.getRequest()
                    .getQueryParams()
                    .get("username");
            RatpackPac4j.userProfile(ctx)
                    .then(userProfile -> userProfile.ifPresent(up -> {
                        if (up.getId().equals(username)) {
                            ctx.next();
                        } else {
                            ctx.getResponse().status(403).send("403 ERROR");
                        }
                    }));
        }
    }
}
