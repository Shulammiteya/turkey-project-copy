package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;

public class Doc3SetStateActivity extends AppCompatActivity {
    private TextView textView_name, textView_gender, textView_height, textView_weight, textView_age, textView_history, textView_test;
    private RadioGroup radioGroup_strength;
    private Button setbtn;
    private CollectionReference patientsRef;
    private DocumentReference newStrengthRef;
    private DocumentReference StateRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String DoctorName;
    private String PatientName;
    private RadioButton btn_strong,btn_weak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_doc3_set_state);

        //WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        //getWindow().setAttributes(params);

        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");

        findObject();
        getState();
        buttonClickEvent();
    }

    private void findObject() {
        btn_strong=findViewById(R.id.btn_strong);
        btn_weak=findViewById(R.id.btn_weak);
        textView_name = findViewById(R.id.textView_name_ans);
//        textView_gender = findViewById(R.id.textView_gender_ans);
//        textView_height = findViewById(R.id.textView_height_ans);
//        textView_weight = findViewById(R.id.textView_weight_ans);
//        textView_age = findViewById(R.id.textView_age_ans);
        textView_history = findViewById(R.id.textView_history_ans);
        textView_test = findViewById(R.id.textView_test_ans);
        radioGroup_strength = findViewById(R.id.radioGroup_strength_ans);
        setbtn = findViewById(R.id.btn_set);
    }

    public void getState() {
        StateRef=db.collection(DoctorName).document(PatientName).collection("資料").document("基本資料");
        StateRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                textView_name.setText(PatientName);
                                Number score = (Number) document.get("量表分數");
                                textView_test.setText(String.valueOf(score.intValue()));
                                Boolean heartdisease = document.getBoolean("心血管疾病");
                                Boolean diabetes = document.getBoolean("糖尿病");
                                Boolean hypertension = document.getBoolean("高血壓");

                                if (heartdisease == true || diabetes == true || hypertension == true) {
                                    String putText = "";
                                    textView_history.setText("");
                                    if (heartdisease == true) {
                                        putText = putText + "心血管疾病\n";
                                    }
                                    if (hypertension == true) {
                                        putText = putText + "高血壓\n";
                                    }
                                    if (diabetes == true) {
                                        putText = putText + "糖尿病";
                                    }
                                    textView_history.setText(putText);
                                }
                            }
                            else {
                                textView_name.setText("無此病人");
//                                textView_age.setText("無");
//                                textView_gender.setText("無");
//                                textView_height.setText("無");
//                                textView_weight.setText("無");
                                textView_history.setText("無");
                                textView_test.setText("無");
                            }
                        }
                        else {
                            textView_name.setText("無此病人");
//                            textView_age.setText("無");
//                            textView_gender.setText("無");
//                            textView_height.setText("無");
//                            textView_weight.setText("無");
                            textView_history.setText("無");
                            textView_test.setText("無");
                            Log.d("MissionActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
        StateRef=db.collection(DoctorName).document(PatientName).collection("資料").document("訓練強度");
        StateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        String s=document.getString("強度");


                        if(s.equals("強")){
                            btn_strong.setChecked(true);
                            btn_weak.setChecked(false);
                        }
                        else if(s.equals("弱")){
                            btn_strong.setChecked(false);
                            btn_weak.setChecked(true);
                        }

                    }
                    else {
                        Log.d("doc3", "cannot get strength");

                    }
                }
                else {
                    Log.d("doc3", "cannot get strength task fail");
                }
            }
        });
    }

    public  void buttonClickEvent() {
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(User3HomeActivity.this, "test for btn", Toast.LENGTH_SHORT).show();
                try {
                    switch (radioGroup_strength.getCheckedRadioButtonId()) {
                        case R.id.btn_strong:
                            Map<String, Object> note1 = new HashMap<>();
                            note1.put("強度", "強");
                            newStrengthRef = db.collection(DoctorName).document(PatientName).collection("資料").document("訓練強度");
                            newStrengthRef.set(note1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ;//Toast.makeText(Doc3SetStateActivity.this, "success2", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(Doc3SetStateActivity.this, "errr22222222", Toast.LENGTH_SHORT).show();
                                            Log.d("user2", e.toString());
                                        }
                                    });
                            Toast.makeText(Doc3SetStateActivity.this, "已設定訓練強度為強", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.btn_weak:
                            Map<String, Object> note2 = new HashMap<>();
                            note2.put("強度", "弱");
                            newStrengthRef = db.collection(DoctorName).document(PatientName).collection("資料").document("訓練強度");
                            newStrengthRef.set(note2)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ;//Toast.makeText(Doc3SetStateActivity.this, "success2", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            ;//Toast.makeText(Doc3SetStateActivity.this, "errr22222222", Toast.LENGTH_SHORT).show();
                                            Log.d("user2", e.toString());
                                        }
                                    });
                            Toast.makeText(Doc3SetStateActivity.this, "已設定訓練強度為弱", Toast.LENGTH_LONG).show();
                            break;
                    }
//                    Intent intent = new Intent();
//                    intent.setClass(User3HomeActivity.this, User4TrainActivity.class);
//                    startActivity(intent);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}