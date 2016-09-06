package com.example.jiajunyang.ptsgui;

import android.content.Context;
import android.os.Build;
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
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    private ScatterChart mChart;

    private SeekBar seekSigma, seekDt, seekR;

    public static String myIP = "192.168.178.30";
    private int nr;
    float x, y; // For touchView
    int s, t, r;

    private static ArrayList<Entry> mData = new ArrayList<Entry>();
    int listenPort = 7012;

    private float touchViewWidth; // Locate touchView dimension
    private float touchViewHeight;
    boolean validIP = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = (ScatterChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setMaxVisibleValueCount(0);
        mChart.setPinchZoom(true);
        mChart.getAxisRight().setEnabled(true);
        mChart.setMaxHighlightDistance(10f);
        final TextView textView = (TextView)findViewById(R.id.textView);
        final View touchView = findViewById(R.id.touchView); // listen for touch event;

        seekSigma = (SeekBar) findViewById(R.id.sigmaSeekBar) ;
        seekSigma.setOnSeekBarChangeListener(this);
        seekDt = (SeekBar) findViewById(R.id.dtSeekBar);
        seekDt.setOnSeekBarChangeListener(this);
        seekR = (SeekBar) findViewById(R.id.rSeekBar);
        seekR.setOnSeekBarChangeListener(this);




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
                        Thread trigger = new Thread(new OSCSend(myIP, "trigger", x, y, 0, 0, 0));
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
            OSCPortIn receiver = new OSCPortIn(listenPort);
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

    public void onReset(View view){
        mData.clear();
        mChart.invalidate();
    }


    // Print IP address here.
    public void onPrint(View view){
        String androidIP;
        androidIP = FindIP.getIPAddress(true);

        Toast.makeText(getApplicationContext(), "IP address: " + androidIP, Toast.LENGTH_LONG).show();



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
        set1.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        set1.setScatterShapeSize(8f);
        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        dataSets.add(set1); // add the datasets
        ScatterData data = new ScatterData(dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

    // On value selec, need to find the right index. But at the moment it is not correct.
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }

    public void enterIP(View view) {
        final IPAddressValidator ipvalidator = new IPAddressValidator();
        final EditText yourIP = (EditText) findViewById(R.id.enterIP);
        yourIP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do something
                    myIP = yourIP.getText().toString();
                    validIP = ipvalidator.validate(myIP);
                }
                if (validIP) {
                    Toast.makeText(getApplicationContext(), "New IP is " + myIP, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid IP, correct format,e.g. 192.168.0.1, 255.255.255.255.", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    // Update Slider.
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        s = seekSigma.getProgress();
        t = seekDt.getProgress();
        r = seekR.getProgress();
        Thread sliderUpdate = new Thread(new OSCSend(myIP, "sliders", 0, 0, s, t, r));
        sliderUpdate.start();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

}
