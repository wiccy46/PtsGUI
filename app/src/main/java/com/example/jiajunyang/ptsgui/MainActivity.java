package com.example.jiajunyang.ptsgui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;



public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ScatterChart mChart;
    public static String myIP = "129.70.149.23";
    private int nr;
    public OSCPortIn receiver;
    float x, y;
    ArrayList<Entry> mData = new ArrayList<Entry>();
    int listenPort = 7012;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (ScatterChart) findViewById(R.id.scatter);
        mChart.setDescription("");
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setMaxHighlightDistance(300f);

        // Enable Scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getAxisRight().setEnabled(true);

//          These two lines are to check the IP address of the emulator.
//        String ipad = getLocalIpAddress();
////        System.out.println("Your emulator IP is: " + ipad);

//        Thread populateData = new Thread(new ReceiveOSC());
//        populateData.start();


////
        try {
            receiver = new OSCPortIn(listenPort);
            OSCListener resetDataListener = new OSCListener() {
                public void acceptMessage(Date time, OSCMessage message) {
                    mData.clear();
                    System.out.println("Reset Data");
                }
            };

            OSCListener nrListener = new OSCListener() {
                public void acceptMessage(Date time, OSCMessage message) {
                    nr = Integer.parseInt(message.getArguments().get(0).toString());
                    System.out.println("row number is " + nr);
                }
            };


            OSCListener dataListener = new OSCListener() {
                public void acceptMessage(Date time, OSCMessage message) {
                    System.out.println("Receiving data");
                    int idx;
                    int val;
                    for (int i = 0; i < nr; i ++)
                    {
                        x = Float.parseFloat(message.getArguments().get(i*2).toString()) * 1000;
                        y = Float.parseFloat(message.getArguments().get(i * 2+ 1).toString())* 1000;
                        idx = (int) x;
                        val = (int) y;
                        mData.add(new Entry(idx, val));
                    }
                    System.out.println(mData);
                }
            };
            receiver.addListener("/resetData", resetDataListener);
            receiver.addListener("/getNr", nrListener);
            receiver.addListener("/getData", dataListener);
            receiver.startListening();
            System.out.println("Port: " + listenPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }



    }


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        System.out.println("IP is : " + inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("getLocalIpAddress Socket error");
        }
        return "a";
    }




    public void onPrint(View view){
        System.out.println("New data is : ");
        System.out.println(mData);
    }
    public void onReset(View view){
        mData.clear();
    }

    public void onPlot(View view) {
        System.out.println("plot data");
        System.out.println(mData);
        mData.clear();
        for (int i = 0; i < 200; i++) {
            float val = (float) (Math.random());
            float idx = (float) (Math.random());
            mData.add(new Entry((i - 5) * 100, val * 100));
        }
        ScatterDataSet set1 = new ScatterDataSet(mData, "DS 1");
        set1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        set1.setScatterShapeSize(10f);
        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        ScatterData data = new ScatterData(dataSets);
        System.out.println(mData);
        mChart.setData(data);
        mChart.invalidate();


    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
        // The actual index needs to be modified.
//        Thread trigger = new Thread(new OSCSend(myIP, (int) e.getX()));
//        trigger.start();
    }



    @Override
    public void onNothingSelected() {
    }

}
