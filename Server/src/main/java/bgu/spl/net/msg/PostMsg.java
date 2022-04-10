package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;
import java.util.LinkedList;
import java.util.List;

public class PostMsg implements Message {
    private String line;
    private String[] conetent;
    private List<String> taggedUsers = new LinkedList<>();
    final short OP = 5;
    public User me;

    public PostMsg(String lineToDecode) {
        line = lineToDecode;
        conetent = lineToDecode.substring(0, lineToDecode.length() - 1).split(" ");
    }

    @Override
    public Message execute(Connections connections, int ConnectionId) {
        split();

        User thisUser = DataBase.getInstance().getUserByConnectionId(ConnectionId);
        if (thisUser == null || !thisUser.isConnected()) {
            return new ErrorMsg(getOP());
        }
                me=thisUser;
                addPostToFollowers(connections,ConnectionId, thisUser);
                addPostForTaggedUsers(connections, ConnectionId, thisUser);
                DataBase.getInstance().addMessage(this);

        return new AckMsg(getOP(), "");
    }

    @Override
    public void split() {
        for (int i = 0; i < conetent.length; i++) {
            if (conetent[i].charAt(0) == '@') {
                taggedUsers.add(conetent[i].substring(1));//need add without the @
            }
        }
    }

    @Override
    public short getOP() {
        return OP;
    }

    @Override
    public String getLine() {
        return line;
    }


    public void addPostForTaggedUsers(Connections connections, int connectionId, User user) {
        for (String userName : taggedUsers) {
            if (!user.isFollowingMe(userName)) {//if (!user.ifUsersFollowMe(userName)) {
                User userToCheck = DataBase.getInstance().getUser(userName);
                if (userToCheck != null) {
                    String tmp = "" + (char) 1+ userName + (char)0+ conetent[conetent.length-1] + (char)0;
                    NotificationMsg notification = new NotificationMsg(tmp);
                    if (!userToCheck.isConnected()) {
                        userToCheck.addMassage(notification);
                    } else {
                        connections.send(userToCheck.getConnectionhandlerId(), notification);
                    }
                }
            }
        }
    }
    public void addPostToFollowers(Connections connections, int connectionId,User user) {
        for (String userName : user.getUsersThatFollowMe().keySet()) {
            if (user.isFollowingMe(userName)) {//if (!user.ifUsersFollowMe(userName)) {
                User userToCheck = DataBase.getInstance().getUser(userName);
                if (userToCheck != null) {
                    String tmp = "" + (char) 1  + userName + (char)0 + conetent[conetent.length-1] +(char)0 ;
                    NotificationMsg notification = new NotificationMsg(tmp);
                    if (!userToCheck.isConnected())
                        userToCheck.addMassage(notification);
                    else
                        connections.send(userToCheck.getConnectionhandlerId(), notification);

                }
            }
        }
    }


    public User getMe(){
        return me;
}

}
