#include <stdlib.h>
#include "../include/connectionHandler.h"
#include "../include/EncoderDecoder.h"
#include <iostream>
#include <fstream>
#include <thread>

using namespace std;
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
/**
 * Main method - Client starts connection to server by given Post and IP
 * @param argc - how many args - 2
 * @param argv - the args - first arg - IP ,second- PORT
 * @return 0
 */
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    //lock for sync between encoding and decoding
    bool lock;
    //boolean for if user logged out - changes by user input in the program
    bool logout=false;
    EncoderDecoder E;
    //Encoder thread
    std::thread t1(&EncoderDecoder::Encode, &E, std::ref(connectionHandler),std::ref(lock),std::ref(logout));
    //Decoder thread
    std::thread t2(&EncoderDecoder::Decode, &E, std::ref(connectionHandler),std::ref(lock),std::ref(logout));
    //killing threads
    t1.join();
    t2.join();
    return 0;
}