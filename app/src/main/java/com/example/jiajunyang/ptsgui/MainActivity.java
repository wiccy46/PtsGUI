package com.example.jiajunyang.ptsgui;

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


import android.util.Log;


import java.util.ArrayList;


// AppCompatActivity , DemoBase
public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ScatterChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Hellow world!");

        mChart = (ScatterChart) findViewById(R.id.scatter);
        mChart.setDescription("");

        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setMaxHighlightDistance(5f);

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

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 100) + 3;
            yVals1.add(new Entry(i, val));
        }

        System.out.println(yVals1);
        ScatterDataSet set1 = new ScatterDataSet(yVals1, "DS 1");
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        set1.setScatterShapeSize(4f);
        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        ScatterData data = new ScatterData(dataSets);
        mChart.setData(data);
        mChart.invalidate();

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

        System.out.println("Nothing!");
    }



//

}
