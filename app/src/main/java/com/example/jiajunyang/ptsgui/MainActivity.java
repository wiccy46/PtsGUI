package com.example.jiajunyang.ptsgui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
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
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private ScatterChart mChart;
    public static String myIP = "129.70.148.19";
    private int nr;
    private OSCPortIn receiver;
    float x, y;

    private static ArrayList<Entry> mData = new ArrayList<Entry>();
    int listenPort = 7012;

    private float touchViewWidth; // Locate touchView dimension
    private float touchViewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (ScatterChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setMaxVisibleValueCount(0);
        mChart.setPinchZoom(true);
        mChart.getAxisRight().setEnabled(true);
        mChart.setMaxHighlightDistance(10f);
        final TextView textView = (TextView)findViewById(R.id.textView);
        final View touchView = findViewById(R.id.touchView); // listen for touch event;

        // Get the width and height of the control panel.
        touchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    touchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    touchView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                touchViewWidth = touchView.getWidth();
                touchViewHeight = touchView.getHeight();
            }


        });

        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pointerIndex = event.getActionIndex();
                switch( event.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        float x = (((event.getX(pointerIndex) / touchViewWidth) - 0.081807f) / 0.873709f - 0.5f) * 0.6f /0.5f;
                        float y = ((- (event.getY(pointerIndex)/(touchViewHeight)) + 0.04257559f) /0.91671634f + 0.5f) * 0.6f /0.5f;
                        float pressure = event.getPressure() * 5f;
                        if (pressure > 1.0f)
                        {
                            pressure = 1.0f;  // Hard clipping of the pressure.
                        }
                        DecimalFormat df = new DecimalFormat("#.00");
                        textView.setText("Touch coordinates : " +  " x: " +
                                String.valueOf(df.format(x)) + " y: " + String.valueOf(df.format(y)) + "\n" + "Pressure: " + String.valueOf(pressure) );
                        Thread trigger = new Thread(new OSCSend(myIP, x, y));
                        trigger.start();
                    }
                }
                return true;
            }
        });

//        mChart.getData().setDrawValues(false);
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisMaxValue(0.6f);

        xAxis.setAxisMinValue(-0.6f);
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMaxValue(0.6f);
        yAxis.setAxisMinValue(-0.6f);

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
                        x = Float.parseFloat(message.getArguments().get(i*2).toString()) ;
                        y = Float.parseFloat(message.getArguments().get(i * 2+ 1).toString());
                        mData.add(new Entry(x, y));
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
        mChart.invalidate();
    }

    public void onPlot(View view) {
        System.out.println("plot data");
        System.out.println(mData);

//        mData.clear();
//        for (int i = 0; i < 600; i++) {
//            float val = (float) (Math.random()) - 0.5f;
//            float idx = (float) (Math.random());
//            mData.add(new Entry(i    /600f - 0.5f, val ));
//        }

        ScatterDataSet set1 = new ScatterDataSet(mData, "Data");
        set1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
//        set1.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
//        set1.setScatterShapeHoleRadius(3f);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        set1.setScatterShapeSize(8f);
        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        dataSets.add(set1); // add the datasets
        ScatterData data = new ScatterData(dataSets);
//        data.setDrawValues(false);
//        data.setValueTextSize(8f);
//        data.setValueTextColor(Color.WHITE);
//        data.setHighlightCircleWidth(20f);
        mChart.setData(data);
        mChart.invalidate();
    }

    // On value selec, need to find the right index. But at the moment it is not correct.
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
