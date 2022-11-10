package com.srini.vcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreatePost extends AppCompatActivity {
    private EditText vTaskInput;
    private EditText vDescription;
    private TextView vDatePicker;
    private RelativeLayout vBtnAddTask;
    private ProgressBar vProgressBar;
    private TextView vAddTaskTextview;

    //backend variables
    private TaskHelperClass taskHelperClass;
    //firebase variables
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        vTaskInput = findViewById(R.id.task_input);
        vDescription = findViewById(R.id.task_discription_input);
        vBtnAddTask = findViewById(R.id.btnAddTask);
        vProgressBar = findViewById(R.id.ProgressbarAddTask);
        vAddTaskTextview = findViewById(R.id.BtnAddTasktv);


        vBtnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vAddTaskTextview.setVisibility(View.INVISIBLE);
                vProgressBar.setVisibility(View.VISIBLE);
                taskHelperClass = new TaskHelperClass();
                taskHelperClass.setTitle(vTaskInput.getText().toString().trim());
                taskHelperClass.setDescription(vDescription.getText().toString().trim());
                taskHelperClass.setDone(false);
                if(taskHelperClass.getTitle().isEmpty()){
                    vTaskInput.setError("Title required");
                    vAddTaskTextview.setVisibility(View.VISIBLE);
                    vProgressBar.setVisibility(View.INVISIBLE);
                }else {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    documentReference = firebaseFirestore.collection("posts").document(taskHelperClass.getTitle());

                    documentReference.set(taskHelperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){vProgressBar.setVisibility(View.INVISIBLE);
                                            vAddTaskTextview.setVisibility(View.VISIBLE);
                                            vTaskInput.setText("");
                                            vDescription.setText("");
                                            Toast.makeText(getApplicationContext(), "New Task Added", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Somthing went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
    }
    }