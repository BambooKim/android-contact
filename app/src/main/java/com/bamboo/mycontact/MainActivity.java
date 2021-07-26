package com.bamboo.mycontact;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bamboo.mycontact.database.AppDatabase;
import com.bamboo.mycontact.database.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactListAdapter adapter;

    ArrayList<Contact> items;

    public static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(this, AppDatabase.class, "User-db")
                .allowMainThreadQueries().build();

        initList();
        showList();
    }


    public void initList() {
        items = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.contactListRecyclerView);

        adapter = new ContactListAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void clearList() {
        items = null;
        items = new ArrayList<>();
    }

    public void showList() {
        List<Contact> list = db.contactDao().getAll();

        for (Contact item : list) {
            items.add(new Contact(item.getName(), item.getMobile(), item.getByteArray()));
        }

        adapter.setItems(items);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case RESULT_CANCELED:
                            break;
                        case RESULT_OK:
                            clearList();
                            showList();
                            break;
                    }
                }
            }
    );

    public void btnOnClick(View view) {
        switch (view.getId()) {
            case R.id.addContactFab:
                Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
                launcher.launch(intent);
                break;
        }
    }
}