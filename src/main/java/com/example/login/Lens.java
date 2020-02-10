package com.example.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.w3c.dom.Text;

import java.io.IOException;

public class Lens extends AppCompatActivity {


    private TextView mTextView;
    private Button uploadBtn;
    private Button copyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lens);

        mTextView = (TextView)findViewById(R.id.text);
        uploadBtn = (Button)findViewById(R.id.upload);
        copyBtn =  (Button)findViewById(R.id.copy);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CopiedText = mTextView.getText().toString();
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("Image Copied",CopiedText);
                clipboardManager.setPrimaryClip(data);
                Toast.makeText(Lens.this,"Text Copied",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK)
        {
            try {
                FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(Lens.this, data.getData());
                FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                recognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String text = firebaseVisionText.getText();
                        for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks())
                        {
                            mTextView.append("\n \n" + block.getText());
                        }
                    }
                });
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            }

        //super.onActivityResult(requestCode, resultCode, data);
    }
}
