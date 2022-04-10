package bgu.spl.net.srv.info;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.net.msg.Message;

public class DataBase {



    private ConcurrentHashMap<String,User> userToObj;//= new ConcurrentHashMap<>();//Username-->user
    private AtomicInteger how_Many_Msg = new AtomicInteger(0);
    private ConcurrentHashMap<Integer,Message> MassageDataBase = new ConcurrentHashMap<>();//saves the all messages
    private Object manulUserim;
    private Object manulMessagim;
    private LinkedList<String> RegisterdOrder=new LinkedList<>();


    private static class SingletonHolder {
        private static DataBase instance = new DataBase();
    }

    public static DataBase getInstance() {
        return SingletonHolder.instance;
    }

    private DataBase()
    {
        manulUserim = new Object();
        manulMessagim = new Object();
        userToObj = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, User> getUserToObj() {
        return userToObj;
    }


    public User getUser(String username)
    {
        if(userToObj.containsKey(username))
            return userToObj.get(username);
        return null;
    }

    public boolean addUser(User u)
    {
        synchronized (manulUserim)
        {
            if (!userToObj.containsKey(u.getUsername())) {
                userToObj.put(u.getUsername(), u);
                RegisterdOrder.addLast(u.getUsername());
                return true;
            }
        }
        return false;
    }

    /**
     * returns user by ConnectionId, if no one has ConnectionId returns null
     * @param ConnectionId
     * @return User with ConnectionId
     */
    public User getUserByConnectionId(int ConnectionId){
        for(Map.Entry<String,User> entry : userToObj.entrySet()){
            if(entry.getValue().getConnectionhandlerId()==ConnectionId)
                return entry.getValue();
        }
        return null;
    }

    public void addMessage(Message msg) {
        synchronized (manulMessagim) {
            MassageDataBase.put(how_Many_Msg.get(), msg);
            how_Many_Msg.incrementAndGet();
        }
    }

    public int numOfUsers (){
        return userToObj.size();
    }

    public LinkedList<String> getRegisterdOrder(){
        return RegisterdOrder;
    }

}
