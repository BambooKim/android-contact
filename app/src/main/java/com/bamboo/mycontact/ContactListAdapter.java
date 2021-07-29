package com.bamboo.mycontact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bamboo.mycontact.activity.ContactInfoActivity;
import com.bamboo.mycontact.activity.MainActivity;
import com.bamboo.mycontact.database.Contact;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private ArrayList<Contact> items = new ArrayList<>();
    private ArrayList<Contact> selectedItems = new ArrayList<>();

    public ArrayList<Contact> getSelectedItems() {
        return selectedItems;
    }

    public int getSelectedCount() {
        return selectedItems.size();
    }

    public void setDeleteMode(boolean flag) {
        isDeleteMode = flag;
    }

    public boolean getDeleteMode() {
        return isDeleteMode;
    }

    private boolean isDeleteMode = false;

    public void setCheckAll(boolean flag) {
        for (Contact item : items) {
            if (item.isSelected() == !flag) {
                item.setSelected(flag);

                if (flag)
                    selectedItems.add(item);
                else
                    selectedItems.remove(item);
            }
        }
    }

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
        Contact item = items.get(position);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isSelected());

        if (isDeleteMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        else
            holder.checkBox.setVisibility(View.INVISIBLE);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSelected(isChecked);

                if (isChecked) {
                    selectedItems.add(items.get(position));

                    MainActivity.selectedCallback(holder.itemView.getContext(), getSelectedCount());
                } else {
                    selectedItems.remove(items.get(position));

                    MainActivity.selectedCallback(holder.itemView.getContext(), getSelectedCount());
                }
            }
        });

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
        ImageView contactProfileImage;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.contactName);
            contactProfileImage = (ImageView) itemView.findViewById(R.id.contactProfileImage);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        Contact item = items.get(pos);

                        Intent intent = new Intent(v.getContext(), ContactInfoActivity.class);
                        intent.putExtra("id", item.getId());
                        intent.putExtra("name", item.getName());
                        intent.putExtra("phone", item.getMobile());
                        intent.putExtra("profile", item.getByteArray());

                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void setItem(Contact item) {
            contactName.setText(item.getName());

            byte[] bytes = item.getByteArray();
            if (bytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                contactProfileImage.setImageBitmap(bitmap);
            } else {
                contactProfileImage.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }
        }
    }

}
