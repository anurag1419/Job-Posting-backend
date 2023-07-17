package com.telusko.joblisting.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "JobPost")
public class Post {
    private String profile;
    private String desc;
    private int exp;
    @Id
    private int uniqueId;
    private String techs[];
    private String blogId;
}
