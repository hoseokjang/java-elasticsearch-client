package com.elastictest.elastictest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClient {
    String servelUrl = "https://localhost:9200";
    String apiKey = "ejZoVUM0MEJsMEdHRTA5UHNCVnE6NzlLN1JpUW9STUtOeWFKUmcwVU9MZw==";


    org.elasticsearch.client.RestClient restClient = org.elasticsearch.client.RestClient
            .builder(HttpHost.create(servelUrl))
            .setDefaultHeaders(new Header[] {
                    new BasicHeader("Authorization", "Apikey" + apiKey)
            })
            .build();

    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper()
    );

    ElasticsearchClient esClient = new ElasticsearchClient(transport);

}
