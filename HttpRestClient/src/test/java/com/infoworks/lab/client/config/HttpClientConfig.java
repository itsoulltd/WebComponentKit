package com.infoworks.lab.client.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClientConfig {

    public static HttpClient defaultHttpClient() {
        //Config connection pooling:
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        //Request config:
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(1200) // timeout to get connection from pool
                .setSocketTimeout(500) // standard connection timeout
                .setConnectTimeout(1000) // standard connection timeout
                .build();
        //Create http-client:
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

}
