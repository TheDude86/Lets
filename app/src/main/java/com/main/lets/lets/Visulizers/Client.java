package com.main.lets.lets.Visulizers;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Joe on 5/29/2016.
 */
public abstract class Client {
    protected static AsyncHttpClient client = new AsyncHttpClient();
    protected static final String BASE_URL = "http://letsapi.azurewebsites.net/";

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public abstract void draw(org.json.JSONObject j);
}
