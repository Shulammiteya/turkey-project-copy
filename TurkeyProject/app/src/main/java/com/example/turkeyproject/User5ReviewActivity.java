package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User5ReviewActivity extends AppCompatActivity {
    private LineChart linechartMonth,linechartWeek;
    private BarChart barChart;
    private Button day_review,week_review,month_review,record_review;
    private TextView tv_title,tv_record;
    private String PatientName="";
    private String DoctorName="";
    private String putText="";
    private Spinner sp_day,sp_month,sp_year;
    private static final String KEY_NAME="count";
    private ArrayList<AccountInfo> myAccountBook = new ArrayList<AccountInfo>();
    //private DBHelper myDBHelper;
    private boolean failDay=false;
    private boolean failMonth=false;




    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference UserExerxiseRef;
    private DocumentReference UserExerxiseDayRef;
    private DocumentReference UserRecordDayRef;

    private List<Pair<String, Float>> list = new ArrayList<Pair<String, Float>>();

    private int dayDataCount;
    private float dayDataComplete;
    private List<Pair<String, Integer>> dayDataList = new ArrayList<Pair<String, Integer>>();

    private int year,month,day;
    private String month_str,day_str;

    private String check_year="";
    private String check_month="";
    private String check_day="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user5_review);
        Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);

        month = c.get(Calendar.MONTH)+1;
        if (month<10){
            month_str="0"+String.valueOf(month);
        }
        else{
            month_str=String.valueOf(month);
        }


        day = c.get(Calendar.DAY_OF_MONTH);
        if (day<10){
            day_str="0"+String.valueOf(day);
        }
        else{
            day_str=String.valueOf(day);
        }

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");
        setInitial();
        sp_year.setVisibility(View.INVISIBLE);
        sp_month.setVisibility(View.INVISIBLE);
        sp_day.setVisibility(View.INVISIBLE);
        sp_year.setEnabled(false);
        sp_month.setEnabled(false);
        sp_day.setEnabled(false);

        tv_title.setText("????????????????????????\n????????????2???!!");
        getMonthRecord();
        buttonClickEvent();
        spinnerSeletedEvent();

//        month_review.performClick();



//        Toast.makeText(this, String.valueOf(month)+"day: "+String.valueOf(day), Toast.LENGTH_SHORT).show();
    }
    private void getRecord(){
        UserRecordDayRef=db.collection(DoctorName).document(PatientName).collection(check_year+"???"+check_month+"???").document(check_day);
        UserRecordDayRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    tv_record.setText("");
                    putText="????????????\n";
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        if(!document.getId().equals("-1")) {
                            Number b = (Number) document.get("????????????");
                            dayDataCount = b.intValue();
                            String move;
                            String moveCount;
                            String move1;
                            int moveCount1;


                            for (int i = 1; i <= dayDataCount; i++) {
                                move = "??????" + String.valueOf(i);
                                moveCount = "??????" + String.valueOf(i) + "_??????";
                                move1 = document.getString(move);
                                Number c = (Number) document.get(moveCount);
                                moveCount1 = c.intValue();
                                putText = putText + move1 + ": " + String.valueOf(moveCount1) + "\n";
//                            dayDataList.add(new Pair<String, Integer>(move1, moveCount1));
                            }
                            Number d = (Number) document.get("?????????");
                            float a = d.floatValue();
                            putText = putText + "?????????: " + String.valueOf(a) + "\n";
                            tv_record.setText(putText);
                            tv_record.setTextSize(40);
                        }
                    }
                    else {
                        tv_record.setText("????????????\n?????????????????????");
                        tv_record.setTextSize(40);
//                        Toast.makeText(User5ReviewActivity.this, "miss day", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    tv_record.setText("???????????? ?????????????????????");
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void spinnerSeletedEvent() {

        String preYear=String.valueOf(year-1);
        String curYear=String.valueOf(year);
        final String[] years={"???",preYear,curYear};
        ArrayAdapter<String> yearlist=new ArrayAdapter<>(User5ReviewActivity.this, android.R.layout.simple_spinner_dropdown_item,years);
        sp_year.setAdapter(yearlist);
        final String[] months={"???","01","02","03","04","05","06","07","08","09","10","11","12"};
        ArrayAdapter<String> monthlist=new ArrayAdapter<>(User5ReviewActivity.this, android.R.layout.simple_spinner_dropdown_item,months);
        sp_month.setAdapter(monthlist);
        final String[] days={"???","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        ArrayAdapter<String> daylist=new ArrayAdapter<>(User5ReviewActivity.this, android.R.layout.simple_spinner_dropdown_item,days);
        sp_day.setAdapter(daylist);

        sp_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!days[position].equals("???")){
                    check_day=days[position];
//                    Toast.makeText(User5ReviewActivity.this, days[position], Toast.LENGTH_SHORT).show();
                    if((!check_month.equals(""))&&(!check_day.equals(""))&&(!check_year.equals(""))){
                        getRecord();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!months[position].equals("???")){
                    check_month=months[position];
//                    Toast.makeText(User5ReviewActivity.this, months[position], Toast.LENGTH_SHORT).show();
                    if((!check_month.equals(""))&&(!check_day.equals(""))&&(!check_year.equals(""))){
                        getRecord();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!years[position].equals("???")){
                    check_year=years[position];
//                    Toast.makeText(User5ReviewActivity.this, years[position], Toast.LENGTH_SHORT).show();
                    if((!check_month.equals(""))&&(!check_day.equals(""))&&(!check_year.equals(""))){
                        getRecord();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void getMonthRecord() {

        UserExerxiseRef=db.collection(DoctorName).document(PatientName).collection(String.valueOf(year)+"???"+month_str+"???");
        UserExerxiseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


//                List<Pair<String, Float>> list = new ArrayList<Pair<String, Float>>();
                if(task.isSuccessful()){
                    list.clear();
                    failMonth=false;
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String dName=document.getId();
                        if(!dName.equals("-1")) {
                            Number b = (Number) document.get("?????????");
                            float a = b.floatValue();
                            list.add(new Pair<String, Float>(dName, a));
                        }



                    }
//                    String aaa=String.valueOf(year)+"???"+String.valueOf(month)+"???";
//                    Log.d("ReviewActivity","list.size:"+list.size());
//                    Toast.makeText(User5ReviewActivity.this, aaa, Toast.LENGTH_SHORT).show();


                }
                else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                    failMonth=true;
                }
            }
        });
    }
    private void getDayRecord() {

        UserExerxiseDayRef=db.collection(DoctorName).document(PatientName).collection(String.valueOf(year)+"???"+month_str+"???").document(day_str);
        UserExerxiseDayRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    dayDataList.clear();
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        if(!document.getId().equals("-1")) {
                            Number b = (Number) document.get("????????????");
                            dayDataCount = b.intValue();
                            String move;
                            String moveCount;
                            String move1;
                            int moveCount1;
                            failDay = false;
                            for (int i = 1; i <= dayDataCount; i++) {
                                move = "??????" + String.valueOf(i);
                                moveCount = "??????" + String.valueOf(i) + "_??????";
                                move1 = document.getString(move);
                                Number c = (Number) document.get(moveCount);
                                moveCount1 = c.intValue();
                                dayDataList.add(new Pair<String, Integer>(move1, moveCount1));
                            }
                        }
                    }
                    else {
                        //Toast.makeText(User5ReviewActivity.this, "miss day", Toast.LENGTH_SHORT).show();
                        tv_record.setText("????????????");
                        failDay=true;
                    }

                }
                else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }






















    private void setInitial() {
        tv_record=findViewById(R.id.tv_record);
        record_review=findViewById(R.id.record_review);
        sp_day=findViewById(R.id.sp_day);
        sp_month=findViewById(R.id.sp_month);
        sp_year=findViewById(R.id.sp_year);
        tv_title=findViewById(R.id. tv_title);
        barChart =findViewById(R.id.barChart);
        linechartMonth=findViewById(R.id.lineChartmonth);
        linechartWeek=findViewById(R.id.lineChartweek);

        Description description = barChart.getDescription();
        description.setEnabled(false);
        description=linechartMonth.getDescription();
        description.setEnabled(false);
        description=linechartWeek.getDescription();
        description.setEnabled(false);


        barChart.setNoDataText("");
        barChart.setNoDataTextColor(Color.BLACK);
        linechartWeek.setNoDataText("");
        linechartWeek.setNoDataTextColor(Color.BLACK);
        linechartMonth.setNoDataText("");
        linechartMonth.setNoDataTextColor(Color.BLACK);
        ytext_day();
        ytext_month();
        ytext_week();


        day_review=findViewById(R.id.day_review);
        week_review=findViewById(R.id.week_review);
        month_review=findViewById(R.id.month_review);

    }
    public  void buttonClickEvent(){
        day_review.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                tv_record.setText("");
                sp_year.setVisibility(View.INVISIBLE);
                sp_month.setVisibility(View.INVISIBLE);
                sp_day.setVisibility(View.INVISIBLE);
                sp_year.setEnabled(false);
                sp_month.setEnabled(false);
                sp_day.setEnabled(false);
                tv_title.setText(month_str+"/"+day_str+"??????????????????");
                getDayRecord();
                linechartMonth.clear();
                linechartWeek.clear();
                barChart.clear();
                if(failDay==false) {

                    ArrayList<BarEntry> values2 = new ArrayList<>();
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("");
                    for (int j = 0; j < dayDataList.size(); j++) {
                        Pair<String, Integer> pair = dayDataList.get(j);
                        values2.add(new BarEntry(j + 1, pair.second));
                        arrayList.add(pair.first);
                    }
//                values2.add(new BarEntry(1,8));
//                values2.add(new BarEntry(2,10));
//                values2.add(new BarEntry(3,15));
//                values2.add(new BarEntry(4,8));
//                values2.add(new BarEntry(5,11));

                    daychart(values2, arrayList, dayDataCount);
                }





            }
        });
        week_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_record.setText("");
                sp_year.setVisibility(View.INVISIBLE);
                sp_month.setVisibility(View.INVISIBLE);
                sp_day.setVisibility(View.INVISIBLE);
                sp_year.setEnabled(false);
                sp_month.setEnabled(false);
                sp_day.setEnabled(false);

                linechartMonth.clear();
                linechartWeek.clear();
                barChart.clear();
                getMonthRecord();

                if(failMonth==false) {


                    tv_title.setText(month_str + "/" + String.valueOf(day - 6) + "-" + month_str + "/" + day_str + " ???????????????");

                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("");

                    ArrayList<Entry> values2 = new ArrayList<>();
                    int count = 0;
                    for (int j = 0; j < list.size(); j++) {
                        Pair<String, Float> pair = list.get(j);
                        if ((day - 7) < Integer.valueOf(pair.first).intValue() && Integer.valueOf(pair.first).intValue() <= day) {
                            count++;
                            values2.add(new Entry(count, pair.second));
                            arrayList.add(pair.first);

                        }

                    }

                    weekchart(values2, arrayList, count);
                }
                else {
                    tv_record.setText("error occur");


                }


            }
        });
        month_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_record.setText("");
                sp_year.setVisibility(View.INVISIBLE);
                sp_month.setVisibility(View.INVISIBLE);
                sp_day.setVisibility(View.INVISIBLE);
                sp_year.setEnabled(false);
                sp_month.setEnabled(false);
                sp_day.setEnabled(false);

                tv_title.setText(month_str+"??? ???????????????");
                linechartWeek.clear();
                linechartWeek.clear();
                barChart.clear();
                getMonthRecord();
                if(failMonth==false) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("");

                    ArrayList<Entry> values2 = new ArrayList<>();
                    for (int j = 0; j < list.size(); j++) {
                        Pair<String, Float> pair = list.get(j);
                        values2.add(new Entry(j + 1, pair.second));
                        arrayList.add(pair.first);
                    }
//                values2.add(new Entry(1,80));
//                values2.add(new Entry(2,95));
//                values2.add(new Entry(3,60));
//                values2.add(new Entry(4,80));
//                values2.add(new Entry(5,76));
//                values2.add(new Entry(6,20));
//                values2.add(new Entry(7,80));
//                values2.add(new Entry(8,95));
//                values2.add(new Entry(9,60));
//                values2.add(new Entry(10,80));
//                values2.add(new Entry(11,76));
//                values2.add(new Entry(12,20));
//                values2.add(new Entry(13,80));
//                values2.add(new Entry(14,95));
//                values2.add(new Entry(15,60));
//                values2.add(new Entry(16,80));
//                values2.add(new Entry(17,76));
//                values2.add(new Entry(18,20));
//                values2.add(new Entry(19,20));
//                values2.add(new Entry(20,80));
//                values2.add(new Entry(21,95));
//                values2.add(new Entry(22,60));
//                values2.add(new Entry(23,80));
//                values2.add(new Entry(24,76));
//                values2.add(new Entry(25,20));
//                ArrayList<String> arrayList =new ArrayList<>();
//                for(int i=0;i<26;i++){
//                    if(i==0){
//                        arrayList.add("");
//                    }else if(i>1) {
//                        arrayList.add(String.valueOf(i));
//                    }
//                }
                    int count = list.size();
                    monthchart(values2, arrayList, count);
                }
                else {
                    tv_record.setText("error occur");


                }

            }
        });
        record_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_title.setText("???????????????????????????");
                sp_year.setVisibility(View.VISIBLE);
                sp_month.setVisibility(View.VISIBLE);
                sp_day.setVisibility(View.VISIBLE);
                sp_year.setEnabled(true);
                sp_month.setEnabled(true);
                sp_day.setEnabled(true);
                tv_record.setText(" ");
                linechartWeek.clear();
                linechartMonth.clear();
                barChart.clear();
                if(!putText.equals("")){
                    tv_record.setText(putText);
                }

            }
        });
    }

    public void daychart(ArrayList<BarEntry> values,ArrayList<String> orderList,int count){

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X?????????????????????(???????????????????????????????????????/??????????????????/???????????????????????????)
        xAxis.setTextColor(Color.GRAY);//X???????????????
        xAxis.setTextSize(10);//X???????????????
        xAxis.setLabelCount(count);//X???????????????
        xAxis.setSpaceMin(0.5f);//????????????????????????Y?????????
        xAxis.setSpaceMax(0.5f);//????????????????????????Y?????????
        xAxis.setDrawGridLines(false);//??????????????????????????????X????????? (????????????)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(orderList));


        BarDataSet set1=new BarDataSet(values,"?????????");

        set1.setColor(Color.parseColor("#7bbbe8"));


        set1.setHighlightEnabled(true);
        set1.setHighLightColor(Color.RED);
        set1.setValueTextSize(20);

        ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };
        set1.setValueFormatter(vf);
        BMV bmv=new BMV(this,orderList);
        bmv.setChartView(barChart);
        barChart.setMarker(bmv);




        BarData data=new BarData(set1);
        barChart.setData(data);
        barChart.invalidate();
//        textall(values);
    }
    public void monthchart(ArrayList<Entry> values,ArrayList<String> orderList,int count){

        XAxis xAxis = linechartMonth.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X?????????????????????(???????????????????????????????????????/??????????????????/???????????????????????????)
        xAxis.setTextColor(Color.GRAY);//X???????????????
        xAxis.setTextSize(10);//X???????????????
        xAxis.setLabelCount(count);//X???????????????
        xAxis.setSpaceMin(0.5f);//????????????????????????Y?????????
        xAxis.setSpaceMax(0.5f);//????????????????????????Y?????????
        xAxis.setDrawGridLines(false);//??????????????????????????????X????????? (????????????)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(orderList));


        LineDataSet set1=new LineDataSet(values,"?????????");
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setColor(Color.BLUE);
        set1.setLineWidth(1.5f);//??????
        set1.setCircleRadius(4);
        set1.enableDashedHighlightLine(5,5,0);

        set1.setHighlightEnabled(true);
        set1.setHighLightColor(Color.RED);
        set1.setValueTextSize(12);
        set1.setDrawFilled(false);

        LineData data=new LineData(set1);
        linechartMonth.setData(data);

        DMV dmv=new DMV(this,orderList);
        dmv.setChartView(linechartMonth);
        linechartMonth.setMarker(dmv);


        linechartMonth.invalidate();
//        textall(values);
    }
    public void weekchart(ArrayList<Entry> values,ArrayList<String> orderList,int count){

        XAxis xAxis = linechartWeek.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X?????????????????????(???????????????????????????????????????/??????????????????/???????????????????????????)
        xAxis.setTextColor(Color.GRAY);//X???????????????
        xAxis.setTextSize(10);//X???????????????
        xAxis.setLabelCount(count);//X???????????????
        xAxis.setSpaceMin(0.5f);//????????????????????????Y?????????
        xAxis.setSpaceMax(0.5f);//????????????????????????Y?????????
        xAxis.setDrawGridLines(false);//??????????????????????????????X????????? (????????????)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(orderList));


        LineDataSet set1=new LineDataSet(values,"?????????");
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setColor(Color.BLUE);
        set1.setLineWidth(1.5f);//??????
        set1.setCircleRadius(4);
        set1.enableDashedHighlightLine(5,5,0);

        set1.setHighlightEnabled(true);
        set1.setHighLightColor(Color.RED);
        set1.setValueTextSize(12);
        set1.setDrawFilled(false);

        LineData data=new LineData(set1);
        linechartWeek.setData(data);

        DMV dmv=new DMV(this,orderList);
        dmv.setChartView(linechartWeek);
        linechartWeek.setMarker(dmv);

        linechartWeek.invalidate();
//        textall(values);
    }
    private void ytext_day(){
        YAxis rightAxis = barChart.getAxisRight();//?????????????????????
        rightAxis.setEnabled(false);//???????????????Y???
        YAxis leftAxis = barChart.getAxisLeft();//?????????????????????
//        leftAxis.setLabelCount(4);//Y???????????????
        leftAxis.setTextSize(10);//X???????????????
        leftAxis.setTextColor(Color.GRAY);//Y???????????????
//        leftAxis.setTextSize(12);//Y???????????????
//
        leftAxis.setAxisMinimum(0);//Y??????????????????

//        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
    }
    private void ytext_week(){
        YAxis rightAxis = linechartWeek.getAxisRight();//?????????????????????
        rightAxis.setEnabled(false);//???????????????Y???
        YAxis leftAxis = linechartWeek.getAxisLeft();//?????????????????????
//        leftAxis.setLabelCount(4);//Y???????????????
        leftAxis.setTextSize(10);//X???????????????
        leftAxis.setTextColor(Color.GRAY);//Y???????????????
//        leftAxis.setTextSize(12);//Y???????????????
//
        leftAxis.setAxisMinimum(0);//Y??????????????????
        leftAxis.setAxisMaximum(100);//Y??????????????????
//        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
    }
    private void ytext_month(){
        YAxis rightAxis = linechartMonth.getAxisRight();//?????????????????????
        rightAxis.setEnabled(false);//???????????????Y???
        YAxis leftAxis = linechartMonth.getAxisLeft();//?????????????????????
//        leftAxis.setLabelCount(4);//Y???????????????
        leftAxis.setTextSize(10);//X???????????????
        leftAxis.setTextColor(Color.GRAY);//Y???????????????
//        leftAxis.setTextSize(12);//Y???????????????
//
        leftAxis.setAxisMinimum(0);//Y??????????????????
        leftAxis.setAxisMaximum(100);//Y??????????????????
//        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
    }

}

