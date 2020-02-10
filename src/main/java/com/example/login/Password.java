package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Password extends AppCompatActivity {


    private EditText useremail;
    private Button btnEmail;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        useremail = (EditText) findViewById(R.id.txtEmail);
        btnEmail = (Button)findViewById(R.id.btnSend);

        firebaseAuth = FirebaseAuth.getInstance();

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = useremail.getText().toString().trim();
                if(email.equals(""))
                {
                    Toast.makeText(Password.this, "PLease enter your registered email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Password.this,"Password Reset Link sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(Password.this, MainActivity.class));
                            }
                            else
                            {
                                Toast.makeText(Password.this,"Error in sending password reset link", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

    }
}
