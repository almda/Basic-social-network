package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;

public class PmMsg implements Message {
    String userTosend;
    String Content;
    private String line;
    final short OP=6;

    public PmMsg(String lineToDecode) {
        line=lineToDecode;
    }

    @Override
    public Message execute(Connections connections, int ConnectionId) {
        split();

        String s = ""+ (char)0 + userTosend + (char)0 + Content + (char)0;//added
        NotificationMsg nm = new NotificationMsg(s);//added

        User thisUser  = DataBase.getInstance().getUserByConnectionId(ConnectionId);
        if(thisUser==null || !thisUser.isConnected() || DataBase.getInstance().getUser(userTosend)==null)
            return new ErrorMsg(getOP());
        User sendto = DataBase.getInstance().getUser(userTosend);
        if(sendto==null) return new ErrorMsg(getOP());
        if(DataBase.getInstance().getUser(userTosend).isConnected()) {
            connections.send(DataBase.getInstance().getUser(userTosend).getConnectionhandlerId(),nm);
        }

        else
            sendto.addMassage(this);
        return new AckMsg(getOP(),"");
    }

    @Override
    public void split() {
        String [] details = line.split("\u0000");
        userTosend = details[0];
        Content = details[1];
    }

    @Override
    public short getOP() {
        return OP;
    }

    @Override
    public String getLine() {
        return line;
    }
}
