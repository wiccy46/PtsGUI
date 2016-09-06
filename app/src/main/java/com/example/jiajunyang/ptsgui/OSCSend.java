package com.example.jiajunyang.ptsgui;

import android.util.Log;

import com.illposed.osc.*;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class OSCSend implements Runnable {
    String myIP, target; float x, y;
    OSCPortOut oscPortOut; // Needs to be an input option.
    int myPort = 5678;

    float sigmaSlider, dtSlider, rSlider;


    public OSCSend(String myIP, String target, float x, float y, float s, float t, float r){
        this.myIP = myIP;
        this.target = target;
        this.x = x;
        this.y = y;
        this.sigmaSlider = s;
        this.dtSlider = t;
        this.rSlider = r;

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

    private void sendSlider(){
        // For now. I cant send 3 arguments, haven't figured out why.
        ArrayList<Object> sendBang = new ArrayList<>();
//        sendBang.add(sigmaSlider);
        sendBang.add(dtSlider);
        sendBang.add(rSlider);
        OSCMessage message = new OSCMessage("/sliders", sendBang);
//        Log.d("OSC", "Send Sliders information");
        try{
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC", "Failed to send.");
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
            if (target == "trigger"){
                sendIndex();
            }
            else if (target == "sliders"){
                sendSlider();
            }
        }
        else{
            Log.d("OSC Action Error: ", "OSC Action Error."); // Need to change it to a Toast.
             }
    }
}
