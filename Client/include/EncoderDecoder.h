//
// Created by orisha@wincs.cs.bgu.ac.il on 12/26/18.
//

#ifndef BOOT_ECHO_CLIENT_ENCODERDECODER_H
#define BOOT_ECHO_CLIENT_ENCODERDECODER_H

#include <string>
#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <iostream>
#include <fstream>
using namespace std;
class EncoderDecoder {
private:
    //Function for Encode
    void CommandRegister(string& line,ConnectionHandler& handler);
    void CommandLogin(string& line,ConnectionHandler& handler);
    void CommandLogout(ConnectionHandler& handler);
    void CommandFollow(string& line,ConnectionHandler& handler);
    void CommandPost(string& line,ConnectionHandler& handler);
    void CommandPM(string& line,ConnectionHandler& handler);
    void CommandUserlist(string& line,ConnectionHandler& handler);
    void CommandStat(string& line,ConnectionHandler& handler);
    //function for Decode
    void CommandNotification(ConnectionHandler& handler);
    void CommandACK(ConnectionHandler& handler,bool &logout);
    void CommandError(ConnectionHandler& handler,bool &lock);
    //helper Function
    short bytesToShort(char* bytesArr);
    void addOpCode(short num, char *Abytes);

/////Values
    int space=1;//the \0 in msg
/////OpCodes:
    int OpREGISTER =1;
    int OpLOGIN =2;
    int OpLOGOUT =3;
    int OpFOLLOW =4;
    int OpPOST =5;
    int OpPM=6;
    int OpUSERLIST=7;
    int OpSTAT =8;
    int OpNotification =9;
    int OpACK=10;
    int OpError =11;

public:
    virtual ~EncoderDecoder();
    EncoderDecoder();
    void Encode(ConnectionHandler& handler,bool &lock,bool &logout);
    void Decode(ConnectionHandler& handler,bool &lock,bool &logout);



};


#endif //BOOT_ECHO_CLIENT_ENCODERDECODER_H
