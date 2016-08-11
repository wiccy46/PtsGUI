package com.example.jiajunyang.ptsgui;

import android.util.Log;

import com.illposed.osc.*;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by jiajunyang on 29/05/16.
 */

public class OSCSend implements Runnable {
    String myIP, prefix, userID, userName, run, nrStim;
    int myPort = 7000;
    String action; // action define what function it uses.
    OSCPortOut oscPortOut;
    int emoIndex;
    int degreeIndex;
    int count;
    String model;

    // Updating parameters and setup OSC port out.
    // Takes 5 parameters.
    public OSCSend(String myIP, String action, int emoIndex, int degreeIndex, int count, String prefix, String userID, String userName
            , String model, String run, int nrStim ){
        this.myIP = myIP;
        this.action = action;
        this.emoIndex = emoIndex;
        this.degreeIndex = degreeIndex;
        this.count = count;
        this.prefix = prefix;
        this.userID = userID;
        this.userName = userName;
        this.run = run;
        this.nrStim = Integer.toString(nrStim);
        this.model = model; // model is int from the radiogroup. Hence it needs to be convert to string: vocal and

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

    // This function is for the play button
    private void play(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add("bang");
        OSCMessage message = new OSCMessage("/play", sendBang);
        Log.d("OSCRun", "Play Sound.");
        try{
            // Send messages
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC2", "Failed to send.");
        }
    }

    private void next(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add(count);
        OSCMessage message = new OSCMessage("/next", sendBang);
        Log.d("OSCRun", "Next Sound.");
        try{
            // Send messages
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC2", "Failed to send.");
        }
    }

    private void save(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add("bang");
        OSCMessage message = new OSCMessage("/save", sendBang);
        Log.d("OSCRun", "Finish test and save file.");
        try{
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC2", "Failed to send.");
        }
    }

    private void emo(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add(emoIndex);
        OSCMessage message = new OSCMessage("/emo", sendBang);
        Log.d("OSCRun", "Emotion Index: " + emoIndex);
        try{
            // Send messages
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC2", "Failed to send.");
        }
    }

    private void degree(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add(degreeIndex);
        OSCMessage message = new OSCMessage("/degree", sendBang);
        Log.d("OSCRun", "Degree Index: " + degreeIndex);
        try{
            // Send messages
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC2", "Failed to send.");
        }
    }


    private void init(){
        ArrayList<Object> sendBang = new ArrayList<>();
        sendBang.add(prefix);
        sendBang.add(userID);
        sendBang.add(userName);
        sendBang.add(model);
        sendBang.add(run);
        sendBang.add(nrStim);
        OSCMessage message = new OSCMessage("/init", sendBang);

        try{
            // Send messages
            oscPortOut.send(message);
        } catch (Exception e){
            Log.d("OSC2", "Failed to send.");
        }
    }

    // Run the thread.
    @Override
    public void run(){
        if (oscPortOut != null){
            // Dont know why swich case doesnt work.
            if (action == "play"){
                play();
            }
            else if (action == "next") {
                next();
            }
            else if (action == "emo"){
                emo();
            }
            else if (action == "degree"){
                degree();
            }
            else if (action == "save"){
                save();
            } else if (action == "init"){
                init();
            }

            else{
                Log.d("OSC Action Error: ", "OSC Action Error."); // Need to change it to a Toast.
            }
        }
    }
}
