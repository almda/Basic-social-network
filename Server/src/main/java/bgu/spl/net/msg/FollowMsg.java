package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;
import java.util.LinkedList;

public class FollowMsg implements Message {
    private String line;
    final short OP=4;
    short NumOfUsers;
    private LinkedList<String> followList=new LinkedList<>();//users we need to do the follow/unfollow
    private boolean follow; //follow = true / unfollow =false

    public FollowMsg(String lineToDecode,short NumOfUsers) {//call split and add all names to UserNameList
        line=lineToDecode;
        this.NumOfUsers=NumOfUsers;
        follow=false;
    }


    @Override
    public Message execute(Connections connections, int ConnectionId) {
        split();
        User someUser = DataBase.getInstance().getUserByConnectionId(ConnectionId);
        String usersWhoSucceed = "";
        int allSucceed=0;
        if(follow==true) {
            for (String s:followList){//for every user we got in the string
                User toAdd = DataBase.getInstance().getUser(s);
                if (toAdd != null && someUser.isConnected() && !someUser.isFollowing(toAdd)){
                    allSucceed++;
                    someUser.addFollow(toAdd);
                    toAdd.getUsersThatFollowMe().put(someUser.getUsername(),someUser);
                    usersWhoSucceed=usersWhoSucceed+toAdd.getUsername()+'\0';
                }
            }
            if (allSucceed==0)
                return new ErrorMsg(getOP());
            else{
                byte [] NBy = shortToBytes((short)allSucceed);
                byte [] OPFOLLOW = shortToBytes(OP);
                String string_send = ""+(char)NBy[0] + (char)NBy[1] +usersWhoSucceed;
                return new AckMsg(getOP(),string_send);
            }
        }

        for (String s:followList){//for every user we got in the string
            User toRemove = DataBase.getInstance().getUser(s);
            if (toRemove != null && someUser.isConnected() && someUser.isFollowing(toRemove)){
                allSucceed++;
                someUser.removeFollow(toRemove);
                usersWhoSucceed=usersWhoSucceed+toRemove.getUsername()+'\0';
            }
        }
        if (allSucceed==0)
            return new ErrorMsg(getOP());
        else{//changed
            byte [] NByU = shortToBytes((short)allSucceed);//NUMOFUSERS
            byte [] OPUNFOLLOW = shortToBytes(OP);
            String string_send = ""+(char)NByU[0] + (char)NByU[1] +usersWhoSucceed;
            return new AckMsg(getOP(),string_send);
        }
    }

    @Override
    public void split() {
        follow=line.charAt(0)=='0';//if its 0 then follow = true
        String lineToDestroy = line;
        lineToDestroy = line.substring(3);//we don't need the first byte,we know it FOLLOW msg
        String [] details = lineToDestroy.split("\u0000");
        for(int i=0;i<details.length;i++) {
            followList.add(details[i]); //now it contains all the names
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

    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }


}
