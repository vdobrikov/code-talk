package com.codetalk.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentMessage extends Message<String> {
    @JsonIgnore
    public static final String TYPE = "content";

    public ContentMessage() {
    }

    public ContentMessage(String data) {
        super(TYPE, data);
    }
}
