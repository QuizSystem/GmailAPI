package com.haynhanh.gmailapi;

/**
 * Created by thieumao on 1/29/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private ListActivity mActivity;
    private GoogleAccountCredential credential;

    private static final String USER_ID = "me";
    private static final String TO = "leoski94@gmail.com";
    private static final String FROM = "thieumao@gmail.com";
    private static final String SUBJECT = "My title";
    private static final String BODY = "This email is from Thieu Mao";

    ApiAsyncTask(ListActivity activity, GoogleAccountCredential credential) {
        this.mActivity = activity;
        this.credential = credential;
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {

        /*
        try {
            String token = credential.getToken();
            Log.d("CredentialTask", "token:\n" + token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        */

        try {
//            mActivity.clearResultsText();
//            mActivity.updateResultsText(getDataFromApi());

            List<Message> messages = listAllMessages(USER_ID);
            // TODO: Get all email from inbox
            if(messages != null && messages.size() > 0){
                for (int i = 0; i<messages.size() && i<10; i++) {
                    GmailUtil.getMessage(mActivity.mService, USER_ID, messages.get(i).getId(), "raw");
                }
            }

            GmailUtil.sendMessage(mActivity.mService, USER_ID, GmailUtil.createEmail(TO, FROM, SUBJECT, BODY));

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    TestActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
//            mActivity.updateStatus("The following error occurred:\n" +
//                    e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     * @return List of Strings labels.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<String>();
        ListLabelsResponse listResponse =
                mActivity.mService.users().labels().list(user).execute();
        for (Label label : listResponse.getLabels()) {
            labels.add(label.getName());
        }
        return labels;
    }

    /**
     * List all Messages of the user's mailbox with labelIds applied.
     *
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @throws IOException
     */
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
        for (Message message : messages) {
            System.out.println(message.toPrettyString());
        }
        return messages;
    }
}
