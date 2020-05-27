package com.example.android.smsdo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.smsdo.Database.PhoneContract;
import com.example.android.smsdo.Database.PhoneDbHelper;

public class EditorActivity extends AppCompatActivity {

    private EditText phoneET;
    private Button sendButton;
    private PhoneDbHelper mDbHelper=new PhoneDbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        phoneET=findViewById(R.id.phone_number_editor_et);
        sendButton=findViewById(R.id.editor_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertPhoneNo();
            }
        });
    }

    private void insertPhoneNo(){

        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        String phoneNo=phoneET.getText().toString().trim();
        if(!phoneNo.isEmpty()) {
            values.put(PhoneContract.ItemEntry.COLUMN_PHONE_NO, phoneNo);
            long newRowId = db.insert(PhoneContract.ItemEntry.TABLE_NAME, null, values);
            Toast.makeText(this,"Data Saved",Toast.LENGTH_SHORT).show();
            finish();
        }else
            Toast.makeText(this,"Enter a Phone number",Toast.LENGTH_SHORT).show();
    }
}
