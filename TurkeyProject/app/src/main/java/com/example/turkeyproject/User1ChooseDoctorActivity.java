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

public class User1ChooseDoctorActivity extends AppCompatActivity {
    private static final String TAG = "ChooseDoctorActivity";
    private static final String KEY_NAME = "name";
    //private DBHelper myDBHelper;
    private ArrayAdapter listAdapter;
    private ListView listView_doctor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorsRef = db.collection("醫師list");
    private String patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user1_choose_doctor);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);

        //myDBHelper = new DBHelper(this);
        patientName = getIntent().getStringExtra("name");
        findObject();
        getDoctorName();
    }

    private void findObject() {
        listView_doctor = findViewById(R.id.listView_doctor);
    }

    private void getDoctorName() {
        doctorsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> mMissionsList = new ArrayList<>();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String dName = document.getString(KEY_NAME);
                        mMissionsList.add(dName);
                    }
                    listAdapter = new ArrayAdapter(User1ChooseDoctorActivity.this, android.R.layout.simple_list_item_1,mMissionsList);
                    listView_doctor.setAdapter(listAdapter);
                    listView_doctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            openOptionsDialog(adapterView.getItemAtPosition(position).toString());
                        }
                    });
                }
                else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // 對話框所執行的function
    private void openOptionsDialog(String whichDoctor) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("您選擇了    " + whichDoctor + "  治療師。");

        // 設定 PositiveButton 也就是一般 確定 或 OK 的按鈕
        dialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialoginterface, int i) {
                // 當使用者按下確定鈕後所執行的動作
                Map<String, Object> note = new HashMap<>();
                note.put(KEY_NAME, patientName);
                //Toast.makeText(User1ChooseDoctorActivity.this, patientName, Toast.LENGTH_SHORT).show();
                db.collection(whichDoctor).document(patientName).set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(User1ChooseDoctorActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                                //myDBHelper.insertToLocalDB(patientName, whichDoctor);
                                Intent intent = new Intent();
                                intent.setClass(User1ChooseDoctorActivity.this, User2SurveyActivity.class);
                                intent.putExtra("pName", patientName);
                                intent.putExtra("dName", whichDoctor);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(User1ChooseDoctorActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
            }
        } );

        //設定 NegativeButton 也就是一般 取消 或 Cancel 的按鈕
        dialog.setNegativeButton("重新選擇", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialoginterface, int i) { }
        });

        dialog.show();
    }
}
