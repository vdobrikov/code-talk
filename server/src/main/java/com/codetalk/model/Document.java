package com.codetalk.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@org.springframework.data.mongodb.core.mapping.Document
public class Document {
    @Id
    private String id;
    private String title;
    private String syntax;
    private String content = "";
    @CreatedDate
    private Instant createdDate;
    @LastModifiedDate
    private Instant updatedDate;

    public Document() {
    }

    public Document(String title, String syntax) {
        this.title = title;
        this.syntax = syntax;
    }

    public Document(String title, String syntax, String content) {
        this.title = title;
        this.syntax = syntax;
        this.content = content;
    }

    public Document copyFrom(Document another) {
        this.setTitle(another.getTitle());
        this.setSyntax(another.getSyntax());
        this.setContent(another.getContent());
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", syntax='" + syntax + '\'' +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
