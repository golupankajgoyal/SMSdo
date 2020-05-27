package com.example.android.smsdo.Message;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.smsdo.R;

public class SendTypeViewHolder extends RecyclerView.ViewHolder {

    public TextView sendMessageTv;

    public SendTypeViewHolder(@NonNull View itemView) {
        super(itemView);

        this.sendMessageTv=itemView.findViewById(R.id.sent_message_tv);
    }
}
