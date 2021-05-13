package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User3HomeActivity extends AppCompatActivity {
    //private ArrayList<AccountInfo> myAccountBook = new ArrayList<AccountInfo>();
    //private DBHelper myDBHelper;

    private String PatientName, DoctorName, strength;
    private ImageButton trainbtn, reviewbtn;
    private TextView textView_date, textView_introduction;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference calendarRef;
    private DocumentReference stateRef;
    private DocumentReference dayRef;
    private int year,month,day;
    private String year_str,month_str,day_str;
    private Boolean boo_monday,boo_tuesday,boo_wednesday,boo_thursday,boo_friday,boo_saturday,boo_sunday;
    private Boolean[]boo_weekday={false,false,false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user3_home);

        //WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        //getWindow().setAttributes(params);

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");
        strength = getIntent().getStringExtra("strength");

        findObject();
        ani_title();

        //setInit();
        boo_monday=false;
        boo_tuesday=false;
        boo_wednesday=false;
        boo_thursday=false;
        boo_friday=false;
        boo_saturday=false;
        boo_sunday=false;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        String intro = "歡迎您 " + PatientName + "\n今天也來一起運動!";
        textView_date.setText(dateTime + "          ");
        textView_introduction.setText(intro);

        year = calendar.get(Calendar.YEAR);
        year_str=String.valueOf(year);

        month = calendar.get(Calendar.MONTH)+1;
        if (month<10){
            month_str="0"+String.valueOf(month);
        }
        else{
            month_str=String.valueOf(month);
        }
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day<10){
            day_str="0"+String.valueOf(day);
        }
        else{
            day_str=String.valueOf(day);
        }

        calendarRef=db.collection(DoctorName).document(PatientName).collection(year_str+"年"+month_str+"月").document("-1");
        calendarRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        ;
                    }
                    else {
                        //Toast.makeText(User3HomeActivity.this, "miss on document not exist", Toast.LENGTH_SHORT).show();
                        setMonth();
                    }
                }
                else {

                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                    setMonth();
                }
            }
        });

        buttonClickEvent();
    }

    public void setMonth(){
        Log.d("MissionActivity", "Enter set month");
        stateRef=db.collection(DoctorName).document(PatientName).collection("資料").document("基本資料");
        stateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Number score= (Number) document.get("量表分數");
                        boo_weekday[0]=document.getBoolean("週日");
                        boo_weekday[1]=document.getBoolean("週一");
                        boo_weekday[2]=document.getBoolean("週二");
                        boo_weekday[3]=document.getBoolean("週三");
                        boo_weekday[4]=document.getBoolean("週四");
                        boo_weekday[5]=document.getBoolean("週五");
                        boo_weekday[6]=document.getBoolean("週六");
                        Calendar ccalendar=Calendar.getInstance();
                        if(month==1)
                            ccalendar.set(Calendar.MONTH,Calendar.JANUARY);
                        else if(month==2)
                            ccalendar.set(Calendar.MONTH,Calendar.FEBRUARY);
                        else if(month==3)
                            ccalendar.set(Calendar.MONTH,Calendar.MARCH);
                        else if(month==4)
                            ccalendar.set(Calendar.MONTH,Calendar.APRIL);
                        else if(month==5)
                            ccalendar.set(Calendar.MONTH,Calendar.MAY);
                        else if(month==6)
                            ccalendar.set(Calendar.MONTH,Calendar.JUNE);
                        else if(month==7)
                            ccalendar.set(Calendar.MONTH,Calendar.JULY);
                        else if(month==8)
                            ccalendar.set(Calendar.MONTH,Calendar.AUGUST);
                        else if(month==9)
                            ccalendar.set(Calendar.MONTH,Calendar.SEPTEMBER);
                        else if(month==10)
                            ccalendar.set(Calendar.MONTH,Calendar.OCTOBER);
                        else if(month==11)
                            ccalendar.set(Calendar.MONTH,Calendar.NOVEMBER);
                        else if(month==12)
                            ccalendar.set(Calendar.MONTH,Calendar.DECEMBER);
                        int daysinmonth=ccalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        Log.d("User3",String.valueOf(daysinmonth));



                        WriteBatch batch=db.batch();
                        String setDayStr="";
                        String strDate="";
                        for(int i=1;i<=daysinmonth;i++){
                            setDayStr="0";

                            if(i<10){
                                setDayStr=setDayStr+String.valueOf(i);
                            }
                            else{
                                setDayStr=String.valueOf(i);
                            }
                            dayRef=db.collection(DoctorName).document(PatientName).collection(year_str+"年"+month_str+"月").document(setDayStr);
                            strDate = year_str+"-"+month_str+"-"+setDayStr;//"2013-03-08"// 定义日期字符串
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 定义日期格式
                            Date date = null;
                            try {
                                date = format.parse(strDate);// 将字符串转换为日期
                            } catch (ParseException e) {
                                Log.d("user3","day format is wrong"+strDate);
                            }

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
                            Log.d("user3",String.valueOf(week_index));
                            if(week_index<0){
                                week_index = 0;
                            }
                            if(boo_weekday[week_index]==true){
                                Log.d("user3",String.valueOf(week_index)+"false");
                                Map<String, Object> note = new HashMap<>();
                                note.put("動作1","大腿運動");
                                batch.set(dayRef,note);

                                batch.update(dayRef,"動作1","大腿運動","動作1_次數",0,"動作2","向上抬腳","動作2_次數",0,"動作3","側邊抬腿","動作3_次數",0,"動作個數",3,"完成度",0);
                            }

                        }
                        dayRef=db.collection(DoctorName).document(PatientName).collection(year_str+"年"+month_str+"月").document("-1");
                        Map<String, Object> note = new HashMap<>();
                        note.put("動作1","大腿運動");
                        batch.set(dayRef,note);
                        batch.commit().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(User3HomeActivity.this, "wrong with batch commit", Toast.LENGTH_SHORT).show();
                                Log.d("user3","wrong with batch commit");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("user3","success with batch commit");
                            }
                        });


                    }
                    else{
                        ;//Toast.makeText(User3HomeActivity.this, "doc not exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //Toast.makeText(User3HomeActivity.this, "error with staterEF", Toast.LENGTH_SHORT).show();
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void ani_title() {
        ObjectAnimator animator1;
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        animator1 = ObjectAnimator.ofFloat(linearLayout, "alpha", (float) 0.6, (float)0.1);
        animator1.setDuration(3000);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator animator2;
                animator2 = ObjectAnimator.ofFloat(linearLayout, "alpha", (float) 0.1, (float)0.6);
                animator2.setDuration(3000);
                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ani_title();
                    }
                });
                animator2.start();
            }
        });
        animator1.start();
    }

    public void setInit() {
        //myDBHelper = new DBHelper(this);
        //myAccountBook.addAll(myDBHelper.getTotalAccountInfo());
        //getAccountSource();
    }

    public void getAccountSource() {
//        ArrayList<String> totalListViewData=new ArrayList<String>();
        /*for (int index = 0; index < this.myAccountBook.size(); index++) {
            AccountInfo eachPersonContactInfo = this.myAccountBook.get(index);
            PatientName = eachPersonContactInfo.getAccount();
            DoctorName = eachPersonContactInfo.getDoctorName();

//            String eachListViewData="["+eachPersonContactInfo.getUserID()+"]"+"帳戶名 : "+eachPersonContactInfo.getAccount()+" "+"密碼 : "+eachPersonContactInfo.getDoctorName();
//            totalListViewData.add(eachListViewData);
        }*/
//        Toast.makeText(this, PatientName, Toast.LENGTH_LONG).show();
//        Toast.makeText(this, DoctorName, Toast.LENGTH_LONG).show();
//        return totalListViewData;
    }

    private void findObject() {
        textView_date = findViewById(R.id.textView_date);
        textView_introduction = findViewById(R.id.textView_introduction);
        trainbtn = findViewById(R.id.trainbtn);
        reviewbtn = findViewById(R.id.reviewbtn);
//        title = findViewById(R.id.textView_title);
//        background = findViewById(R.id.imageView_background0);
    }

    public  void buttonClickEvent() {
        trainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(User3HomeActivity.this, "test for btn", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent();
                    intent.setClass(User3HomeActivity.this, User7SelectActivity.class);
                    intent.putExtra("pName", PatientName);
                    intent.putExtra("dName", DoctorName);
                    intent.putExtra("strength", strength);

                    startActivity(intent);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(User3HomeActivity.this, User5ReviewActivity.class);
                intent.putExtra("pName", PatientName);
                intent.putExtra("dName", DoctorName);
                startActivity(intent);
            }
        });
    }
}

