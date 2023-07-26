package com.example.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;

import javax.naming.NamingException;

public class HttpClientHelper {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);

    public HttpClientHelper() {} // no-arg constructor

    public HttpClient buildClient() {
        logger.info("Setting up HttpClient");

        HttpClient client = HttpClient.newBuilder()
                            .version(Version.HTTP_2)
                            .followRedirects(Redirect.NORMAL)
                            .connectTimeout(Duration.ofSeconds(120))
                            .build();

        logger.info("Finished setting up HttpClient");
        return client;
    }

    public HttpRequest buildRequest(URI endpoint, String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                        .uri(endpoint)
                        .header("authorization", String.format("Bearer %s", accessToken))
                        .header("Content-type", "application/json")
                        .header("Accept", "application/json")
                        .GET()
                        .build();
        return request;
    }

    /**
     *Converts a string to a URI, kills container if it is not a valid URI
     **/
    public URI buildUri(String url) {
        URI endpoint = null;
        try {
            endpoint = new URI(url);
        } catch (URISyntaxException e) {
            logger.error("A malformed URL was detected, killing the container because the API key is probably bad", e);
            logger.error("Email team");
            logger.warn("Killing the container");
        }
        return endpoint;
    }
}
