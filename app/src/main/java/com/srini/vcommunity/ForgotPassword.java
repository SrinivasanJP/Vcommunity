package com.srini.vcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    //view variables
    private EditText vMailID;
    private ProgressBar vProgressBar;
    private RelativeLayout vRecoverId;
    private TextView BacktoSignin;

    //backend variables
    private String mailID;

    //firebase variables
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //hooks
        vMailID = findViewById(R.id.emailID_torecover);
        vProgressBar = findViewById(R.id.ProgressbarForgetPassword);
        vRecoverId = findViewById(R.id.btnRecover_fpa);
        BacktoSignin = findViewById(R.id.fptologin);

        vProgressBar.setVisibility(View.INVISIBLE);

        BacktoSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        vRecoverId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailID = vMailID.getText().toString().trim();
                vProgressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(mailID);
            }
        });
    }
}