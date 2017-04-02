package com.dyadav.chirpntweet.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DirectMessages {

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("recipient")
    @Expose
    private User recipient;

    @SerializedName("recipient_screen_name")
    @Expose
    private String recipientScreenName;

    @SerializedName("sender")
    @Expose
    private User sender;

    @SerializedName("sender_screen_name")
    @Expose
    private String senderScreenName;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("id")
    @Expose
    private Long id;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getRecipientScreenName() {
        return recipientScreenName;
    }

    public void setRecipientScreenName(String recipientScreenName) {
        this.recipientScreenName = recipientScreenName;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderScreenName() {
        return senderScreenName;
    }

    public void setSenderScreenName(String senderScreenName) {
        this.senderScreenName = senderScreenName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
