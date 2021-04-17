package com.codetalk.web.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

//TODO: Use @JsonSubTypes
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private String type;
    private JsonNode data;

    public Message() {
    }

    public Message(String type, JsonNode data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
