package com.telusko.joblisting.repository;

import java.util.Arrays;

import com.mongodb.client.MongoClient;
import com.telusko.joblisting.model.Post;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.AggregateIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class SearchRepositoryImpl implements SearchRepository{

    @Autowired
    MongoClient mongoClient;
    @Value("${spring.data.mongodb.database}")
    private String dbname;
    @Autowired
    MongoConverter converter;
    @Override
    public List<Post> searchByText(String text) {
        System.out.println(dbname);
        List<Post> finalList = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(dbname);
        MongoCollection<org.bson.Document> collection = database.getCollection("JobPost");
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                        new Document("text", new Document("query",text).append("path", "techs"))),
                        new Document("$sort", new Document("exp", -1L)), new Document("$limit", 1L)));
        result.forEach( ele -> finalList.add(converter.read(Post.class,ele)));
        return finalList;
    }
}
