//package com.elastictest.elastictest.Entity;
//
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//@Document(indexName = "books")
//public class Book {
//
//    @Field(name = "id", type = FieldType.Integer)
//    private Integer id;
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    @Field(name = "bookId", type = FieldType.Long)
//    private String bookId;
//
//    @Field(name = "name", type = FieldType.Text)
//    private String name;
//
//    @Field(name = "author", type = FieldType.Text)
//    private String author;
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    public void setBookId(String bookId) {
//        this.bookId = bookId;
//    }
//
//    public String getBookId() {
//        return bookId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getAuthor() {
//        return author;
//    }
//}
