package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;

public class AckMsg implements Message {

    private final short OP=10;
    private int responseToMsg;//op code of the message called him
    private String line;

    public AckMsg(int OPsended, String line) {
        responseToMsg=OPsended;
        this.line=line;
    }

    @Override
    public Message execute(Connections connections, int ConnectionId) {
        return null;
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

    public int getResponseToMsg(){
        return responseToMsg;
    }

}