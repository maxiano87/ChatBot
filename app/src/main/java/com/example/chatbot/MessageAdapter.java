package com.example.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    List<Message> list;
    Context context;
    LayoutInflater inflater;

    public MessageAdapter(List<Message> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder {
        TextView bot;
        TextView myMessage;
        ImageView photoBot;
        ImageView myPhoto;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.item_message, null);
        Holder h = new Holder();
        h.bot = (TextView) v.findViewById(R.id.bot_message);
        h.myMessage = (TextView) v.findViewById(R.id.my_message);
        h.photoBot = (ImageView) v.findViewById(R.id.image_bot);
        h.myPhoto = (ImageView) v.findViewById(R.id.my_image);

        if (list.get(i).type == 0) {
            h.bot.setText("Bot: " + list.get(i).text);
            h.myMessage.setVisibility(View.GONE);
            h.myPhoto.setVisibility(View.GONE);
        } else {
            h.myMessage.setText("Yo: " + list.get(i).text);
            h.bot.setVisibility(View.GONE);
            h.photoBot.setVisibility(View.GONE);
        }
        return v;
    }
}