package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User2SurveyActivity extends AppCompatActivity {
    private String PatientName = "",  DoctorName = "";
    private DBHelper myDBHelper;
    //private ArrayList<AccountInfo> myAccountBook = new ArrayList<AccountInfo>();
    private DocumentReference UserRef;
    private TextView title, question;
    private Button questionConfirmBtn;
    private ImageView background;
    private CheckBox[] checkBox = new CheckBox[4];
    private ObjectAnimator[] animator = new ObjectAnimator[6];
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String[] questionStr = {
            "過去7天，是否經常到戶外走動，如：散步、購物等？",                   // 2
            "\n\n\t\t\t\t\t一天走路幾小時？",
            "過去7天，是否經常從事輕度活動，如：伸展運動、釣魚、唱歌等？",        // 3
            "平均一天從事輕度活動幾個小時？如：伸展運動、釣魚、唱歌等？",
            "過去7天，是否經常從事中度活動，如：太極拳、土風舞、騎單車等？",      // 4
            "平均一天從事中度活動幾個小時？如：太極拳、土風舞、騎單車等？",
            "過去7天，是否經常從事激烈活動，如：跑步、爬山、打球等？",            // 5
            "平均一天從事激烈活動幾個小時？如：跑步、爬山、打球等？",
            "過去7天，是否經常從事肌力或肌耐力活動，如：舉重、伏地挺身等？",      // 6
            "平均一天從事肌力或肌耐力活動幾個小時？如：舉重、伏地挺身等？",
            "過去7天，是否有做一些輕鬆的家務，如：洗碗、掃地、清掃灰塵？",        // 7
            "過去7天，是否有做一些粗重的家務，如：拖地、擦洗門窗？",             // 8
            "過去7天，是否有整修居家環境，如：粉刷室內、貼壁紙、水電工？",       // 9.1
            "過去7天，是否有整理前後院環境，如：清掃落葉、修剪樹枝？",           // 9.2
            "過去7天，是否有在戶外栽花種草？",                                 // 9.3
            "過去7天，是否有照顧配偶、孫子或其他家人？",                        // 9.4
            "您運動時身邊會有照顧者陪同嗎？",
            "\n\n\t\t請勾選您的過往病史。"
    };
    private String[][] checkBoxText = {
            {"不曾             ", "很少 (1-2天)", "有時 (3-4天)", "經常 (5-7天)"},
            {"少於 1 小時", "1 ~ 2 小時  ", "2 ~ 4 小時  ", "超過 4 小時"},
            {"無", "有"},
            {"心血管疾病", "高血壓        ", "糖尿病        ", "無                "}
    };
    private boolean[] disease = {false, false, false};
    private double[] scoreArr = new double[16];
    private double score = 0;
    private int qLength = 17;
    private int question_index = 0;
    private boolean[] isTimeChecked = {false, false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user2_survey);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);

        findObject();
        //setInit();
        ani_greeting();
        checkedEvent();
        btnClickEvent();

        myDBHelper = new DBHelper(this);
        PatientName = getIntent().getStringExtra("pName");
        DoctorName = getIntent().getStringExtra("dName");
    }

    public void setInit() {
        //myDBHelper = new DBHelper(this);
        //myAccountBook.addAll(myDBHelper.getTotalAccountInfo());
        //getAccountSource();
    }

    public void getAccountSource(){
/*        ArrayList<String> totalListViewData = new ArrayList<String>();
        for (int index = 0; index < this.myAccountBook.size(); index++){
            AccountInfo eachPersonContactInfo  = this.myAccountBook.get(index);
            PatientName = eachPersonContactInfo.getAccount();
            DoctorName = eachPersonContactInfo.getDoctorName();

//            String eachListViewData="["+eachPersonContactInfo.getUserID()+"]"+"帳戶名 : "+eachPersonContactInfo.getAccount()+" "+"密碼 : "+eachPersonContactInfo.getDoctorName();
//            totalListViewData.add(eachListViewData);
        }
//        Toast.makeText(this, PatientName, Toast.LENGTH_LONG).show();
//        Toast.makeText(this, DoctorName, Toast.LENGTH_LONG).show();
//        return totalListViewData;
 */
    }

    private void findObject() {
        question = findViewById(R.id.textView_question);
        questionConfirmBtn = findViewById(R.id.button_questionConfirmBtn);
        checkBox[0] = findViewById(R.id.checkBox_0);
        checkBox[1] = findViewById(R.id.checkBox_1);
        checkBox[2] = findViewById(R.id.checkBox_2);
        checkBox[3] = findViewById(R.id.checkBox_3);
        title = findViewById(R.id.textView_title);
        background = findViewById(R.id.imageView_background0);
    }

    private void ani_greeting() {
        animator[0] = ObjectAnimator.ofFloat(title, "translationY", 0, 150, -490);
        animator[0].setStartDelay(1750);
        animator[0].setDuration(2000);

        animator[1] = ObjectAnimator.ofFloat(title, "alpha", 1, 0);
        animator[1].setStartDelay(3500);
        animator[1].setDuration(750);

        animator[2] = ObjectAnimator.ofFloat(background, "alpha", 1, 0);
        animator[2].setStartDelay(2500);
        animator[2].setDuration(2000);

        animator[1].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ani_checkBox(750,0, 1);    // start survey
            }
        });

        animator[0].start();
        animator[1].start();
        animator[2].start();
    }

    private void ani_checkBox(int duration, int start, int end) {
        for(int i = 0; i < 4; i++) {
            if((question_index > 10 && question_index < qLength) || (question_index == 10 && end == 1)) {
                if(i == 1 || i == 3)
                    continue;
            }
            animator[i] = ObjectAnimator.ofFloat(checkBox[i], "alpha", start, end);
        }
        animator[4] = ObjectAnimator.ofFloat(question, "alpha", start, end);
        animator[5] = ObjectAnimator.ofFloat(questionConfirmBtn, "alpha", start, end);

        animator[4].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(question_index < 10 || question_index == 17) {
                    for (int i = 0; i < 4; i++) {
                        checkBox[i].setEnabled(true);
                        if(question_index < 10) {
                            checkBox[i].setText(checkBoxText[question_index % 2][i]);
                        }
                        else {
                            checkBox[i].setText(checkBoxText[3][i]);
                        }
                    }
                }
                else {
                    checkBox[0].setEnabled(true);
                    checkBox[1].setEnabled(false);
                    checkBox[2].setEnabled(true);
                    checkBox[3].setEnabled(false);
                    checkBox[0].setText(checkBoxText[2][0]);
                    checkBox[2].setText(checkBoxText[2][1]);
                }
                if (question.getAlpha() == 0) {
                    if(question_index == 18) {
                        ani_survey2();
                    }
                    else {
                        for (int i = 0; i < 4; i++) {
                            checkBox[i].setChecked(false);
                        }
                        questionConfirmBtn.setEnabled(false);
                        question.setText(questionStr[question_index]);
                        ani_checkBox(750, 0, 1);
                    }
                }
            }
        });

        for(int i = 0; i < 6; i++) {
            if((question_index > 10 && question_index < qLength) || (question_index == 10 && end == 1)) {
                if(i == 1 || i == 3)
                    continue;
            }
            animator[i].setStartDelay(duration);
            animator[i].setDuration(duration);
            animator[i].start();
        }
    }

    private void ani_survey2() {
        title.setText("請選擇您方便的運動時段");

        animator[0] = ObjectAnimator.ofFloat(title, "translationY", 0, 150, -200);
        animator[0].setDuration(4000);

        animator[1] = ObjectAnimator.ofFloat(title, "alpha", 1, 0);
        animator[1].setStartDelay(4500);
        animator[1].setDuration(500);

        animator[2] = ObjectAnimator.ofFloat(background, "alpha", 0, 1);
        animator[2].setDuration(2000);

        animator[3] = ObjectAnimator.ofFloat(title, "alpha", 0, 1);
        animator[3].setStartDelay(1000);
        animator[3].setDuration(2000);

        animator[1].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                calculateScore();
                openOptionsDialog();
            }
        });

        animator[0].start();
        animator[1].start();
        animator[2].start();
        animator[3].start();
    }

    private void calculateScore() {
        double[] scoreForEachAns_1 = {0, 1.5, 3.5, 6}, scoreForEachAns_2 = {0.5, 1.5, 3, 5};
        double[] weight = {20, 21, 23, 23, 30, 25, 25, 30, 36, 20, 35};
        int weightIndex = 0;

        for(int i = 0; i < 16; i++) {
            if(i < 10) {
                if(i % 2 == 0) {
                    continue;
                }
                else {
                    score += weight[weightIndex] * scoreForEachAns_1[(int)scoreArr[i-1]] * scoreForEachAns_2[(int)scoreArr[i]] / 7;
                    weightIndex++;
                }
            }
            else {
                score += weight[weightIndex] * (scoreArr[i]/2);
                weightIndex++;
            }
        }
        //Toast.makeText(User2SurveyActivity.this, "分數:" + Double.toString(score), Toast.LENGTH_LONG).show();
    }

    private void openOptionsDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        String[] timeStr = {
                "星期 一",
                "星期 二",
                "星期 三",
                "星期 四",
                "星期 五",
                "星期 六",
                "星期 日"
        };

        dialog.setTitle("請選擇您方便的運動時段")
                .setMultiChoiceItems(timeStr, isTimeChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        isTimeChecked[which] = isChecked;
                    }
                })
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialoginterface, int i) {
                        int testCount = 0;
                        for(int j = 0; j < 7; j++) {
                            if(isTimeChecked[j] == true) {
                                testCount++;
                            }
                        }
                        if(testCount == 0){
                            Toast.makeText(User2SurveyActivity.this, "您未選擇時段", Toast.LENGTH_LONG).show();
                            openOptionsDialog();
                        }
                        else{
                            String strength;
                            //Toast.makeText(User2SurveyActivity.this, PatientName, Toast.LENGTH_SHORT).show();
                            Map<String, Object> note = new HashMap<>();
                            note.put("量表分數",score);
                            note.put("名字",PatientName);
                            note.put("週一",isTimeChecked[0]);
                            note.put("週二",isTimeChecked[1]);
                            note.put("週三",isTimeChecked[2]);
                            note.put("週四",isTimeChecked[3]);
                            note.put("週五",isTimeChecked[4]);
                            note.put("週六",isTimeChecked[5]);
                            note.put("週日",isTimeChecked[6]);

                            note.put("心血管疾病",disease[0]);
                            note.put("高血壓",disease[1]);
                            note.put("糖尿病",disease[2]);
                            UserRef = db.collection(DoctorName).document(PatientName).collection("資料").document("基本資料");
                            UserRef.set(note)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ;//Toast.makeText(User2SurveyActivity.this, "success", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(User2SurveyActivity.this, "errrrrrrrrrrrrrrrr", Toast.LENGTH_SHORT).show();
                                            Log.d("user2", e.toString());
                                        }
                                    });
                            Map<String, Object> note1 = new HashMap<>();
                            if(score<336) {
                                note1.put("強度","弱");
                                strength = "弱";
                            }
                            else {
                                note1.put("強度","強");
                                strength = "強";
                            }
                            UserRef = db.collection(DoctorName).document(PatientName).collection("資料").document("訓練強度");
                            UserRef.set(note1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ;//Toast.makeText(User2SurveyActivity.this, "success2", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(User2SurveyActivity.this, "errr22222222", Toast.LENGTH_SHORT).show();
                                            Log.d("user2", e.toString());
                                        }
                                    });
                            myDBHelper.insertToLocalDB(PatientName, DoctorName);
                            Intent intent = new Intent();
                            intent.setClass(User2SurveyActivity.this, User3HomeActivity.class);
                            intent.putExtra("pName", PatientName);
                            intent.putExtra("dName", DoctorName);
                            intent.putExtra("strength", strength);
                            startActivity(intent);
                        }
                    }
                })
                .setCancelable(false)
                .create();

        dialog.show();
    }

    public void btnClickEvent() {
        questionConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(question_index == 17) {
                    for(int i = 0; i < 3; i++) {
                        if(checkBox[i].isChecked()) {
                            disease[i] = true;
                        }
                    }
                }
                for(int i = 0; question_index < 16 && i < 4; i++) {
                    if(checkBox[i].isChecked()) {
                        scoreArr[question_index] = i;
                        break;
                    }
                }
                if(question_index == 15 || (question_index <= 8 && question_index % 2 == 0 && scoreArr[question_index] == 0)) {
                    question_index += 2;
                }
                else {
                    question_index++;
                }
                ani_checkBox(500,1, 0);
            }
        });
    }

    public void checkedEvent() {
        checkBox[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    questionConfirmBtn.setEnabled(true);
                    for(int j = 0; question_index < qLength && j < 4; j++) {
                        if(j != 0)
                            checkBox[j].setChecked(false);
                    }
                    checkBox[3].setChecked(false);
                }
                else {
                    questionConfirmBtn.setEnabled(false);
                    for(int j = 0; j < 4; j++) {
                        if(checkBox[j].isChecked())
                            questionConfirmBtn.setEnabled(true);
                    }
                }
            }
        });
        checkBox[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    questionConfirmBtn.setEnabled(true);
                    for(int j = 0; question_index < qLength && j < 4; j++) {
                        if(j != 1)
                            checkBox[j].setChecked(false);
                    }
                    checkBox[3].setChecked(false);
                }
                else {
                    questionConfirmBtn.setEnabled(false);
                    for(int j = 0; j < 4; j++) {
                        if(checkBox[j].isChecked())
                            questionConfirmBtn.setEnabled(true);
                    }
                }
            }
        });
        checkBox[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    questionConfirmBtn.setEnabled(true);
                    for(int j = 0; question_index < qLength && j < 4; j++) {
                        if(j != 2)
                            checkBox[j].setChecked(false);
                    }
                    checkBox[3].setChecked(false);
                }
                else {
                    questionConfirmBtn.setEnabled(false);
                    for(int j = 0; j < 4; j++) {
                        if(checkBox[j].isChecked())
                            questionConfirmBtn.setEnabled(true);
                    }
                }
            }
        });
        checkBox[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    questionConfirmBtn.setEnabled(true);
                    for(int j = 0; question_index <= qLength && j < 4; j++) {
                        if(j != 3)
                            checkBox[j].setChecked(false);
                    }
                }
                else {
                    questionConfirmBtn.setEnabled(false);
                    for(int j = 0; j < 4; j++) {
                        if(checkBox[j].isChecked())
                            questionConfirmBtn.setEnabled(true);
                    }
                }
            }
        });
    }

}

