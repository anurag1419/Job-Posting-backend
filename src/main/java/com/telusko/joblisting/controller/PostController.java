package com.telusko.joblisting.controller;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.telusko.joblisting.PostRepository;
import com.telusko.joblisting.model.Post;
import com.telusko.joblisting.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.util.*;

@RestController
//@CrossOrigin(origins="*")
public class PostController {
    private final MongoTemplate mongoTemplate;
    PostController(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }
    @Autowired
    PostRepository repo;
    @Autowired
    SearchRepository search;
    @ApiIgnore
    @RequestMapping("/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping("/posts")
    public List<Post> getAllposts(){
        return repo.findAll();
    }
    @PostMapping("/addPosts")
    public Post addPost(@RequestBody Post post){
        HashMap<String,List<UpdateResult>>map = new HashMap<>();
        if(post.getBlogId()!=null){
            Query query = new Query();
            query.addCriteria(Criteria.where("uniqueId").is(post.getUniqueId()));
            UpdateDefinition update = new Update().set("profile", post.getProfile())
                    .set("desc", post.getDesc())
                    .set("exp", post.getExp())
                    .set("techs", post.getTechs())
                    .set("blogId", post.getBlogId());
            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
            return mongoTemplate.findAndModify(query,update,options,Post.class);
        }
       String blockID = createBlogId();
        post.setBlogId(blockID);
        repo.insert(post);
        ArrayList<Post> list = new ArrayList<>();
        list.add(post);
        return post;
    }

    @GetMapping("/getPost/{value}")
    public List<Post> getPost(@PathVariable String value){
        System.out.println(value);


       return search.searchByText(value);
    }

    @GetMapping("/getPostById/{userId}")
    public List<Post> getPostByUserID(@PathVariable Integer userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("uniqueId").is(userId));
        return mongoTemplate.find(query,Post.class);
    }

    @GetMapping("/delete/{blogId}")
    public HashMap<String,String> deletePost(@PathVariable String blogId){
        HashMap<String,String> map = new HashMap<>();
        Query query = new Query(Criteria.where("blogId").is(blogId));
         DeleteResult del =  mongoTemplate.remove(query,Post.class);
         if(del.getDeletedCount()>0){
             map.put("message","Job has been deleted");
            return map;
         }
         map.put("message","No job found");
         return map;
    }


    private String createBlogId(){
        return UUID.randomUUID().toString();
    }
}
