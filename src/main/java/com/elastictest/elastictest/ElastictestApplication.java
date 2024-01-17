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
import java.io.File;
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

		// url and security
		String servelUrl = "https://localhost:9200";
		String apiKey = "SDRJU0VJMEIzZzc3ZWZzeVZseW86QmUtUjZYaHpRN2VFNVZFTmVpOU5vZw==";

		// verifying https with fingerprint or CA certificate
		// String fingerprint = "59c7bb3baf0d584122be686f290c498741c3ce131da9708140057c5c71b64fa4";
		File certFile = new File("D:/elasticsearch-8.11.4/config/certs/http_ca.crt");

		//SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);
		SSLContext sslContext = TransportUtils.sslContextFromHttpCaCrt(certFile);


		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("hs.jang", "123456"));


		String host = "localhost";
		Integer port = 9200;

		// create the low-level client
		org.elasticsearch.client.RestClient restClient = org.elasticsearch.client.RestClient
				.builder(new HttpHost(host, port, "https"))
				.setDefaultHeaders(new Header[] {
						new BasicHeader("Authorization", "Apikey" + apiKey)
				})
				.setHttpClientConfigCallback(hc -> hc
						.setSSLContext(sslContext)
						.setDefaultCredentialsProvider(provider))
				.build();

		// create the transport with a Jackson Mapper
		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper()
		);

		// raw Json data
		Reader input = new StringReader(
				"{'id': 1, '@timestamp': '2024-01-14T12:08:23', 'level': 'warn', 'message': 'Some log Message'}"
						.replace('\'','"')
				);


		IndexRequest<JsonData> request = IndexRequest.of(i -> i
				.index("logs_java")
				.withJson(input)
		);

		ElasticsearchClient esClient = new ElasticsearchClient(transport);

		// create index
//		esClient.indices().create(c -> c.index("logs"));

		IndexResponse response = esClient.index(request);

		System.out.println("Indexed with version " + response.version());

	}

}
