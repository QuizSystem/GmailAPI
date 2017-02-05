package com.haynhanh.gmailapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.MessagingException;

public class SendActivity extends AppCompatActivity {

    EditText etFrom, etTo, etSubject, etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        etFrom = (EditText) findViewById(R.id.etFrom);
        etTo = (EditText) findViewById(R.id.etTo);
        etSubject = (EditText) findViewById(R.id.etSubject);
        etContent = (EditText) findViewById(R.id.etContent);

        etFrom.setText(MainActivity.FROM);
        etTo.setText(MainActivity.TO);
        if (MainActivity.SUBJECT.length() > 0) {
            etSubject.setText("Reply: " + MainActivity.SUBJECT);
        }
    }

    public void sendMail(View view){
        MainActivity.FROM = etFrom.getText().toString();
        MainActivity.TO = etTo.getText().toString();
        MainActivity.SUBJECT = etSubject.getText().toString();
        MainActivity.CONTENT = etContent.getText().toString();
        finish();
    }

}
