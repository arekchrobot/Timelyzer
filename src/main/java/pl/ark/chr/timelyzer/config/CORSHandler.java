package pl.ark.chr.timelyzer.config;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MutableHeaders;

public class CORSHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
        MutableHeaders headers = ctx.getResponse().getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept, Authorization");
        headers.add("Access-Control-Allow-Methods", "*");
        headers.add("Access-Control-Allow-Credentials", "true");

        ctx.next();
    }
}
