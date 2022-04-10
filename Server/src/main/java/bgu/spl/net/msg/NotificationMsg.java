package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;

public class NotificationMsg implements Message {
    private String line;
    final short OP=9;

    private char type;//true if its PM and false if its Public
    private String PostingUser;
    private String Content;

    public NotificationMsg(String line) {
        this.line=line;
    }

    @Override
    public Message execute(Connections connections, int ConnectionId) {
        return null;
    }

    @Override
    public void split() {
        type = line.charAt(0);
        String[] split = line.split("\u0000");
        PostingUser = split[1];
        Content = split[2];
        Content = type + PostingUser+'\0'+Content+'\0';
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
