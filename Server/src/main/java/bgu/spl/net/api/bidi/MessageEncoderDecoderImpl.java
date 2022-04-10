package bgu.spl.net.api.bidi;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.msg.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] buffer = new byte[1024]; // array of bytes
    public int len = 0; // how many bytes we write
    private short OP; // the opcode of the message
    int numofEncDec;
    private int zerosWeSaw; // count the decimeters
    //for follow only
    private byte [] NOUhelp=new byte[2];
    private short zerosForFollow=0;


    /**
     * @param nextByte the next byte to consider for the currently decoded
     * message
     * @return string that the array represents
     */
    @Override
    public Message decodeNextByte(byte nextByte) {
        pushByte(nextByte);//pushing the nextByte to the next empty spot in buffer
        if (len > 2 && nextByte == '\0') {//counts the 0 bytes after the op code
            zerosWeSaw++;
        }
        if (len == 2) {  // get the op code of the message
            OP = bytesToShort(buffer);
        }
        if(OP==1)
            return CreateRegister();
        else if(OP==2)
            return CreateLogin();
        else if(OP==3)
            return CreateLogout();
        else if(OP==4)
            return CreateFollow(nextByte);
        else if(OP==5)
            return CreatePost();
        else if(OP==6)
            return CreatePM();
        else if(OP==7)
            return CreateUserList();
        else if(OP==8)
            return CreateStats();
        return null;
    }

    /**
     * encode a message
     * @param message the message to encode
     * @return encode array of bytes
     */
    @Override
    public byte[] encode(Message message) {
        byte[] output = null;
        if(message.getOP()==9) {
            NotificationMsg notification = (NotificationMsg) message;
            output = notificationEncode(notification);
        }
        else if(message.getOP()==10) {
            AckMsg ack = (AckMsg) message;
            output = ackEncode(ack);
        }
        else if(message.getOP()==11) {
            ErrorMsg err = (ErrorMsg)message;
            output = errorEncode(err);
        }
        return output;
    }

    private byte[] errorEncode(ErrorMsg message) {
        byte[] theMs = new byte[4];
        byte[] OPC = shortToBytes((short)message.getOP());
        short someN = Short.parseShort(message.getLine());
        byte[] opcodeError = shortToBytes(someN);
        for (int y=0;y<2;y=y+1)
            theMs[y]=OPC[y];
        for(int i=0;i<2;i=i+1)
            theMs[i+2]=opcodeError[i];
        return theMs;
    }

    /**
     * encode the ack message
     * @param message - the message to encode
     * @return - encoded ack message
     */
    private byte[] ackEncode(AckMsg message) {
        String action = message.getLine();
        byte[] line = action.getBytes();
        byte[] sucOpCode = shortToBytes((short)message.getResponseToMsg());
        byte[] opppcode = shortToBytes((short)message.getOP());
        byte[] output = new byte[line.length + opppcode.length + sucOpCode.length];
        int index=0;
        for(int i=0;i<opppcode.length;i++,index++)
            output[index] = opppcode[i];
        for(int i=0;i<sucOpCode.length;i++,index++)
            output[index] = sucOpCode[i];
        for(int i=0;i<line.length;i++,index++)
            output[index] = line[i];
        return output;
    }

    /**
     * encode the notification message
     * @param message - the message to encode
     * @return - encoded notification message
     */
    private byte[] notificationEncode(NotificationMsg message) {
        String ac = message.getLine();
        byte[] currline = ac.getBytes();
        byte[] opcode = shortToBytes((short)message.getOP());
        byte[] output = new byte[currline.length + opcode.length];
        output[0] = opcode[0];
        output[1] = opcode[1];
        for(int t=2;t<output.length;t++)
            output[t] = currline[t-2];
        return output;
    }


    /**
     * translate byte array to short
     * @param byteArr the byte array
     * @return short number
     */
    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    /**
     * translate short number to byte array
     * @param num - the num to tanslate
     * @return - array of bytes
     */
    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private void pushByte(byte nextByte) {
        if (len >= buffer.length)
            buffer= Arrays.copyOf(buffer, len * 2);
        buffer[len++] = nextByte;
    }

    private String popString() { //This method does the actual decoding
        String result = new String(buffer, 2, len, StandardCharsets.UTF_8); //that's makes the bytes in buffer back to string using UTF8 method ,we pass it without OP
        buffer = new byte[1024]; // array of bytes
        len = 0; // how many bytes we write
        OP=0; // the opcode of the message
        zerosWeSaw=0; // count the decimeters
        NOUhelp=new byte[2];
        zerosForFollow=0;
        return result;
    }

    private Message CreateStats() {
        if (zerosWeSaw == 1)
            return new StatMsg(popString());
        return null;
    }

    private Message CreateUserList() {
        return new UserlistMsg(popString());
    }

    private Message CreatePM() {
        if ( zerosWeSaw == 2)
            return new PmMsg(popString());
        return null;
    }


    private Message CreatePost() {
        if ( zerosWeSaw == 1)
            return new PostMsg(popString());
        return null;
    }


    private Message CreateFollow(byte nb) {//counting 0 wont help here.. so use len
        if (len == 4 || len == 5)
            NOUhelp[len - 4] = nb;
        if (len < 5)
            zerosWeSaw = 0;
        if(len>4) {
            zerosForFollow = bytesToShort(NOUhelp);
        }
        if ( zerosForFollow == zerosWeSaw && len > 4) {
            short numToPass = bytesToShort(NOUhelp);
            return new FollowMsg(popString(), numToPass);
        }
        return null;
    }


    private Message CreateLogout() {
        return new LogoutMsg(popString());
    }

    private Message CreateLogin() {
        if (zerosWeSaw == 2)
            return new LoginMsg(popString());
        return null;
    }


    private Message CreateRegister() {
        if (zerosWeSaw == 2)//we create the msg only when we reached the end of the msg
            return new RegisterMsg(popString());
        return null;
    }
}
