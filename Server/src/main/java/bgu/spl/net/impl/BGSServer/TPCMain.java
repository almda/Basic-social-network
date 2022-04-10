package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[]args) {
        Server.threadPerClient(
                Integer.decode(args[0]),
                ()-> new BidiMessagingProtocolImpl(),
                ()-> new MessageEncoderDecoderImpl() {
                }
        ).serve();
    }
}
