package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler> allConnections=new ConcurrentHashMap<>();//the Integer is the curr id

    public ConnectionsImpl(){}

    @Override
    public boolean send(int connectionId, T msg) {
        boolean output = false;
        ConnectionHandler ch = allConnections.get(connectionId);
        if (ch != null) { // if there is a connection handler with the given id
            ch.send(msg);
            output = true;
        }
        return output;
    }

    @Override
    public void broadcast(T msg) {
        for (Integer id : allConnections.keySet()) {
            allConnections.get(id).send(msg);
        }
    }

    public void register(int connectionId,ConnectionHandler handler) {
            if(handler != null)
                allConnections.put(connectionId,handler);
    }

    @Override
    public void disconnect(int connectionId) {
        allConnections.remove(connectionId);
    }
}
