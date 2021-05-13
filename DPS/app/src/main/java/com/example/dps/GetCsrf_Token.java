package com.example.dps;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;

public class GetCsrf_Token {
    String csrftoken;

    public GetCsrf_Token() throws IOException {
        // Create a new HttpClient and Post Header
        // Get the CSRF token
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.execute(new HttpGet("http://localhost:8000/"));
        CookieStore cookieStore = (CookieStore) httpClient.getCookieStore();
        List<HttpCookie> cookies =  cookieStore.getCookies();
        for (HttpCookie cookie: cookies) {
            if (cookie.getName().equals("XSRF-TOKEN")) {
                this.csrftoken = cookie.getValue();

            }
        }
    }


}
