package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[]args) {
        Server.reactor( Integer.decode(args[1]).intValue(), Integer.decode(args[0]).intValue(),BidiMessagingProtocolImpl::new,MessageEncoderDecoderImpl::new).serve();
    }
}
