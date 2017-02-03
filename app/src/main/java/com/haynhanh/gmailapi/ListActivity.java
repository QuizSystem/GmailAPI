package com.haynhanh.gmailapi;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ListView lvMailList;

    private static final String TAG = ListActivity.class.getSimpleName();

    com.google.api.services.gmail.Gmail mService;

    GoogleAccountCredential credential;
//    private TextView mStatusText;
//    private TextView mResultsText;
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
        // TODO: Test Email List
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
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
        new AsynLoad(this, credential).execute();
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
//            mStatusText.setText("Google Play Services required: " +
//                    "after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
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
//                    mStatusText.setText("Account unspecified.");
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
//                new ApiAsyncTask(this, credential).execute();
            } else {
//                mStatusText.setText("No network connection available.");
            }
        }
    }

//    public void clearResultsText() {
//        Log.d(TAG, "clearResultsText");
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStatusText.setText("Retrieving data…");
//                mResultsText.setText("");
//            }
//        });
//    }

//    public void updateResultsText(final List<String> dataStrings) {
//        Log.d(TAG, "updateResultsText");
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (dataStrings == null) {
//                    mStatusText.setText("Error retrieving data!");
//                } else if (dataStrings.size() == 0) {
//                    mStatusText.setText("No data found.");
//                } else {
//                    mStatusText.setText("Data retrieved using" +
//                            " the Gmail API:");
//                    mResultsText.setText(TextUtils.join("\n\n", dataStrings));
//                }
//            }
//        });
//    }

//    public void updateStatus(final String message) {
//        Log.d(TAG, "updateStatus");
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStatusText.setText(message);
//            }
//        });
//    }

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

        private ListActivity mActivity;
        private GoogleAccountCredential credential;

        List<Message> messages = new ArrayList<Message>();

        AsynLoad(ListActivity activity, GoogleAccountCredential credential) {
            this.mActivity = activity;
            this.credential = credential;
            messages = new ArrayList<Message>();
        }

        public List<Message> listAllMessages(String userId) throws IOException {
            System.out.println("listMessagesWithLabels");
            ListMessagesResponse response = mActivity.mService.users().messages().list(userId).execute();
            List<Message> messages = new ArrayList<Message>();
            int dem = 0;
            while (response.getMessages() != null && dem < 2) {
                dem++;
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = mActivity.mService.users().messages().list(userId)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }
            return messages;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                messages = listAllMessages("me");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            for (Message message : messages) {
                try {
                    Log.e("mao", "mao " + message.toPrettyString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
