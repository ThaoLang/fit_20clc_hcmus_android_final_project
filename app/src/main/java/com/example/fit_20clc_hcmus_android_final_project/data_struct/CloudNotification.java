package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CloudNotification implements Serializable {

    public static final String TOPIC_INVITE_FRIENDS = "NOTIFICATION_INVITE";
    public static final String TOPIC_PLAN_CHANGES = "NOTIFICATION_PLAN_CHANGES";
    public static final char SEPARATOR_CHAR_TOPIC_INVITE_FRIENDS = ':';
    public static final String CONTENT_INVITE_FRIEND = "This notification is an invitation for joining a trip! Have a look now!";



    private String title;
    private String sender_email;
    private String content;
    private String topic;
    private List<String> targets; //id of an object in notification collection in firebase database


    public CloudNotification()
    {
        title = "";
        sender_email = "";
        content = "";
        topic = "";
        targets = new ArrayList<>();
    }

    public CloudNotification(String title, String sender_email, String content, String topic, List<String> targets) {
        this.title = title;
        this.sender_email = sender_email;
        this.content = content;
        this.topic = topic;
        this.targets = targets;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender_email() {
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getTargets()
    {
        return targets;
    }

    public void setTargets(List<String> targets)
    {
        this.targets = targets;
    }

    public static byte[] fromObjectToBytes(CloudNotification object)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            baos.close();
            oos.close();
            return baos.toByteArray();

        } catch (IOException e) {
            Log.e("<<Serializable error>>", e.getMessage());
            return null;
        }
    }

    public CloudNotification fromBytesToObject(byte[] bytes)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            CloudNotification dest = (CloudNotification) ois.readObject();
            bais.close();
            ois.close();
            return dest;
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.e("<<Serializable error>>", e.getMessage());
            return null;
        }
    }

//    public static List<String> constructTargets_Topic_InviteFriends(@NotNull List<User> users, @NotNull List<Integer> roles)
//    {
//        List<String> result = new ArrayList<>();
//        for(int i=0; i< users.size(); i++)
//        {
//            String item = new StringBuilder().append(users.get(i).getUserEmail()).append(":").append(roles.get(i)).toString();
//            result.add(item);
//        }
//
//        return result;
//    }
//
//    public static List<String> constructTargets_Topic_Plan_changes(@NotNull Plan plan)
//    {
//        List<String> result = new ArrayList<>();
//        result.add(plan.getPlanId());
//        return result;
//    }
}
