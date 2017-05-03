package com.example.vimadhavan.assignment131;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public  static final int RequestPermissionCode  = 1 ;
    private Button addBtn,deleteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBtn=(Button) findViewById(R.id.addBtn);
        deleteBtn=(Button) findViewById(R.id.deleteBtn);
        addBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        EnableRuntimePermission();


    }



    private void addContact(String name, String phone) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name)
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build()); //
        //INSERT MOBILE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build()); //



        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("Error::", "RemoteException encountered while inserting contact: " + e.getMessage());

        } catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.e("Error::", "OperationApplicationException encountered while inserting contact: " + e.getMessage());

        }
    }

    private void deleteContact(Context ctx, String name,String phone) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);

                    }

                } while (cur.moveToNext());
            }else{
                Log.d("debug::","no");
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }

    }

    private void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addBtn:
                addBtn.setVisibility(View.GONE);

                addContact("Coderz","1234567890");
                addContact("James","5656215521");
                addContact("John","4545454545");
                addContact("Mary","9632587410");
                addContact("Peter","4561237890");
                deleteBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.deleteBtn:
                deleteBtn.setVisibility(View.GONE);
                deleteContact(getBaseContext(),"Coderz","1234567890");
                deleteContact(getBaseContext(),"James","5656215521");
                deleteContact(getBaseContext(),"John","4545454545");
                deleteContact(getBaseContext(),"Mary","9632587410");
                deleteContact(getBaseContext(),"Peter","4561237890");
                addBtn.setVisibility(View.VISIBLE);
                break;
        }

    }
}
