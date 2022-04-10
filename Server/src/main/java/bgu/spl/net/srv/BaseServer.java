package bgu.spl.net.srv;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private ConnectionsImpl connections;
    AtomicInteger CurrId;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
        this.sock = null;
        connections = new ConnectionsImpl();
        CurrId = new AtomicInteger(0);
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
            System.out.println("Server started");

            this.sock = serverSock; //just to be able to close
            connections = new ConnectionsImpl<>(); //
            AtomicInteger id = new AtomicInteger(0);
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSock = serverSock.accept();
                BidiMessagingProtocol<T> p = protocolFactory.get();
                p.start(id.get(),connections);
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        p);
                connections.register(id.get(),handler);
                id.incrementAndGet();
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
        if (sock != null)
            sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}