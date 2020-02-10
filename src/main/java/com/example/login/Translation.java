package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import org.w3c.dom.Text;

public class Translation extends AppCompatActivity {

    private Button Translate;
    private EditText mSourcetext;
    private TextView mSourceLang;
    private TextView mTranslatedtText;
    private String SourceText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        Translate = (Button)findViewById(R.id.btnTranslate);
        mSourcetext = (EditText)findViewById(R.id.txtSourceText);
        mSourceLang = (TextView) findViewById(R.id.txtsrc);
        mTranslatedtText = (TextView)findViewById(R.id.txtdest);

        Translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyLanguage();
            }


        });
    }
    private void identifyLanguage() {
       SourceText = mSourcetext.getText().toString();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        mSourceLang.setText("Detecting...");
        identifier.identifyLanguage(SourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und"))
                {
                    Toast.makeText(Translation.this,"Language not identified",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getLanguageCode(s);
                }
            }
        });
    }

    private void getLanguageCode(String language)
    {
        int langCode;

        switch (language)
        {
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;
                mSourceLang.setText("HINDI");
                break;

            case "ar":
                langCode = FirebaseTranslateLanguage.AR;
                mSourceLang.setText("ARABIC");
                break;

            case "ur":
                langCode = FirebaseTranslateLanguage.UR;
                mSourceLang.setText("URDU");
                break;

                default:
                    langCode = 0;
         }

         tranlateText(langCode);

    }

    private void tranlateText(int langCode)
    {
        mTranslatedtText.setText("Translating..");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder().setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.EN).build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(SourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                            mTranslatedtText.setText(s);
                    }
                });
            }
        });
    }
}
