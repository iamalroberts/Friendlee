package com.iamalroberts.friendlee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView l;
    ArrayList<Contact> contacts = new ArrayList<>();
    ContactAdapter arr;
    EditText customMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customMessage = findViewById(R.id.customMessage);
        l = findViewById(R.id.list);
        arr = new ContactAdapter(this, contacts);
        l.setAdapter(arr);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact c = (Contact) adapterView.getItemAtPosition(i);
                Toast.makeText(MainActivity.this, c.getPhoneNumber(), Toast.LENGTH_LONG).show();

            }
        });
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact c = (Contact) adapterView.getItemAtPosition(i);
                removeContact(c);
                Toast.makeText(MainActivity.this, c.getDisplayName() + " removed", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        getContacts();
        Button addContact = findViewById(R.id.addContact);
        Button sendMessage = findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( getApplicationContext().checkSelfPermission( Manifest.permission.SEND_SMS ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 3);
                }
                else {
                    sendGroupMessage(contacts);
                }

            }
        });
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 2);
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContact, 1);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForResult
            switch (requestCode) {
                case 1:
                    contactPicked(data);
                    break;
                case 2:
                    break;
                case 3:
                    sendGroupMessage(contacts);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Uri uri = data.getData();
        ContentResolver cr = this.getContentResolver();

        try {
            Cursor cur = cr.query(uri, null, null, null, null);
            cur.moveToFirst();
            String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber =cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            saveContact(displayName, phoneNumber);
            arr.add(new Contact(displayName, phoneNumber));
            l.invalidate();

    }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
}
    public void sendGroupMessage(ArrayList<Contact> contacts)
    {
        String messageToSend;
        if(customMessage.getText().toString().isEmpty())
        {
            messageToSend = "How are you feeling";
        }else
        {
            messageToSend = customMessage.getText().toString();
        }

        for(Contact c : contacts)
        {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(c.getPhoneNumber(), null, messageToSend, null, null);
        }

        Toast.makeText(this, "Messages sent successfully", Toast.LENGTH_SHORT).show();


    }

    public void getContacts()
    {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences .getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            arr.add(new Contact(entry.getKey(), entry.getValue().toString()));

        }
        if(!arr.isEmpty()) {
            l.invalidate();
        }


    }
    public void saveContact(String name, String number)
    {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, number);
        editor.apply();
    }
    public void removeContact(Contact contactToRemove)
    {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(contactToRemove.getDisplayName());
        editor.apply();
        arr.remove(contactToRemove);
        l.invalidate();
    }

}