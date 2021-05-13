package com.example.turkeyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doc1ChoosePatientActivity extends AppCompatActivity {
    private static final String TAG = "ChooseDoctorActivity";
    private static final String KEY_NAME = "name";
    //private DBHelper myDBHelper;
    private TextView textView_choosePatient;
    private ArrayAdapter listAdapter;
    private ListView listView_patient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientsRef;
    private CollectionReference doctorsRef = db.collection("醫師list");
    private DocumentReference newDoctorRef;
    private String doctorName;
    private List<String> doctorsList = new ArrayList<>();
    private static final String testNumber = "ydfp3slg4g";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_doc1_choose_patient);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);

        //myDBHelper = new DBHelper(this);
        doctorName = getIntent().getStringExtra("name");

        findObject();
        checkDoctor();
        getPatientName();
    }

    private void findObject() {
        textView_choosePatient = findViewById(R.id.textView_chPatient);
        listView_patient = findViewById(R.id.listView_patient);
    }

    public void checkDoctor() {
        doctorsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    doctorsList.clear();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String dName = document.getString(KEY_NAME);
                        doctorsList.add(dName);
                    }
                    boolean isExist = false;
                    if(!doctorsList.isEmpty()){
                        for(int i = 0; i < doctorsList.size(); i++) {
                            if(doctorsList.get(i).equals(doctorName)) {
                                isExist = true;
                                break;
                            }
                        }
                    }

                    if(isExist == true) {
                        getPatientName();
                    }
                    else {
                        //myDBHelper.insertToLocalDB(testNumber, doctorName);
                        Toast.makeText(Doc1ChoosePatientActivity.this, "歡迎加入!!", Toast.LENGTH_LONG).show();
                        Map<String, Object> note1 = new HashMap<>();
                        note1.put("name", doctorName);

                        newDoctorRef = db.collection("醫師list").document(String.valueOf(doctorsList.size() + 1));
                        newDoctorRef.set(note1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(Doc1ChoosePatientActivity.this, "success2", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(Doc1ChoosePatientActivity.this, "errr22222222", Toast.LENGTH_SHORT).show();
                                        Log.d("user2", e.toString());
                                    }
                                });
                        newDoctorRef = db.collection(doctorName).document(testNumber);
                        newDoctorRef.set(note1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ;//Toast.makeText(Doc1ChoosePatientActivity.this, "success4", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(Doc1ChoosePatientActivity.this, "errr244", Toast.LENGTH_SHORT).show();
                                        Log.d("user2", e.toString());
                                    }
                                });
                    }
                }
                else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void getPatientName() {
        // doctor : 醫生名單
        patientsRef = db.collection(doctorName);
        patientsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> pList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String pName = document.getId();
                        if(!pName.equals(testNumber))
                            pList.add(pName);
                    }
                    if (!pList.isEmpty()) {
                        textView_choosePatient.setText("請選擇欲查看的患者");
                        listAdapter = new ArrayAdapter(Doc1ChoosePatientActivity.this, android.R.layout.simple_list_item_1, pList);
                        listView_patient.setAdapter(listAdapter);
                        listView_patient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                openOptionsDialog(adapterView.getItemAtPosition(position).toString());
                            }
                        });
                    }
                    else {
                        textView_choosePatient.setText("尚未有患者加入");
                    }
                }
                else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // 對話框所執行的function
    private void openOptionsDialog(String whichPatient) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("您選擇了    " + whichPatient + "  患者。");

        // 設定 PositiveButton 也就是一般 確定 或 OK 的按鈕
        dialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialoginterface, int i) {
                // 當使用者按下確定鈕後所執行的動作
                Intent intent = new Intent();
                intent.setClass(Doc1ChoosePatientActivity.this, Doc2HomeActivity.class);
                intent.putExtra("pName", whichPatient);
                intent.putExtra("dName", doctorName);
                startActivity(intent);
//                Map<String, Object> note = new HashMap<>();
//                note.put(KEY_NAME, doctorName);
//                //Toast.makeText(User1ChooseDoctorActivity.this, patientName, Toast.LENGTH_SHORT).show();
//                db.collection(whichPatient).document(doctorName).set(note)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                //Toast.makeText(User1ChooseDoctorActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
//                                //myDBHelper.insertToLocalDB(whichPatient, doctorName);
//                                Intent intent = new Intent();
//                                intent.setClass(Doc1ChoosePatientActivity.this, Doc2HomeActivity.class);
//                                intent.putExtra("pName", whichPatient);
//                                intent.putExtra("dName", doctorName);
//                                startActivity(intent);
//                                //finish();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                //Toast.makeText(User1ChooseDoctorActivity.this, "Error!", Toast.LENGTH_SHORT).show();
//                                Log.d(TAG, e.toString());
//                            }
//                        });

            }
        } );

        //設定 NegativeButton 也就是一般 取消 或 Cancel 的按鈕
        dialog.setNegativeButton("重新選擇", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialoginterface, int i) { }
        });

        dialog.show();
    }
}