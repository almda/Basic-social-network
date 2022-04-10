package bgu.spl.net.msg;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;

public class StatMsg implements Message {
    private String line;
    final short OP=8;
    private String userName;

    public StatMsg(String lineToDecode) {
        line=lineToDecode;
    }


    @Override
    public Message execute(Connections connections, int ConnectionId)
    {
        split();
        User thisUser = DataBase.getInstance().getUserByConnectionId(ConnectionId);
        User statMe = DataBase.getInstance().getUser(userName);
        if(thisUser==null || !thisUser.isConnected() || statMe==null)
            return new ErrorMsg(getOP());
        int nPosts = statMe.getNumOfPosts();
        int nFollowMe=statMe.getUsersThatFollowMe().size();
        int nIfollow=statMe.getUsersTofollow().size();
        byte [] NOP = shortToBytes((short)nPosts);
        byte [] NFM = shortToBytes((short)nFollowMe);
        byte [] NIF = shortToBytes((short)nIfollow);
        String RET =""+(char)NOP[0]+(char)NOP[1]+(char)NFM[0]+(char)NFM[1]+(char)NIF[0]+(char)NIF[1];
        return new AckMsg(getOP(),RET);
    }

    @Override
    public void split() {
        String [] details = line.split("\u0000");
        userName=details[0];
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
