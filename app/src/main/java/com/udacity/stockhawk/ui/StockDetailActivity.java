package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.primitives.Floats;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StockDetailActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.lineChart)
    LineChart lineChart;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.stock_overview)
    TextView overview;

    String stockFullName= null;
    ActionBar actionBar;
    //final String SYMBOL_KEY= "symbol";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        //Bundle extra= getIntent().getBundleExtra("symbol");
        //String symbol= extra.getString(SYMBOL_KEY);
        String symbol= getIntent().getStringExtra(getString(R.string.symbol_intent_key));

        actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(symbol);



        //build uri for symbol
        Uri uri = Contract.Quote.URI.buildUpon()
                .appendPath(symbol)
                .build();
        //select only the stock fullname and historry
        String projection []= {Contract.Quote.COLUMN_NAME, Contract.Quote.COLUMN_HISTORY};
        Cursor cus= getContentResolver().query(uri, projection, null, null, null);
        String s= null;

        if (cus != null){
            cus.moveToFirst();
            //for (int i = 0; i < cus.getCount(); i++) {
            s = cus.getString(cus.getColumnIndexOrThrow(Contract.Quote.COLUMN_HISTORY));
            stockFullName = cus.getString(cus.getColumnIndexOrThrow(Contract.Quote.COLUMN_NAME));
            /*    cus.moveToNext();
            }*/
            cus.close();
        }

        actionBar.setTitle(stockFullName);

        String split []= s.split("\n");
        //Toast.makeText(this, String.valueOf(split.length), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        float date []=new float[split.length];
        float close []=new float [split.length];
        //ArrayList<Float> close= new ArrayList<>();
        int i= 0;
        for (String t: split){
            String a []= t.split(", ");
            //date[i]= Float.parseFloat(a[0]);
            close[i]=Float.parseFloat(a[1]);

            i++;
        }


        drawLineChart(close, symbol);
        overview.setText(getString(R.string.large_text, stockFullName, String.valueOf(close[close.length-1]), String.valueOf(close[0]),
                String.valueOf(Floats.min(close)), String.valueOf(Floats.max(close))));
    }

    private void drawLineChart(float yValues[], String symbol){
        Description desc= new Description();
        desc.setText(stockFullName +" "+ this.getString(R.string.x_year_stock_trend));
        lineChart.setDescription(desc);
        ArrayList<Entry> yData= new ArrayList<>();
        int m= yValues.length-1;
        for (int i= 0; i<= m; i++){
            //reverse collections to make sure the latest closing price is on the right
            yData.add(new Entry(i, yValues[m-i]));
        }

        LineDataSet lineDataSet= new LineDataSet(yData, symbol);
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        LineData lineData= new LineData(lineDataSet);
        lineData.setValueTextSize(12f);
        lineData.setValueTextColor(Color.BLACK);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
