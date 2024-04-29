package com.mg;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
EditText email ;
TextView back;
Button btn_e;
FirebaseAuth auth ;
String emalo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        email = (EditText) findViewById(R.id.forget_email_input);
        btn_e = (Button) findViewById(R.id.for_btn);
        back = (TextView) findViewById(R.id.backlogin);
        auth = FirebaseAuth.getInstance();
        btn_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emalo =email.getText().toString().trim();
                if (!TextUtils.isEmpty(emalo)){
                    ResetPassword_1();
                }else {
                    email.setError("Email Faild can't be empty");
                }
            }
        });

    }

    private void ResetPassword_1() {
        back.setVisibility(View.INVISIBLE);
        auth.sendPasswordResetEmail(emalo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgetPassword.this, "Reset Password has been sent to your email", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgetPassword.this, "Error this is email is not true", Toast.LENGTH_SHORT).show();
                back.setVisibility(View.VISIBLE);

            }
        });

    }
}