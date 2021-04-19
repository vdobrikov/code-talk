package com.codetalk.web.websocket.model;

public class ConnectionDetails {
    private String documentId;
    private String userName;

    public ConnectionDetails() {
    }

    public ConnectionDetails(String documentId, String userName) {
        this.documentId = documentId;
        this.userName = userName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
