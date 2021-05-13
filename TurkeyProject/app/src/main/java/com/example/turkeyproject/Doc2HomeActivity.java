package com.example.turkeyproject;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Doc2HomeActivity extends AppCompatActivity {

    //private ArrayList<AccountInfo> myAccountBook = new ArrayList<AccountInfo>();
    //private DBHelper myDBHelper;
    private String PatientName = "", DoctorName = "";
    private ImageButton statebtn, reviewbtn;
    private TextView textView_date, textView_introduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_doc2_home);

        //WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        //getWindow().setAttributes(params);

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");

        findObject();
        ani_title();
        //setInit();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        String intro = "歡迎您 " + DoctorName + "\n祝您有個美好的一天";
        textView_date.setText(dateTime + "          ");
        textView_introduction.setText(intro);

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");

        buttonClickEvent();
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

    public void getAccountSource(){
//        ArrayList<String>  = new ArrayList<String>();
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
        statebtn = findViewById(R.id.statebtn);
        reviewbtn = findViewById(R.id.reviewbtn);
//        title = findViewById(R.id.textView_title);
//        background = findViewById(R.id.imageView_background0);
    }

    public  void buttonClickEvent(){
        statebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(User3HomeActivity.this, "test for btn", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent();
                    intent.setClass(Doc2HomeActivity.this, Doc3SetStateActivity.class);
                    intent.putExtra("pName", PatientName);
                    intent.putExtra("dName", DoctorName);
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
                intent.setClass(Doc2HomeActivity.this, User5ReviewActivity.class);
                intent.putExtra("pName", PatientName);
                intent.putExtra("dName", DoctorName);
                startActivity(intent);
            }
        });
    }
}