//
// Created by orisha@wincs.cs.bgu.ac.il on 12/26/18.
//

#include "../include/EncoderDecoder.h"

EncoderDecoder::~EncoderDecoder() {}

/**
 * Encoder - catch the lines from the user - if the command matches the command in protocol it encodes it
 * by using the matches Command else it sends the string line.
 * @param connectionHandler
 * @param lock - lock for endoce decode - if logout waits for ACK.
 * @param logout - boolean for logout
 */
//////////////////////////////////////////////////////////////////////////////////////////
//                                      ENCODE
//////////////////////////////////////////////////////////////////////////////////////////
void EncoderDecoder::Encode(ConnectionHandler& connectionHandler,bool &lock,bool &logout){   //bool?//locker??

    logout = false;
    lock =false;

    std::string line="";//line that we read

    while (!logout){
        std::getline(std::cin,line);
        string command="";
        string action="";
        int delimiter=0;
        for(unsigned int i=0;i<line.size();i++){
            command+=line[i];
        }
        for(unsigned int j=0;j<command.size();j++) {
            if (command[j] != ' ') {
                action += command[j];//
            } else if(command[j] == ' '||j==line.size()){
                delimiter=j;
                j=command.size();
            }
        }
        if(action=="REGISTER"){
            line = line.substr(delimiter+1);
            CommandRegister(line,connectionHandler);
            //action register - encode...
        }
        else if(action=="LOGIN"){
            logout=false;//if logged in secong logout false
            line = line.substr(delimiter+1);
            CommandLogin(line,connectionHandler);
            //action LOGIN
        }
        else if(action=="LOGOUT"){
            //action LOGOUT
            CommandLogout(connectionHandler);
            //logout=true;
            lock=true;
        }
        else if(action=="FOLLOW"){
            line = line.substr(delimiter+1);
            CommandFollow(line,connectionHandler);
            //action follow
        }
        else if(action=="POST"){
            line = line.substr(delimiter+1);
            CommandPost(line, connectionHandler);
            //action POST
        }
        else if(action=="PM"){
            line = line.substr(delimiter+1);
            CommandPM(line,connectionHandler);
            //action PM
        }
        else if(action=="USERLIST"){
            line = line.substr(delimiter+1);
            CommandUserlist(line,connectionHandler);
            //action USERLIST
        }
        else if(action=="STAT"){
            line = line.substr(delimiter+1);
            CommandStat(line,connectionHandler);
            //action STAT
        } else{
            connectionHandler.sendLine(line);
        }
        while (lock){}

    }
}
/**
 * Encode REGISTER command
 * @param line - the rest of line after 'REGISTER'
 * @param handler - ConnectionHandler to send bytes to
 */
void EncoderDecoder::CommandRegister(string& line,ConnectionHandler& handler){
    //REGISTER Rick pain
    string userName="",password="";
    for(unsigned int i=0;i<line.size();i++){
        if(line[i]!=' ') {
            userName += line[i];
        }
        if(i==line.size()){
            cout<<"Regiser failed because userName and password wrriten togther || no password"<<endl;
        }
        if(line[i]==' '){
            for(unsigned int j=i+1;j<=line.size();j++){
                if(line[j]!='\n' && line[j]!=' ' && j!=line.size()) {
                    password += line[j];
                }
                if(line[j]==' '|| line[j]=='\n'||j==line.size()){
                    j=line.size();
                    i=line.size();
                }
            }
        }
    }

    if(userName.size()>0 && password.size()>0){
        int OPcode=2;
        int space=1;
        const char *username = userName.c_str();
        const char *userpassword = password.c_str();
        int MsgZize = OPcode+userName.size()+space+password.size()+space;
        char bytes[MsgZize];
        addOpCode(OpREGISTER, bytes);
        unsigned int i=0;
        for(;i<userName.length();i++)
            bytes[2+i] = username[i];
        i=i+2;
        bytes[i] = '\0';
        i++;
        unsigned int j=0;
        for(;j<password.length();j++)
            bytes[i+j] = userpassword[j];
        i=i+j;
        bytes[i] = '\0';
        bool happened = handler.sendBytes(bytes,MsgZize);
        if(!happened){
            cout<<"sending REGISTER to server failed\n"<<endl;
        }
    }
    else{
        cout<<"Register failed ! username:"<<userName<<", password: "<<password<<"\n";
    }

}
void EncoderDecoder::CommandLogin(string& line,ConnectionHandler& handler){
    string userName="",password="";

    for(unsigned int i=0;i<line.size();i++){
        if(line[i]!=' ') {
            userName += line[i];
        }
        if(i==line.size()){
            cout<<"LOGIN failed because userName and password wrriten togther || no password"<<endl;
        }
        if(line[i]==' '){
            for(unsigned int j=i+1;j<=line.size();j++){
                if(line[j]!='\n' && line[j]!=' ' && j!=line.size()) {
                    password += line[j];
                }
                if(line[j]==' '||line[j]=='\n'||j==line.size()){
                    j=line.size();
                    i=line.size();
                }
            }
        }
    }

    if(userName.size()>0 && password.size()>0){
        int OPcode=2;
        int space=1;
        const char *usernamee = userName.c_str();
        const char *userpassword = password.c_str();
        int MsgZize = OPcode+userName.size()+space+password.size()+space;
        char bytes[MsgZize];
        addOpCode(OpLOGIN, bytes);
        unsigned int i=0;
        for(;i<userName.length();i++)
            bytes[2+i] = usernamee[i];
        i=i+2;
        bytes[i] = '\0';
        i++;
        unsigned int j=0;
        for(;j<password.length();j++)
            bytes[i+j] = userpassword[j];
        i=i+j;
        bytes[i] = '\0';
        bool happened = handler.sendBytes(bytes,MsgZize);
        if(!happened){
            cout<<"sending LOGIN to server failed\n"<<endl;
        }
    }
    else{
        cout<<"LOGIN failed ! userNmae:"<<userName<<", password: "<<password<<"\n";
    }
}
void EncoderDecoder::CommandLogout(ConnectionHandler& handler){
    int MsgZize =2;
    char bytes[MsgZize];
    addOpCode(OpLOGOUT, bytes);
    bool happened = handler.sendBytes(bytes,MsgZize);
    if(!happened){
        cout<<"sending LOGOUT to server failed\n"<<endl;
    }
}


void EncoderDecoder::CommandFollow(string& line,ConnectionHandler& handler) {
    int HowManyUser = 0;
    unsigned int i=0;
    unsigned int j=0;

    string FollowOFUNFollowStr = "";//strings

    int NumberOfUsers = 0;//the real number of users
    for (i = 0; i <= line.size(); i++) {
        if (line[i] == ' ' || i == line.size()) {
            FollowOFUNFollowStr = line.substr(0, i);
            i = i + 1;
            break;
        }
    }
    string  NumOfUsers = "";

    for (j = i; j < line.size(); j++) {
        if (line[j] == ' ' || j == line.size()) {
            NumOfUsers = line.substr(i, j-i);
            j = j + 1;
            line = line.substr(j);
            break;
        }
    }
    NumberOfUsers = std::stoi(NumOfUsers);

    string UsersAarray[NumberOfUsers];
    for (unsigned int i = 0, k = 0; i <= line.size(); i++) {
        if (line[i] == ' ' || i == line.size()) {
            UsersAarray[HowManyUser] = line.substr(k, i-k);
            k = i + 1;
            HowManyUser=HowManyUser+1;
        }
    }

    if (FollowOFUNFollowStr.size() == 1 && NumOfUsers.size() > 0 && HowManyUser == NumberOfUsers) {
        char const *followORUn = FollowOFUNFollowStr.c_str(); //char array[1]
        int two =2;
        char arrOF2[2];
        addOpCode(NumberOfUsers,arrOF2);
        //MSG size starts from 5 - 2 OPcode , 1-FOLLOW/UN , 2- NUMOFUSERS
        int MsgZize = 5;
        for (int i = 0; i < NumberOfUsers; i++)
            MsgZize += UsersAarray[i].size() + 1;
        char bytes[MsgZize];
        addOpCode(OpFOLLOW, bytes);
        ////////////////////////////////////
        for (i = 0; i < 1; i++)
            bytes[two + i] = followORUn[i];
        //////////////////
        i = i + two;
        ///////////////
        for (j = 0; j < 2; j++)
            bytes[i+j] = arrOF2[j];
        i=i+j;
        for (int j = 0; j < NumberOfUsers; j++) {
            const char *namei = UsersAarray[j].c_str();
            for (unsigned int k = 0; k < UsersAarray[j].size(); k++, i++)
                bytes[i] = namei[k];
            bytes[i] = '\0';
            i=i+1;
        }
        bool happened = handler.sendBytes(bytes,MsgZize);
        if(!happened){
            cout<<"sending FOLLOW to server failed\n"<<endl;
        }
    }
}

void EncoderDecoder::CommandPost(string& line,ConnectionHandler& handler){
    const char* Content=line.c_str();
    if(line.size()==0){
        cout<<"Posting Failed because empty post - please write anotheR"<<endl;
        return;
    }
    int MsgZize = 2+line.size()+space;
    char bytes[MsgZize];
    addOpCode(OpPOST,bytes);
    for(unsigned int i=0;i<line.size();i++){
        bytes[i+2]=Content[i];
    }
    bytes[line.size()+2]='\0';
    bool happened = handler.sendBytes(bytes,MsgZize);
    if(!happened){
        cout<<"sending POST to server failed\n"<<endl;
    }

}

void EncoderDecoder::CommandPM(string& line,ConnectionHandler& handler){
    string username="";
    string Content="";
    int count0=0;
    int delimiter=0;
    for(unsigned int i=0;i<line.size();i++){
        if(line[i]!=' '){
            username+=line[i];
        }
        if(line[i]==' '){
            ++count0;
            delimiter=i+1;
            i=line.size();
        }
    }
    Content=line.substr(delimiter);

    const char* usernameo = username.c_str();
    const char* contento = Content.c_str();

    if(username.size()>0 && Content.size()>0) {
        int MsgZize = 2+username.size()+space+Content.size()+space;
        char bytes[MsgZize];
        addOpCode(OpPM,bytes);
        unsigned int i=0;
        for(;i<username.size();i++){
            bytes[i+2]=usernameo[i];
        }
        bytes[i+2]='\0';
        unsigned int j;
        for(j=0;j<Content.size();j++){
            bytes[j+i+3]=contento[j];
        }
        bytes[i+j+3]='\0';
        bool happened = handler.sendBytes(bytes,MsgZize);
        if(!happened){
            cout<<"sending PM to server failed\n"<<endl;
        }
    }
    else{
        cout<<"one of the fields in PM MSG is not Good please try agian"<<endl;
    }

}

void EncoderDecoder::CommandUserlist(string& line,ConnectionHandler& handler){
    int MsgZize=2;
    char bytes[MsgZize];
    addOpCode(OpUSERLIST,bytes);
    bool happened = handler.sendBytes(bytes,MsgZize);
    if(!happened){
        cout<<"sending USERLIST to server failed\n"<<endl;
    }
}
void EncoderDecoder::CommandStat(string& line,ConnectionHandler& handler){
    string username="";
    unsigned int i=0;
    for(;i<line.size();i++){
        if(line[i]!=' '){
            username+=line[i];
        }
    }
    if(username.size()>0){
        const char *user_name=username.c_str();
        int MsgZize=2+username.size()+space;
        char bytes[MsgZize];
        addOpCode(OpSTAT,bytes);
        for(i=0;i<username.length();i++){
            bytes[2+i] = user_name[i];
        }
        bytes[i+2] = '\0';
        bool happened = handler.sendBytes(bytes,MsgZize);
        if(!happened){
            cout<<"sending STAT to server failed\n"<<endl;
        }
    }
}

void EncoderDecoder::addOpCode(short num, char* Abytes)
{
    Abytes[0] = ((num >> 8) & 0xFF);
    Abytes[1] = (num & 0xFF);
}
short EncoderDecoder::bytesToShort(char* bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
//////////////////////////////////////////////////////////////////////////////////////////
//                                      DECODE
//////////////////////////////////////////////////////////////////////////////////////////
void EncoderDecoder::Decode(ConnectionHandler& handler,bool &lock,bool &logout){//loker???
    logout = false;
    while (!logout){
        int OPByteSize=2;
        char bytes[OPByteSize];
        handler.getBytes(bytes,OPByteSize);
        short opNum = bytesToShort(bytes);
        if(opNum>8){//OP for server to client
            if(opNum==OpNotification){//9

                CommandNotification(handler);
            }
            else if(opNum==OpACK){//10

                CommandACK(handler,logout);
            }
            else if(opNum==OpError){//11

                CommandError(handler,lock);
            }
            if(logout==true)
            {
                lock=false;
            }
            for(int i=0; i<OPByteSize;i++)
                bytes[i]=0;
        }

    }

}
void EncoderDecoder::CommandNotification(ConnectionHandler& handler){
    string user="";
    string text="";
    int count0=0;
    char onebyte[1];
    handler.getBytes(onebyte,1);
    if(onebyte[0]==0){
        cout<<"NOTIFICATION PM";
    }else{
        cout<<"NOTIFICATION Public";
    }
    while(count0<2){
        handler.getBytes(onebyte,1);
        if(onebyte[0]!=0 && count0==0){
            user+=onebyte[0];
        }
        else if(onebyte[0]!=0 && count0==1){
            text+=onebyte[0];
        }
        if(onebyte[0]==0){
            count0++;
        }
    }
    cout<<" "<<user<< " "<<text<<"\n"<<endl;
}

void EncoderDecoder::CommandACK(ConnectionHandler& handler,bool &logout){
    //ack 3 -terminate
    //ack 4 -follow -num of users - userlist
    //ack 7
    int OPByteSize = 2;
    char byte[OPByteSize];
    handler.getBytes(byte,OPByteSize);
    short num = bytesToShort(byte);
    if(num<7 && num!=4) {
        cout<< "ACK "<<num<<endl;
    }
    if(num==4|| num ==7) { //4-Follow Message //7-STAT
        cout<< "ACK "<<num<<" ";
        char byte_num[2];
        handler.getBytes(byte_num,2);
        int HowManyUsers = bytesToShort(byte_num);
        cout<<""<<HowManyUsers<<" ";
        string ListOfUsers="";
        short counter=0;
        char byte[1];
        for(handler.getBytes(byte,1);byte[0]!=0 || counter!=HowManyUsers;) {
            if (byte[0] == 0) {
                if (counter != HowManyUsers - 1)
                    ListOfUsers = ListOfUsers + " ";
                counter++;
            } else
                ListOfUsers = ListOfUsers + byte[0];
            if(counter != HowManyUsers)
                handler.getBytes(byte,1);
        }
        cout<<ListOfUsers<<endl;
    }
    else if(num==8) {
        cout<< "ACK "<<num<<" ";
        char byte_num_post[2];
        char byte_num_NFM[2];
        char byte_num_NIF[2];
        handler.getBytes(byte_num_post,2);
        handler.getBytes(byte_num_NFM,2);
        handler.getBytes(byte_num_NIF,2);
        cout<< bytesToShort(byte_num_post) <<" "<< bytesToShort(byte_num_NFM) <<" " << bytesToShort(byte_num_NIF) <<endl;
    }
    if(num==3){
        logout=true;
    }
}

void EncoderDecoder::CommandError(ConnectionHandler& handler,bool &lock){
    //ERROR - 2 bytes OPcode + 2 bytes MESSAGE OPcode
    char byte[2];
    handler.getBytes(byte,2);
    short n=bytesToShort(byte);
    cout<< "ERROR "<<n<<endl;
    if(n==3){
        lock=false;
    }
}

EncoderDecoder::EncoderDecoder() {

}
