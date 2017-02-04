package com.haynhanh.gmailapi;

/**
 * Created by thieumao on 1/29/17.
 */
import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailUtil {

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public static Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();
        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    // TODO: Get show detail email
    public static Message getMessage(Gmail service, String userId, String messageId, String format) throws IOException {
        Message message = null;
        if(format != null && !format.isEmpty()) {
            message = service.users().messages().get(userId, messageId).setFormat(format).execute();
        } else {
            message = service.users().messages().get(userId, messageId).execute();
        }
        return message;
    }

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {
        System.out.println("listMessagesMatchingQuery");
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
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

    public static List<Message> listMessagesWithLabels(Gmail service, String userId, List<String> labelIds) throws IOException {
        System.out.println("listMessagesWithLabels");
        ListMessagesResponse resp = service.users().messages().list(userId).execute();
        System.out.println("messages");
        System.out.println(resp.getMessages());


        ListMessagesResponse response = service.users().messages().list(userId)
                .setLabelIds(labelIds).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setLabelIds(labelIds)
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

    public static List<Message> listAllMessages(Gmail service, String userId, long max) throws IOException {
//        System.out.println("listMessagesWithLabels");
        ListMessagesResponse response;// = service.users().messages().list(userId).execute();
//        System.out.println("messages");
//        System.out.println(response.getMessages());
        List labelIds = new ArrayList();
        labelIds.add("INBOX");
        if (max < 1) {
            response = service.users().messages().list(userId).setLabelIds(labelIds).execute();
        } else {
            response = service.users().messages().list(userId).setLabelIds(labelIds).setMaxResults(max).execute();
        }


        List<Message> messages = new ArrayList<Message>();
        if (response.getMessages() != null) {
            messages.addAll(response.getMessages());
        }
//        while (response.getMessages() != null) {
//            messages.addAll(response.getMessages());
//            if (response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().messages().list(userId)
//                        .setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
//        }

//        for (Message message : messages) {
//            System.out.println(message.toPrettyString());
//        }

        return messages;
    }


    public static List<String> getLabels(Gmail service, String userId){
        ListLabelsResponse listResponse = null;
        List<String> labelsStr = new ArrayList();

        try {
            listResponse = service.users().labels().list(userId).execute();
            List<Label> labels = listResponse.getLabels();

            if (labels.size() == 0) {
                System.out.println("No labels found.");
            } else {
                System.out.println("Labels:");
                for (Label label : labels) {
                    System.out.printf("- %s\n", label.getName());
                    labelsStr.add(label.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return labelsStr;
    }

}
