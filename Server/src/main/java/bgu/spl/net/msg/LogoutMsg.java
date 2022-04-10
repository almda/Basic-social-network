package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;

public class LogoutMsg implements Message {

    private String line;
    final short OP=3;

    public LogoutMsg(String lineToDecode) {
        line=lineToDecode;
    }


    @Override
    public Message execute(Connections connections, int ConnectionId) {
        User thisUser = DataBase.getInstance().getUserByConnectionId(ConnectionId);
                if (thisUser != null && thisUser.isConnected()) {
                    thisUser.disconnect();
                    return new AckMsg(getOP(), "");
                }
                return new ErrorMsg(getOP());
            }

    @Override
    public void split() {//no need, its only OPcode
    }

    @Override
    public short getOP() {
        return OP;
    }

    @Override
    public String getLine() {
        return line;
    }
    public void DoDisconnect(Connections connections, int ConnectionId){
        User thisUser = DataBase.getInstance().getUserByConnectionId(ConnectionId);
        thisUser.setConnectionhandlerId(-1);
        connections.disconnect(ConnectionId);
    }
}

