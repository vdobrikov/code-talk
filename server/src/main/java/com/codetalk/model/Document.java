package com.codetalk.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

@org.springframework.data.mongodb.core.mapping.Document
public class Document {
    @Id
    private String id;
    private String title;
    private String syntax;
    private String code = "";
    private Date created;
    private Date updated;

    public Document() {
    }

    public Document(String title, String syntax) {
        this.title = title;
        this.syntax = syntax;
    }

    public Document(String title, String syntax, String code) {
        this.title = title;
        this.syntax = syntax;
        this.code = code;
    }

    public Document copyFrom(Document another) {
        this.setTitle(another.getTitle());
        this.setSyntax(another.getSyntax());
        this.setCode(another.getCode());
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
