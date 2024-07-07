package com.infoworks.lab.client.spring;

import com.infoworks.lab.client.config.HttpClientConfig;
import org.apache.http.client.HttpClient;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateConfig {

    public static RestTemplate getTemplate() {
        HttpClient client = HttpClientConfig.defaultHttpClient();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        RestTemplate template = new RestTemplate(requestFactory);
        return template;
    }

}
