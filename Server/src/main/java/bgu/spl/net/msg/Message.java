package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;

public interface Message {
    Message execute(Connections connections, int ConnectionId); //the protocol of the message!
    void split();//we use this to "parse" the information and use it
    short getOP();//OP
    String getLine();
}
