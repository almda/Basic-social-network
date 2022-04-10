package bgu.spl.net.srv;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private volatile boolean connected = true;
    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private Object lockSending;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        lockSending=new Object();
    }

    @Override
    public void run() {//this is a conversation with one client
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);//will be null until message is full
                if (nextMessage != null)
                    protocol.process(nextMessage);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }



    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T message) {
        synchronized (lockSending) {
            try { //(Socket sock = this.sock)
                out = new BufferedOutputStream(sock.getOutputStream());
                if (message != null) {
                    out.write(encdec.encode(message));
                    out.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
