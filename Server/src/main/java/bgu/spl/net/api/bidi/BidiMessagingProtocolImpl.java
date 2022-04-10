package bgu.spl.net.api.bidi;
import bgu.spl.net.msg.*;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private boolean shouldTerminate=false;
    private int cID;
    private Connections conn;

    @Override
    public void start(int connectionId, Connections connections) {
        conn=connections;
        cID=connectionId;
    }


    @Override
    public void process(Message message) {
        Message output = null;
        output = message.execute(conn,cID);
        if(output != null) {
            conn.send(cID, output);
            if ((message.getOP() == 3) && (message instanceof AckMsg)){
                shouldTerminate = true;
            }
            if((message instanceof LoginMsg)&&(output instanceof AckMsg)){
                ((LoginMsg) message).sendBeforeSeenMassges(conn,cID);
            }
            if((message instanceof LogoutMsg)&& (output instanceof AckMsg)){
                ((LogoutMsg) message).DoDisconnect(conn,cID);
            }
        }
        else
            System.out.println("An error has occurred");
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
