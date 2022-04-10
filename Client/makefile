CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_thread -lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/EncoderDecoder.o bin/echoClient.o  $(LDFLAGS) 

BGSclient: bin/connectionHandler.o bin/echoClient.o bin/EncoderDecoder.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp
	
bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/EncoderDecoder.o src/EncoderDecoder.cpp
	
bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp



.PHONY: clean
clean:
	rm -f bin/*
