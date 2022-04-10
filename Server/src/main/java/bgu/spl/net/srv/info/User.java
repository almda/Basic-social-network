package bgu.spl.net.srv.info;

import bgu.spl.net.msg.Message;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private final String username; //username of user
    private final String password; //password of user
    private int id; //id of user
    private int connectionhandlerId;
    private static int countId=0;
    private boolean connected; //connect status
    private Timestamp t;
    private ConcurrentLinkedQueue<Message> beforeSeenMessages; //masseges that the user didn't see
    private ConcurrentHashMap<String,User> UsersTofollow;
    private ConcurrentHashMap<String,User> UsersThatFollowMe;

    private int NumOfPosts;

    /**
     * Consturctor
     * @param username - user name of user
     * @param password - password of user
     */
    public User(String username,String password,int connectionhandlerId){
        this.username=username;
        this.password=password;
        id=++countId;//we can drop it?
        this.connectionhandlerId=connectionhandlerId;
        connected=false;
        t =new Timestamp(0);
        beforeSeenMessages=new ConcurrentLinkedQueue<>();
        UsersTofollow=new ConcurrentHashMap<>();
        UsersThatFollowMe = new ConcurrentHashMap<>();
        NumOfPosts=0;
    }

    /**
     *
     * @return username of user
     */
    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    /**
     * set for id of user
     * @param id - id to change to
     */
    public void setId(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    /**
     * disConnect user
     */
    public void disconnect(){
        connected=false;
    }

    /**
     * connet user
     */
    public void Connect(){
        connected=true;
    }

    public void setConnectionhandlerId(int connectionhandlerId) {
        this.connectionhandlerId = connectionhandlerId;
    }

    public int getConnectionhandlerId(){
        return connectionhandlerId;
    }

    /**
     * @return connected status
     */
    public boolean isConnected(){
        return connected;
    }
    /**
     * set for time of last seen massage
     */
    public void setLastMassageTime(){
        t.setTime(System.currentTimeMillis());
    }

    /**
     * put massage to before Seen Messages of user. if user is disconnect we want to save his massages.
     * @param msg - massage to insert.
     */
    public void addMassage(Message msg){
        beforeSeenMessages.add(msg);
    }

    public ConcurrentHashMap<String,User> getUsersTofollow(){
        return UsersTofollow;
    }

    public ConcurrentHashMap<String,User> getUsersThatFollowMe(){
        return UsersThatFollowMe;
    }

    public User getUser(String s){
        return UsersTofollow.get(s);
    }

    public boolean isFollowing(User u){
        return UsersTofollow.containsKey(u.getUsername());
    }

    public boolean isFollowingMe(String username){
        return UsersThatFollowMe.containsKey(username);
    }

    public void addFollow(User u){
        if(!isFollowing(u))
            UsersTofollow.put(u.getUsername(), u);
    }

    public void removeFollow (User u){
        UsersTofollow.remove(u.getUsername());
    }

    public ConcurrentLinkedQueue<Message> getBeforeSeenMessages(){
        return beforeSeenMessages;
    }

    public int getNumOfPosts() {
        return NumOfPosts;
    }


}
