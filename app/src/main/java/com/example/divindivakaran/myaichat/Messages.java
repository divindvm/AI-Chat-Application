package com.example.divindivakaran.myaichat;

/**
 * Created by divin.divakaran on 9/8/2017.
 */

public class Messages {
    private  String message;
    private boolean seen;
    private long time;
    private String userid;
    private String userImage;
    private String userName;

    private String from;
    private String type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

//
//    public Messages(String from, String userImage, String message, String userName, long time) {
//        this.message = message;
//        this.time = time;
//        this.userImage = userImage;
//        this.userName = userName;
//        this.from = from;
//    }
//
//
//    public Messages(String userName, String userImage) {
//        this.userName = userName;
//        this.userImage = userImage;
//    }
//
//
//    public Messages(String message, boolean seen, String userName, String userImage, long time, String userid, String from, String type) {
//        this.message = message;
//        this.seen = seen;
//        this.userName = userName;
//        this.userImage = userImage;
//        this.time = time;
//        this.userid = userid;
//        this.from = from;
//        this.type = type;
//    }
//
//
//
//
//    public Messages(String message, long time, String from) {
//        this.message = message;
//        this.time = time;
//        this.from = from;
//    }


//
//    public Messages(String message) {
//        this.message = message;
//    }
//



//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getUserImage() {
//        return userImage;
//    }
//
//    public void setUserImage(String userImage) {
//        this.userImage = userImage;
//    }
//
//
//
//    public Messages(String message, boolean seen, long time, String type) {
//        this.message = message;
//        this.seen = seen;
//        this.time = time;
//        this.type = type;
//    }




//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public boolean getSeen() {
//        return seen;
//    }
//
//    public void setSeen(boolean seen) {
//        this.seen = seen;
//    }



//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public Messages(){
//
//    }


}
