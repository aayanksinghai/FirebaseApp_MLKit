package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Registration extends AppCompatActivity {

    private EditText name;
    private EditText username;
    private EditText password;
    private Button btn_register;
    private TextView txtsignin;
    private ImageView userProfile;
    private EditText userage;
    String uname, u_psssword, n_name, age;
    private static int PICK_IMAGE = 123;

    private StorageReference storageReference;

    private FirebaseStorage firebaseStorage;
    Uri imagePath;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null)
        {
            imagePath = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfile.setImageBitmap(bitmap);

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        SetupUI();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        //StorageReference myRefl = storageReference.child(firebaseAuth.getUid()).getRoot();

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate())
                {
                    String user_email = username.getText().toString().trim();
                    String user_password = password.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(Registration.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                               //sendEmailVerification();
                                sendUserData();
                                Toast.makeText(Registration.this,"Successfully registered, Upload completed",Toast.LENGTH_SHORT).show();
                                //firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(Registration.this, MainActivity.class));
                            }
                            else
                            {
                                Toast.makeText(Registration.this, "Registration Failed Please Try Again!", Toast.LENGTH_SHORT).show();
                            }
                            }
                    });

                }
            }
        });

        txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });
    }

    private void SetupUI()
    {
        name = (EditText) findViewById(R.id.txtName);
        username = (EditText) findViewById(R.id.txtusername);
        password = (EditText) findViewById(R.id.txtPass);
        btn_register = (Button) findViewById(R.id.btnRegister);
        txtsignin = (TextView)findViewById(R.id.txtSignin);
        userage = (EditText) findViewById(R.id.txtAge);
        userProfile = (ImageView) findViewById(R.id.imgProfile);


    }

    private Boolean Validate()
    {
         Boolean result = false;

          uname = username.getText().toString();
          u_psssword = password.getText().toString();
          n_name = name.getText().toString();
          age = userage.getText().toString();

         if(uname.isEmpty() || u_psssword.isEmpty() || n_name.isEmpty() || age.isEmpty() || imagePath == null)
         {
             Toast.makeText(this,"Please enter all the details", Toast.LENGTH_SHORT).show();

         }
         else
         {
             result = true;
         }
         return result;

    }

    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        sendUserData();
                        firebaseAuth.signOut();
                        Toast.makeText(Registration.this,"Successfully registered, Verification mail Sent",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Registration.this, MainActivity.class));

                    }
                    else
                    {
                        Toast.makeText(Registration.this,"Verification mail hasn't been Sent!",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


    private void sendUserData()
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registration.this,"Upload Failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Registration.this,"Upload Successful",Toast.LENGTH_SHORT).show();
            }
        });

        UserProfile userprofile = new UserProfile(age, n_name, uname);
        myRef.setValue(userprofile);
    }

}
