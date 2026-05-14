package com.group_g.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//used by ReadJSON to check if the JSON files have already been uploaded to MongoDB, otherwise it would dupe the files in the DB
//learned and understood through LLM usage
@Document(collection = "seed_metadata")
public class SeedMetadata {
    @Id
    private String id;
    private int version;

    public SeedMetadata() {
    }

    public SeedMetadata(String id, int version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
