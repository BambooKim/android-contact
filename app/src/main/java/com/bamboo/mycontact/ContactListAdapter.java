package com.bamboo.mycontact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bamboo.mycontact.database.Contact;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    ArrayList<Contact> items = new ArrayList<>();


    @NonNull
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.contact_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    public void setItems(ArrayList<Contact> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        ImageView contactIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.contactName);
            contactIcon = (ImageView) itemView.findViewById(R.id.contactIcon);
        }

        public void setItem(Contact item) {
            contactName.setText(item.getName());
            // contactIcon.setImageResource(item.());
        }
    }
}