package bgu.spl.net.msg;

import bgu.spl.net.api.bidi.Connections;

public class ErrorMsg implements Message {
    private final short OP=11;
    private int responseToMsg;//op code of the message called him

    public ErrorMsg(int OPsended) {
        responseToMsg=OPsended;
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
        return Integer.toString(responseToMsg);
    }

}