package com.codetalk.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNameMessage extends Message<String> {
    @JsonIgnore
    public static final String TYPE = "userName";

    public UserNameMessage() {
    }

    public UserNameMessage(String data) {
        super(TYPE, data);
    }
}
