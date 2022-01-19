package com.iamalroberts.friendlee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    private Context mContext;
    private List<Contact> contactList = new ArrayList<>();

    public ContactAdapter(@NonNull Context context, ArrayList<Contact> list) {
        super(context, 0 , list);
        mContext = context;
        contactList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      // super.getView(position, convertView, parent);
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.contacts_list_item,parent,false);
        Contact currentContact = contactList.get(position);
        TextView name = listItem.findViewById(R.id.contactName);
        name.setText(currentContact.getDisplayName());
        return listItem;
    }

//todo implement sort 
//    public void sort() {
//    }
}
