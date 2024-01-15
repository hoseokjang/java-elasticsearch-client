package com.elastictest.elastictest;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.elastictest.elastictest.Repository.SearchRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SearchRepository.class)
})
public class ElastictestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElastictestApplication.class, args);


	}

}
