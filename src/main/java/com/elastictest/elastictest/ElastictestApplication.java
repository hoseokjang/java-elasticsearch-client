package com.elastictest.elastictest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
//import com.elastictest.elastictest.Entity.Book;
//import com.elastictest.elastictest.Repository.SearchRepository;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


@SpringBootApplication
//@EnableElasticsearchRepositories(includeFilters = {
//		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SearchRepository.class)
//})
public class ElastictestApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ElastictestApplication.class, args);

		String servelUrl = "https://localhost:9200";
		String apiKey = "ejZoVUM0MEJsMEdHRTA5UHNCVnE6NzlLN1JpUW9STUtOeWFKUmcwVU9MZw==";
		String fingerprint = "59c7bb3baf0d584122be686f290c498741c3ce131da9708140057c5c71b64fa4";

		SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);

		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("hs.jang", "chang!6509"));


		org.elasticsearch.client.RestClient restClient = org.elasticsearch.client.RestClient
				.builder(HttpHost.create(servelUrl))
				.setDefaultHeaders(new Header[] {
						new BasicHeader("Authorization", "Apikey" + apiKey)
				})
				.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
						.setSSLContext(sslContext)
						.setDefaultCredentialsProvider(provider))
				.build();

		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper()
		);

		Reader input = new StringReader(
				"{'@timestamp': '2024-01-14T12:08:23', 'level': 'warn', 'message': 'Some log Message'}"
						.replace('\'','"'));


		IndexRequest<JsonData> request = IndexRequest.of(i -> i
				.index("logs_java")
				.withJson(input)
		);

		ElasticsearchClient esClient = new ElasticsearchClient(transport);

		// create index
//		esClient.indices().create(c -> c.index("logs"));

		IndexResponse response = esClient.index(request);

		System.out.println("성공");

	}

}
