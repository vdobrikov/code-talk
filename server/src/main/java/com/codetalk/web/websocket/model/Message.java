package com.codetalk.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true,
        defaultImpl = UnknownMessage.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectMessage.class, name = ConnectMessage.TYPE),
        @JsonSubTypes.Type(value = UserNameMessage.class, name = UserNameMessage.TYPE),
        @JsonSubTypes.Type(value = EditMessage.class, name = EditMessage.TYPE),
        @JsonSubTypes.Type(value = ContentMessage.class, name = ContentMessage.TYPE)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Message<T> {
    private String type;
    private T data;

    public Message() {
    }

    public Message(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
