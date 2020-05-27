package com.example.android.smsdo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.smsdo.Message.MessageRecyclerViewAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context=this;
    public ArrayList<MessageData> oldData;
    private static char messageKey='9';
    private char recievedKed;
    private String address=null;
    private String phoneNo= null;
    private EditText messageEt;
    private Button sendButton;
    public MessageRecyclerViewAdapter mAdapter=new MessageRecyclerViewAdapter(null,context);
    private String Check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_message_list);

        phoneNo=getIntent().getStringExtra("phoneNumber").trim();
        address=String.format("\'%s\'",phoneNo);
        String add="address=";
        address=add+address;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-action-local-broadcast"));

        oldData=smsQuery();
        mAdapter=new MessageRecyclerViewAdapter(oldData,context);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, RecyclerView.VERTICAL,false);
        RecyclerView mRecyclerView=findViewById(R.id.messagelist_recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        messageEt=findViewById(R.id.edit_text_chatbox);
        sendButton=findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMessage();
                messageEt.setText("");
                dataChanged();
            }
        });
    }

    public void btn_send(View view){

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                MyMessage();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Give permission")
                .setPermissions(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE)
                .check();

    }

    public void MyMessage(){

        String phoneNumber =phoneNo;
        String Message = messageEt.getText().toString().trim();
        AESenc cipher = new AESenc();

        try {
            Message=cipher.encrypt(Message);
            Message+=messageKey;
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ArrayList<Integer> simCardList = new ArrayList<>();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            SubscriptionManager subscriptionManager;
            subscriptionManager = SubscriptionManager.from(this);
            final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                    .getActiveSubscriptionInfoList();
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                simCardList.add(subscriptionId);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }


        if(!messageEt.getText().toString().equals("")) {
            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(simCardList.get(0));
            smsManager.sendTextMessage(phoneNumber, null, Message, null, null);
            Toast.makeText(this, "Message Send", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "please enter smthng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 0:

                if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    MyMessage();
                }

                else{
                    Toast.makeText(this, "you dont have required permission", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

//  Method for retrieving Sms from phone
    public ArrayList<MessageData> smsQuery() {

        ArrayList<MessageData> data=new ArrayList<>();
        final String SMS_URI_ALL = "content://sms/";
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        Cursor cur = null;
        try {

            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};

            if (ContextCompat.checkSelfPermission(getBaseContext(),
                    "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

                cur = getContentResolver().query(uri,
                        projection,
                        address,
                        null,
                        null);

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);

                cur = getContentResolver().query(uri,
                        projection,
                        address,
                        null,
                        null);
            }

            if (cur.moveToFirst()) {

                int index_Date = cur.getColumnIndex("date");
                int index_Body = cur.getColumnIndex("body");
                int index_Type = cur.getColumnIndex("type");
                do {

                    long longDate = cur.getLong(index_Date);
                    String strbody = cur.getString(index_Body);
                    recievedKed=strbody.charAt(strbody.length()-1);
                    if (recievedKed==messageKey){

                        strbody=strbody.substring(0,strbody.length()-1);
                        AESenc cipher=new AESenc();
                        try {
                            strbody=cipher.decrypt(strbody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    int int_Type = cur.getInt(index_Type);

                    MessageData message=new MessageData(strbody,int_Type,longDate);
                    data.add(message);
                } while (cur.moveToNext());

                if (!data.isEmpty()){
                    Collections.sort(data, new Comparator<MessageData>() {
                        @Override
                        public int compare(MessageData o1, MessageData o2) {
                            return Long.compare(o1.getTime(), o2.getTime());
                        }
                    });
                }

                if (!cur.isClosed()) {
                    cur.close();
                }

            } else {

                data.add(new MessageData("No Result",1,0));
            } // end if
        } catch (
                SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }

        return data;
    }

    public void dataChanged(){
        ArrayList<MessageData> newData=smsQuery();
        mAdapter.updateList(newData);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dataChanged();
        }
    };
}
