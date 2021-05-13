package com.example.turkeyproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.Map;

public class DMV extends MarkerView {
    private TextView tv_X,tv_Y;
    private ArrayList<String>X_label;


    public DMV(Context context,ArrayList<String> x_label) {
        super(context, R.layout.markview);
        X_label=new ArrayList<>();
        X_label.addAll(x_label);
        tv_X=(TextView)findViewById(R.id.tv_X);
        tv_Y=(TextView)findViewById(R.id.tv_Y);
    }
    public void refreshContent(Entry e, Highlight highlight){
        int x=(int)e.getX();
        int y=(int)e.getY();
        tv_X.setText("日期: "+X_label.get(x));
//        tv_X.setText("日期: "+e.getX());
        tv_Y.setText("完成度: "+y+"%");
        super.refreshContent(e,highlight);
    }
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint p=new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.parseColor("#FFFFFF"));
        RectF rf =new RectF(0,0,320,160);
        canvas.drawRoundRect(rf,15,15,p);
    }
}
