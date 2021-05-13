package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class User6ResultActivity extends AppCompatActivity {

    TextView[] txtResults;
    Button btnReturn;
    private DocumentReference UserExerxiseRef;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user6_result);

        findObj();
        setListener();
        showResult();
    }

    protected void findObj() {
        txtResults = new TextView[3];
        txtResults[0] = findViewById(R.id.txtFirstTime);
        txtResults[1] = findViewById(R.id.txtSecondTime);
        txtResults[2] = findViewById(R.id.txtThirdTime);
        btnReturn = findViewById(R.id.btnReturn);
    }


    protected void showResult() {
        Intent intent = getIntent();

        String PatientName = getIntent().getStringExtra("pName");
        String DoctorName = getIntent().getStringExtra("dName");
        String strength = getIntent().getStringExtra("strength");

        int[] resultValues = intent.getIntArrayExtra("resultArray");
        for(int i=0; i<3; ++i){
            txtResults[i].setText(Integer.toString(resultValues[i]) + " 次");
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        String dateTime = simpleDateFormat.format(calendar.getTime());


        int year = calendar.get(Calendar.YEAR);
        String year_str=String.valueOf(year);

        int month = calendar.get(Calendar.MONTH)+1;

        String month_str="";
        if (month<10){
            month_str="0"+String.valueOf(month);
        }
        else{
            month_str=String.valueOf(month);
        }
        String day_str="";
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day<10){
            day_str="0"+String.valueOf(day);
        }
        else{
            day_str=String.valueOf(day);
        }
        Map<String, Object> note1 = new HashMap<>();

        note1.put("動作1","大腿運動");
        note1.put("動作2","向上抬腳");
        note1.put("動作3","側邊抬腿");
        note1.put("動作1_次數",resultValues[0]);
        note1.put("動作2_次數",resultValues[1]);
        note1.put("動作3_次數",resultValues[2]);
        note1.put("動作個數",3);
        int count=0;
        double fcount=0;
        for(int i=0;i<3;i++){
            count+=resultValues[i];
        }
        if(strength.equals("強")){

            fcount=count/84.0;

        }
        else {
            fcount=count/44.0;
        }

        fcount=fcount*100;
        if(fcount>100){
            fcount=100;
        }
        float b=(float)fcount;
        note1.put("完成度",b);



        UserExerxiseRef=db.collection(DoctorName).document(PatientName).collection(year_str+"年"+month_str+"月").document(day_str);
        UserExerxiseRef.set(note1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("user6","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("user6","fail put on today record");
            }
        });

    }

    protected void setListener() {
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(User6ResultActivity.this, User3HomeActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }

}
