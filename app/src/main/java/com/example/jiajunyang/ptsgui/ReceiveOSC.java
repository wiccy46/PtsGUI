package com.example.jiajunyang.ptsgui;

import android.util.Log;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import java.net.SocketException;
import java.util.Date;

public class ReceiveOSC implements Runnable {
    int myPort = 7012;
    OSCPortIn receiver;

    public ReceiveOSC() {
        System.out.println("Init OSC Receiver" );
        try {
            receiver = new OSCPortIn(myPort);
            OSCListener listener = new OSCListener() {
                public void acceptMessage(Date time, OSCMessage message) {
                    System.out.println(message.getArguments().get(0));
                    System.out.println(message.getArguments().get(1));
                }
            };
            receiver.addListener("/fromPython", listener);

        } catch (SocketException e) {
            Log.d("OSCSendInitalisation", "Socket exception error!");
        }
    }


        // Run the thread.
    @Override
    public void run(){
            if (receiver != null) {
                System.out.println("Listening to Port: " + myPort);
                receiver.startListening();
                }
            else{
                Log.d("OSC Action Error: ", "OSC Action Error."); // Need to change it to a Toast.
            }
        }
}
