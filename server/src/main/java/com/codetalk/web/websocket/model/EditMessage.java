package com.codetalk.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

public class EditMessage extends Message<JsonNode> {
    @JsonIgnore
    public static final String TYPE = "edit";

    public EditMessage() {
    }

    public EditMessage(String type, JsonNode data) {
        super(type, data);
    }

    public EditMessage(JsonNode data) {
        super(TYPE, data);
    }
}
