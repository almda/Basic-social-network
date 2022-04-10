package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;
import java.util.concurrent.ConcurrentHashMap;

public class UserlistMsg implements Message {
    private String line;
    final short OP=7;

    public UserlistMsg(String lineToDecode) {
        line=lineToDecode;
    }

    @Override
    public Message execute(Connections connections, int ConnectionId) {
        split();
        User thisUser = DataBase.getInstance().getUserByConnectionId(ConnectionId);
        if(thisUser!=null && thisUser.isConnected()){ //conditions
            ConcurrentHashMap<String,User> toReturn = DataBase.getInstance().getUserToObj();
            int NOU = toReturn.size();//NUMBER OF USERS
            byte [] NumOfUsers = shortToBytes((short)NOU);
            String UsernameList="";
            for(int i=0;i<DataBase.getInstance().getRegisterdOrder().size();i++) {
                UsernameList += DataBase.getInstance().getRegisterdOrder().get(i)+'\0';
            }
            String tmp = "" + (char)NumOfUsers[0] + (char)NumOfUsers[1] + UsernameList;
            return new AckMsg(getOP(),tmp);
        }
        return new ErrorMsg(getOP());
    }

    @Override
    public void split() {
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
