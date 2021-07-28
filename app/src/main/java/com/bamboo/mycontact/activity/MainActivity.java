package com.bamboo.mycontact.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bamboo.mycontact.ContactListAdapter;
import com.bamboo.mycontact.R;
import com.bamboo.mycontact.database.AppDatabase;
import com.bamboo.mycontact.database.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactListAdapter adapter;
    private MenuItem buttonSelectAll, buttonDelete;
    private static FloatingActionButton addFab, deleteFab;

    private ArrayList<Contact> items;

    private boolean isDeleteMode = false;
    private boolean isSelectedAll = false;

    public static AppDatabase db;

    public static void selectedCallback(Context context, int count) {
        // Toast.makeText(context, Integer.toString(count), Toast.LENGTH_SHORT).show();

        if (count > 0) {
            deleteFab.setVisibility(View.VISIBLE);
        } else {
            deleteFab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.activity_main_toolbar));

        addFab = (FloatingActionButton) findViewById(R.id.addContactFab);
        deleteFab = (FloatingActionButton) findViewById(R.id.deleteContactFab);
        deleteFab.setVisibility(View.INVISIBLE);

        db = Room.databaseBuilder(this, AppDatabase.class, "User-db")
                .allowMainThreadQueries().build();

        initList();
        showList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        clearList();
        showList();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        if (isDeleteMode) {
            adapter.setDeleteMode(false);
            adapter.notifyDataSetChanged();

            buttonDelete.setVisible(true);
            buttonSelectAll.setVisible(false);
            addFab.setVisibility(View.VISIBLE);

            isDeleteMode = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_bar_menu, menu);

        buttonSelectAll = menu.findItem(R.id.main_action_select_all);
        buttonDelete = menu.findItem(R.id.main_action_delete);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_action_delete:
                adapter.setDeleteMode(true);
                adapter.notifyDataSetChanged();

                item.setVisible(false);
                addFab.setVisibility(View.INVISIBLE);

                buttonSelectAll.setVisible(true);
                isDeleteMode = true;

                return true;
            case R.id.main_action_select_all:
                // Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();

                if (isSelectedAll) {
                    item.setIcon(R.drawable.ic_baseline_check_box_outline_blank_24);
                    isSelectedAll = false;

                    adapter.setCheckAll(false);
                    adapter.notifyDataSetChanged();

                    deleteFab.setVisibility(View.INVISIBLE);
                } else {
                    item.setIcon(R.drawable.ic_baseline_check_box_24);
                    isSelectedAll = true;

                    adapter.setCheckAll(true);
                    adapter.notifyDataSetChanged();

                    deleteFab.setVisibility(View.VISIBLE);
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            // items.add(new Contact(item.getName(), item.getMobile(), item.getByteArray()));
            items.add(item);
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
            case R.id.deleteContactFab:

                ArrayList<Contact> deleteItems = new ArrayList<>();
                deleteItems = adapter.getSelectedItems();

                for (Contact item : deleteItems) {
                    db.contactDao().deleteById(item.getId());
                }

                adapter.setDeleteMode(false);
                buttonSelectAll.setVisible(false);
                buttonDelete.setVisible(true);
                isDeleteMode = false;
                deleteFab.setVisibility(View.INVISIBLE);
                addFab.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                clearList();
                showList();

                Toast.makeText(getApplicationContext(), "연락처를 삭제했습니다.", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}