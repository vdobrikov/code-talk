package com.codetalk.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectMessage extends Message<ConnectionDetails> {
    @JsonIgnore
    public static final String TYPE = "connect";

    public ConnectMessage() {
    }

    public ConnectMessage(ConnectionDetails data) {
        super(TYPE, data);
    }
}
