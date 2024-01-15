package com.elastictest.elastictest.Repository;

import com.elastictest.elastictest.Entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends ElasticsearchRepository<Book, Long> {
    List<Book> findBookList(Long bookId, String name);
}
