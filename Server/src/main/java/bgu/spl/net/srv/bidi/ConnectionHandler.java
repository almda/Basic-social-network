package bgu.spl.net.srv.bidi;//api was srv

import java.io.Closeable;
import java.io.IOException;

public interface ConnectionHandler<T> extends Closeable{

    void send(T msg) ;

}
