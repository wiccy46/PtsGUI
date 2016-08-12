package com.example.jiajunyang.ptsgui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;


import android.util.Log;


import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;


// AppCompatActivity , DemoBase
public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ScatterChart mChart;
    public static String myIP = "192.168.178.20";
    private int nr; // Initialise the number of row

//    OSCPortIn receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mChart = (ScatterChart) findViewById(R.id.scatter);
        mChart.setDescription("");

        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setMaxHighlightDistance(20f);

        // Enable Scaling and dragging

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

//        mChart.setMaxVisibleValueCount(1000);
        mChart.setPinchZoom(true);

//        YAxis yl = mChart.getAxisLeft();
//        yl.setTypeface(mTfLight); // Comment this out for now.
//        yl.setAxisMinimum(0f);

        mChart.getAxisRight().setEnabled(false);

//        XAxis xl = mChart.getXAxis();

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 100) + 3;
            yVals.add(new Entry(i, val));
        }
        int listenPort = 5679;
//        Thread populateData = new Thread(new ReceiveOSC());
//        populateData.start();


        try {
            OSCPortIn receiver = new OSCPortIn(listenPort);
            System.out.println("Port: " + listenPort);
            OSCListener listener = new OSCListener() {
                public void acceptMessage(Date time, OSCMessage message) {
                    System.out.println("Message received!");
                }
            };
            receiver.addListener("/fromPython", listener);
            receiver.startListening();
        } catch (SocketException e) {
            Log.d("OSCSendInitalisation", "Socket exception error!");
        }



//        ArrayList<Entry> myData = new ArrayList<Entry>();
//        dataSet, nr = receivingData();
//
//        for (int i = 0; i < nr; i++) {
//            float val = (float) (Math.random() * 100) + 3;
//            myData.add(new Entry(i, val));
//        }

//        System.out.println(myData);
//        ScatterDataSet set1 = new ScatterDataSet(myData, "DS 1");
//        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
//        set1.setColor(ColorTemplate.COLORFUL_COLORS[1]);
//        set1.setScatterShapeSize(4f);
//        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1); // add the datasets
//        ScatterData data = new ScatterData(dataSets);
//        mChart.setData(data);
//        mChart.invalidate();


//
//
//        System.out.println(yVals);
//        ScatterDataSet set1 = new ScatterDataSet(yVals, "DS 1");
//        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
//        set1.setColor(ColorTemplate.COLORFUL_COLORS[1]);
//        set1.setScatterShapeSize(4f);
//        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1); // add the datasets
//        ScatterData data = new ScatterData(dataSets);
//        mChart.setData(data);
//        mChart.invalidate();


    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
        // The actual index needs to be modified.
        Thread trigger = new Thread(new OSCSend(myIP, (int) e.getX()));
        trigger.start();
    }

    @Override
    public void onNothingSelected() {

        System.out.println("Nothing!");
    }



}
