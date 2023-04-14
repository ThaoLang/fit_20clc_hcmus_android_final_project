package com.example.fit_20clc_hcmus_android_final_project.data_struct;

public class Comment {
    public enum commentType{
        Plan,
        Location
    }
    private String commentId;
    private String accountId;
    private commentType type;
    private String targetId;    //planId or locationId
    private String textComment;
    private String imageLink;

    public Comment(String commentId, String accountId, commentType type, String targetId, String text_comment, String imageLink) {
        this.commentId = commentId;
        this.accountId = accountId;
        this.type = type;
        this.targetId = targetId;
        this.textComment = text_comment;
        this.imageLink = imageLink;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public commentType getType() {
        return type;
    }

    public void setType(commentType type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getText_comment() {
        return textComment;
    }

    public void setText_comment(String text_comment) {
        this.textComment = text_comment;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
