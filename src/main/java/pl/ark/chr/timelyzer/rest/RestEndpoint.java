package pl.ark.chr.timelyzer.rest;

import ratpack.func.Action;
import ratpack.handling.Chain;

/**
 * Created by Arek on 2017-06-18.
 */
public interface RestEndpoint {

    String getApiPrefix();

    Action<Chain> defineActions();
}
