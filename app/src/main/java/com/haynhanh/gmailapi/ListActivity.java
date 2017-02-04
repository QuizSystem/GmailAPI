package com.haynhanh.gmailapi;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView lvMailList;
    List values;
    ArrayAdapter<String> adapter;
    ProgressDialog progressDialog;

    private static final String TAG = ListActivity.class.getSimpleName();

    com.google.api.services.gmail.Gmail mService;

    GoogleAccountCredential credential;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM, Scopes.PLUS_LOGIN};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lvMailList = (ListView) findViewById(R.id.lvMailList);
        values = new ArrayList();
        // TODO: Test Email List
//        String[] values = new String[] { "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View"
//        };
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        lvMailList.setAdapter(adapter);
        // Event Click
        lvMailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) lvMailList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }

        });

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("AndroidGmailAPIexample")
                .build();
    }

    public void sendMail(View view){
        Toast.makeText(this, "Send Mail", Toast.LENGTH_SHORT).show();
        new AsynLoad().execute();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Toast.makeText(this, "Google Play Services required: after installing, close and relaunch this app.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                Log.d(TAG, "REQUEST_GOOGLE_PLAY_SERVICES");
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                Log.d(TAG, "REQUEST_ACCOUNT_PICKER");
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Account unspecified.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                Log.d(TAG, "REQUEST_AUTHORIZATION");
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshResults() {
        Log.d(TAG, "refreshResults");

        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new AsynLoad().execute();
            } else {
                Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean isDeviceOnline() {
        Log.d(TAG, "isDeviceOnline");

        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d(TAG, "deviceOnline: " + (networkInfo != null && networkInfo.isConnected()));
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        Log.d(TAG, "isGooglePlayServicesAvailable");

        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        Log.d(TAG, "showGooglePlayServicesAvailabilityErrorDialog");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        ListActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private class AsynLoad extends AsyncTask<Void, Void, Boolean> {

        List<Mail> mails = new ArrayList<Mail>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mails.clear();
            progressDialog = new ProgressDialog(ListActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                List<Message> messages = GmailUtil.listAllMessages(mService, "me", 5);
                for (int i = 0; i<messages.size() && i<10; i++) {
                    Message messageDetail = GmailUtil.getMessage(mService, "me", messages.get(i).getId(), "full");
                    String content = messageDetail.getSnippet();
                    String subject = "";
                    String from = "";
                    String date = "";
                    List<MessagePartHeader> messagePartHeader = messageDetail.getPayload().getHeaders();
                    for (int j = 0; j<messagePartHeader.size(); j++) {
                        if (messagePartHeader.get(j).getName().equals("Subject")) {
                            subject = messagePartHeader.get(j).getValue();
                        }
                        if (messagePartHeader.get(j).getName().equals("From")) {
                            from = messagePartHeader.get(j).getValue();
                        }
                        if (messagePartHeader.get(j).getName().equals("Date")) {
                            date = messagePartHeader.get(j).getValue();
                        }
                    }
                    if (subject.length() > 0 && content.length() > 0 && from.length() > 0 && date.length() > 0) {
                        Mail mail = new Mail(subject, content, from, date);
                        mails.add(mail);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            values.clear();
            for (Mail mail : mails) {
                values.add(mail.getFrom());
            }
            adapter.notifyDataSetChanged();
        }

    }

}
