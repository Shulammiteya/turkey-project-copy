package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private TextView question;
    private Button doctorBtn, userBtn, enterNameBtn;
    private EditText editText_name;
    private ObjectAnimator animator1,  animator2, animator3;
    private boolean isDoctor, isUser;
    private GifImageView background_gif;
    private ImageView background3;

    private DBHelper myDBHelper;
    private ArrayList<AccountInfo> myAccountBook;

    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String PatientName = "",  DoctorName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        //getWindow().setAttributes(params);

        myDBHelper = new DBHelper(this);
        myAccountBook = new ArrayList<AccountInfo>();
        myAccountBook.addAll(myDBHelper.getTotalAccountInfo());
        getAccountSource();

        findObject();
        initial();
        btnClickEvent();
        ani_greeting();
    }

    public void getAccountSource() {
        for (int index = 0; index < this.myAccountBook.size(); index++) {
            AccountInfo eachPersonContactInfo = this.myAccountBook.get(index);
            PatientName = eachPersonContactInfo.getAccount();
            DoctorName = eachPersonContactInfo.getDoctorName();
        }
        //Toast.makeText(this, (PatientName + " " + DoctorName), Toast.LENGTH_LONG).show();
    }

    private void isFirstTimeLogin() {

        if(DoctorName.equals("")) {
            return;
        }
        else if(PatientName.equals("")) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Doc1ChoosePatientActivity.class);
            intent.putExtra("name", DoctorName);
            startActivity(intent);
            finish();
            return;
        }
        else {
            db = FirebaseFirestore.getInstance();
            userRef = db.collection(DoctorName).document(PatientName).collection("資料").document("訓練強度");
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String strength = (String) task.getResult().get("強度");
                            //Toast.makeText(MainActivity.this, "訓練強度: " + strength, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, User3HomeActivity.class);
                            intent.putExtra("pName", PatientName);
                            intent.putExtra("dName", DoctorName);
                            intent.putExtra("strength", strength);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    } else {
                        Log.d("MissionActivity", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    public void findObject() {
        linearLayout = findViewById(R.id.linearLayout);
        question = findViewById(R.id.textView_question);
        doctorBtn = findViewById(R.id.button_doctorBtn);
        userBtn = findViewById(R.id.button_userBtn);
        background_gif = findViewById(R.id.gifImageView_background2);
        background3 = findViewById(R.id.imageView_background3);
    }

    private void initial() {
        userBtn.setEnabled(false);
        doctorBtn.setEnabled(false);

        editText_name = new EditText(this);
        editText_name.setId(0);
        editText_name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        editText_name.setHint("     姓名     ");
        editText_name.setAlpha(0);
        editText_name.setEnabled(false);

        enterNameBtn = new Button(this);
        enterNameBtn.setId(0);
        enterNameBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        enterNameBtn.setText("確定");
        enterNameBtn.setAlpha(0);
        enterNameBtn.setTextSize(15);
        enterNameBtn.setEnabled(false);

        isDoctor = false;
        isUser = false;
    }

    public void ani_greeting() {

        animator1 = ObjectAnimator.ofFloat(background_gif, "alpha", 1, 0);
        animator1.setDuration(2500);
        animator1.start();

        animator2 = ObjectAnimator.ofFloat(background3, "alpha", 1, 0);
        animator2.setStartDelay(1500);
        animator2.setDuration(2000);
        animator2.start();

        animator3 = ObjectAnimator.ofFloat(question, "alpha", 0, 1);
        animator3.setStartDelay(900);
        animator3.setDuration(500);
        animator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isFirstTimeLogin();
                animator1 = ObjectAnimator.ofFloat(question, "alpha", 1, 0);
                animator1.setStartDelay(1500);
                animator1.setDuration(1000);
                animator1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ani_askID();
                    }
                });
                animator1.start();
            }
        });
        animator3.start();
    }

    public void ani_askID() {
        question.setTextSize(25);
        question.setText("您的身分是...");

        animator1 = ObjectAnimator.ofFloat(question, "alpha", 0, 1);
        animator1.setDuration(750);

        animator2 = ObjectAnimator.ofFloat(doctorBtn, "alpha", 0, 1);
        animator2.setDuration(750);

        animator3 = ObjectAnimator.ofFloat(userBtn, "alpha", 0, 1);
        animator3.setDuration(750);

        animator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                userBtn.setEnabled(true);
                doctorBtn.setEnabled(true);
            }
        });

        animator1.start();
        animator2.start();
        animator3.start();
    }

    public void ani_askName() {

        question.setAlpha(0);
        editText_name.setAlpha(0);
        enterNameBtn.setAlpha(0);

        animator1 = ObjectAnimator.ofFloat(question, "alpha", 1, 0);
        animator1.setDuration(500);

        animator2 = ObjectAnimator.ofFloat(doctorBtn, "alpha", 1, 0);
        animator2.setDuration(500);

        animator3 = ObjectAnimator.ofFloat(userBtn, "alpha", 1, 0);
        animator3.setDuration(500);

        animator3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                linearLayout.removeAllViews();
                question.setText("您的姓名是...");
                linearLayout.addView(editText_name);
                linearLayout.addView(enterNameBtn);

                animator1 = ObjectAnimator.ofFloat(question, "alpha", 0, 1);
                animator1.setStartDelay(250);
                animator1.setDuration(750);

                animator2 = ObjectAnimator.ofFloat(editText_name, "alpha", 0, 1);
                animator2.setStartDelay(250);
                animator2.setDuration(750);

                animator3 = ObjectAnimator.ofFloat(enterNameBtn, "alpha", 0, 1);
                animator3.setStartDelay(250);
                animator3.setDuration(750);

                animator3.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        editText_name.setEnabled(true);
                        enterNameBtn.setEnabled(true);
                    }
                });

                animator1.start();
                animator2.start();
                animator3.start();
            }
        });

        animator1.start();
        animator2.start();
        animator3.start();
    }

    public void btnClickEvent() {
        doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDoctor = true;
                ani_askName();
            }
        });
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUser = true;
                ani_askName();
            }
        });
        enterNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editText_name.getText().toString();

                if(name.equals(""))
                    Toast.makeText(MainActivity.this, "請輸入您的姓名", Toast.LENGTH_LONG).show();
                else {
                    animator1 = ObjectAnimator.ofFloat(question, "alpha", 1, 0);
                    animator1.setDuration(500);

                    animator2 = ObjectAnimator.ofFloat(editText_name, "alpha", 1, 0);
                    animator2.setDuration(500);

                    animator3 = ObjectAnimator.ofFloat(enterNameBtn, "alpha", 1, 0);
                    animator3.setDuration(500);

                    animator3.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            linearLayout.removeAllViews();
                            Intent intent = new Intent();
                            if(isDoctor) {
                                intent.setClass(MainActivity.this, Doc1ChoosePatientActivity.class);
                                myDBHelper.insertToLocalDB("", name);
                            }
                            else if(isUser)
                                intent.setClass(MainActivity.this, User1ChooseDoctorActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                            finish();
                        }
                    });

                    animator1.start();
                    animator2.start();
                    animator3.start();
                }
            }
        });
    }
}
