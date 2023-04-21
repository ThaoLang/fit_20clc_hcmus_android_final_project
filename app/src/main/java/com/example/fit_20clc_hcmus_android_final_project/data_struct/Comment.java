package com.example.fit_20clc_hcmus_android_final_project.data_struct;

public class Comment {

    private String commentId;
    private String accountEmail;
    private String type;
    private String targetPlanId;    //planId or locationId
    private String targetLocationId;    //planId or locationId
    private String textComment;
    private String imageLink;

    public Comment(){
        commentId="";
        accountEmail="";
        type="Plan";
        targetLocationId="None";
        targetPlanId="";
        textComment="";
        imageLink="None";
    }
    public String getTargetLocationId() {
        return targetLocationId;
    }

    public void setTargetLocationId(String targetLocationId) {
        this.targetLocationId = targetLocationId;
    }

    public Comment(String commentId, String accountId, String type, String targetId, String text_comment, String imageLink) {
        this.commentId = commentId;
        this.accountEmail = accountId;
        this.type = type;
        //this.targetId = targetId;
        this.textComment = text_comment;
        this.imageLink = imageLink;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountId) {
        this.accountEmail = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetPlanId() {
        return targetPlanId;
    }

    public void setTargetPlanId(String targetId) {
        this.targetPlanId = targetId;
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
