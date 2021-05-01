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
public class Message<T> {
    private String type;
    private String userId;
    private T data;

    public Message() {
    }

    public Message(String type, T data) {
        this(type, null, data);
    }

    public Message(String type, String userId, T data) {
        this.type = type;
        this.userId = userId;
        this.data = data;
    }

    public Message(Message<T> another) {
        this(another.getType(), another.getUserId(), another.getData());
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", userId='" + userId + '\'' +
                ", data=" + data +
                '}';
    }
}
