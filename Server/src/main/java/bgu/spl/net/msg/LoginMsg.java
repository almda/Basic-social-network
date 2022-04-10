package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoginMsg implements Message {
    private String line;
    final short OP = 2;
    private String userName;
    private String password;
    static int id=0;
    private ConcurrentLinkedQueue<Message> mssgs = new ConcurrentLinkedQueue<>();

    public LoginMsg(String lineToDecode) {
        line = lineToDecode;
    }


    @Override
    public Message execute(Connections connections, int ConnectionId) {
        split();
        if(DataBase.getInstance().getUser(userName)!=null&& DataBase.getInstance().getUserByConnectionId(ConnectionId)==null) {

                if (DataBase.getInstance().getUser(userName) != null && DataBase.getInstance().getUser(userName).getPassword().equals(password) &&
                        !DataBase.getInstance().getUser(userName).isConnected())//conditions
                {
                    DataBase.getInstance().getUser(userName).setId(++id);
                    DataBase.getInstance().getUser(userName).Connect();//user now connected
                    DataBase.getInstance().getUser(userName).setConnectionhandlerId(ConnectionId);//set CH id to user
                    return new AckMsg(getOP(), "");
                }
                return new ErrorMsg(getOP());
            //}
        }
        return new ErrorMsg(getOP());
    }


    @Override
    public void split() {
        String [] detailes = line.split("\u0000");
        userName = detailes[0];
        password = detailes[1];
    }


    @Override
    public short getOP() {
        return OP;
    }


    @Override
    public String getLine() {
        return line;
    }

    public void sendBeforeSeenMassges(Connections connections, int ConnectionId){
        mssgs=DataBase.getInstance().getUser(userName).getBeforeSeenMessages();
        if(!mssgs.isEmpty())
            for (Message msg: mssgs)
                connections.send(ConnectionId,msg);
    }

}