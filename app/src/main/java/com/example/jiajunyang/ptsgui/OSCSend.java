package com.example.jiajunyang.ptsgui;

import android.util.Log;

import com.illposed.osc.*;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class OSCSend implements Runnable {
    String myIP; float x, y;
    OSCPortOut oscPortOut; // Needs to be an input option.
    int myPort = 5678;

    public OSCSend(String myIP, float x, float y){
        this.myIP = myIP;
        this.x = x;
        this.y = y;
        try{
            // Connect to IP and port
            this.oscPortOut  = new OSCPortOut(InetAddress.getByName(myIP), myPort);
        } catch(UnknownHostException e) {
            Log.d("OSCSendInitalisation", "OSC Port Out UnknownHoseException");
        } catch (SocketException e){
            // Report error
            Log.d("OSCSendInitalisation", "Socket exception error!");
        }
    }

    private void sendIndex(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add(x);
        sendBang.add(y);
        OSCMessage message = new OSCMessage("/trigger", sendBang);
        Log.d("OSC", "Send Coordinates");
        try{
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC", "Failed to send.");
        }
    }

    // Run the thread.
    @Override
    public void run(){
        if (oscPortOut != null) {
            sendIndex();}
        else{
            Log.d("OSC Action Error: ", "OSC Action Error."); // Need to change it to a Toast.
             }
    }
}
