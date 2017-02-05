package com.haynhanh.gmailapi;

/**
 * Created by thieumao on 2/4/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class MailAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<Mail> mails = new ArrayList<Mail>();

    public MailAdapter(Context context, ArrayList<Mail> mails) {
        this.context = context;
        this.mails = mails;
    }

    @Override
    public int getCount() {
        return mails.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.mail_item, parent, false);
        TextView tvFrom = (TextView) itemView.findViewById(R.id.tvFrom);
        TextView tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        TextView tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
        TextView tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        try {
            Mail mail = mails.get(position);
            tvFrom.setText(mail.getFrom());
            tvDate.setText(mail.getDate());
            tvSubject.setText(mail.getSubject());
            tvContent.setText(mail.getContent());
        } catch (Exception e) {

        }
        return itemView;
    }
}

