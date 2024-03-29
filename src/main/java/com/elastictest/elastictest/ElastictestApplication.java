package com.elastictest.elastictest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.elastictest.elastictest.Entity.Book;
//import com.elastictest.elastictest.Repository.SearchRepository;
import com.elastictest.elastictest.Entity.Book;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
@Slf4j
//@EnableElasticsearchRepositories(includeFilters = {
//		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SearchRepository.class)
//})
public class ElastictestApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ElastictestApplication.class, args);

		// url and security
		// String servelUrl = "https://localhost:9200";
		String apiKey = "SDRJU0VJMEIzZzc3ZWZzeVZseW86QmUtUjZYaHpRN2VFNVZFTmVpOU5vZw==";

		// verifying https with fingerprint or CA certificate
		// String fingerprint = "59c7bb3baf0d584122be686f290c498741c3ce131da9708140057c5c71b64fa4";
		File certFile = new File("D:/elasticsearch-8.11.4/config/certs/http_ca.crt");

		//SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint);
		SSLContext sslContext = TransportUtils.sslContextFromHttpCaCrt(certFile);


		BasicCredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("root", "111111"));


		// create the low-level client
		// apikey를 주석하고 certfile만 사용하니 indexing 성공
		org.elasticsearch.client.RestClient restClient = org.elasticsearch.client.RestClient
				.builder(new HttpHost("localhost", 9200, "https"))
//				.setDefaultHeaders(new Header[] {
//						new BasicHeader("Authorization", "Apikey" + apiKey)
//				})
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
//		esClient.indices().create(c -> c.index("logs_java"));

//		IndexResponse response = esClient.index(request);

//		System.out.println("Indexed with version " + response.version());



		// Reading Document by id
//		GetResponse<Book> response = esClient.get(g -> g
//				.index("book")
//				.id("1"),
//				Book.class
//		);

//		if (response.found()) {
//			Book book = response.source();
//			System.out.println("Book name " + book.getAuthor());
//		} else {
//			System.out.println("Not found");
//		}


		// Searching for Document
//		String searchText = "kim";
//
//		SearchResponse<Book> searchResponse = esClient.search(s -> s
//				.index("book")
//				.query(q -> q
//						.match(t -> t
//								.field("author")
//								.query(searchText)
//						)
//				),
//				Book.class
//		);
//
//		TotalHits totalHits = searchResponse.hits().total();
//
//		System.out.println("total hits: " + totalHits);
//
//		try {
//			List<Hit<Book>> hits = searchResponse.hits().hits();
//			for (Hit<Book> hit: hits) {
//				Book book = hit.source();
//				System.out.println("Found Book " + book.getAuthor() + ", score " + hit.score());
//			}
//
//		} catch (NullPointerException e) {
//			System.out.println(e);
//		}


		// Create a Script
//		esClient.putScript(r -> r
//				.id("book-template")
//				.script(s -> s
//						.lang("mustache")
//						.source("{\"query\":{\"match\":{\"{{field}}\":\"{{value}}\"}}}")
//				));

		// Use Search Template
		SearchTemplateResponse<Book> response = esClient.searchTemplate(r -> r
				.index("book")
				.id("book-template")
				.params("field", JsonData.of("author"))
				.params("value",JsonData.of("kim")),
				Book.class
		);

		List<Hit<Book>> hits = response.hits().hits();
		for (Hit<Book> hit: hits) {
			Book book = hit.source();
			System.out.println("book name : " + book.getName());
		}

		// Aggregation
		String searchText = "kim";

		Query query = MatchQuery.of(m -> m
				.field("author")
				.query(searchText)
		)._toQuery();

		SearchResponse<Void> response2 = esClient.search(b -> b
				.index("book")
				.size(0)
				.query(query)
				.aggregations("price-histogram", a -> a
						.histogram(h -> h
								.field("price")
								.interval(50.0)))
		, Void.class);

		List<HistogramBucket> buckets = response2.aggregations()
				.get("price-histogram")
				.histogram()
				.buckets().array();

		for (HistogramBucket bucket: buckets) {
			System.out.println("There are " + bucket.docCount() +
					" book under " + bucket.key());
		}


		// Bulk Indexing
		List<Book> bookList = fetchBooks();

		BulkRequest.Builder br = new BulkRequest.Builder();

		BulkIngester<Void> ingester = BulkIngester.of(b -> b
				.client(esClient)
				.maxOperations(100)
				.flushInterval(1, TimeUnit.SECONDS)
		);

		for (Book book: bookList) {
			ingester.add(op -> op
					.index(idx -> idx
							.index("bookList")
							.document(book)));
		}


	}

}
