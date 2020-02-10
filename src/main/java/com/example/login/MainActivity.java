package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password1;
    private Button button;
    private TextView register;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.uname);
        password1 = (EditText) findViewById(R.id.pass);
        button = (Button) findViewById(R.id.btn);
        register = (TextView) findViewById(R.id.txtRegister);
        forgot = (TextView)findViewById(R.id.txtForgot);

        firebaseAuth = firebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();



        if(user != null)
        {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity2.class));
        }



        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Validate(username.getText().toString(),password1.getText().toString());
                                      }
                                  }

        );

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registration.class
                ));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Password.class));
            }
        });


    }

    private void Validate(String uname, String password)
    {
        progressDialog.setMessage("You can study till the time you re getting verified.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(uname,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    //Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(MainActivity.this, MainActivity2.class));
                    checkEmailVerification();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();
        startActivity(new Intent(MainActivity.this, MainActivity2.class));
/*
        if(emailflag)
        {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity2.class));
            
        }
        else
        {
            Toast.makeText(this, "Verify your Email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();

        }


 */
    }
}
