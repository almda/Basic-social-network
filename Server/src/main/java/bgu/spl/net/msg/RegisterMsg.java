package bgu.spl.net.msg;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.info.DataBase;
import bgu.spl.net.srv.info.User;

/*
this class represents a register message we got from client
 */
public class RegisterMsg implements Message{
    private String line;
    final short OP=1;
    private String userName;
    private String password;

    public RegisterMsg(String lineToDecode) {
        line=lineToDecode;
    }


    @Override
    public Message execute(Connections connections, int ConnectionId) {
        split();
        User newUser = new User(userName,password,-1);
        boolean temp= DataBase.getInstance().addUser(newUser);
            if (temp) {
                return new AckMsg(getOP(), "");//registered successfully
            }
            return new ErrorMsg(getOP());//user already in data base
    }

    @Override
    public void split() {
        String [] details = line.split("\u0000");//davide the string with using "0"
        userName = details[0];
        password = details[1];
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
