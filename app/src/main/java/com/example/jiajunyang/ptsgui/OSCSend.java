package com.example.jiajunyang.ptsgui;

import android.util.Log;

import com.illposed.osc.*;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by jiajunyang on 11/08/16.
 */

public class OSCSend implements Runnable {
    String myIP; int idx; OSCPortOut oscPortOut; // Needs to be an input option.
    int myPort = 5678;

    public OSCSend(String myIP, int idx){
        this.myIP = myIP;
        this.idx = idx;
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
        sendBang.add(idx);
        OSCMessage message = new OSCMessage("/trigger", sendBang);
        Log.d("OSC", "Data index: " + idx);
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
