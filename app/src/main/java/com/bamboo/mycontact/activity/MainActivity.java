package com.bamboo.mycontact.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bamboo.mycontact.ContactListAdapter;
import com.bamboo.mycontact.R;
import com.bamboo.mycontact.database.AppDatabase;
import com.bamboo.mycontact.database.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] permission_list = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS
    };

    private final String TAG = this.getClass().getSimpleName();

    private TextView textViewContactCount;
    private RecyclerView recyclerView;
    private ContactListAdapter adapter;
    private MenuItem buttonSelectAll, buttonDelete, searchItem;
    private static FloatingActionButton addFab, deleteFab;

    private ArrayList<Contact> items;

    private boolean isDeleteMode = false;
    private boolean isSelectedAll = false;

    public static AppDatabase db;

    public static void selectedCallback(Context context, int count) {
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

        textViewContactCount = (TextView) findViewById(R.id.textViewMainCount);
        addFab = (FloatingActionButton) findViewById(R.id.addContactFab);
        deleteFab = (FloatingActionButton) findViewById(R.id.deleteContactFab);
        deleteFab.setVisibility(View.INVISIBLE);

        db = Room.databaseBuilder(this, AppDatabase.class, "User-db")
                .allowMainThreadQueries().build();

        initList();
        showList(db.contactDao().getAll());

        checkPermission();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        clearList();
        showList(db.contactDao().getAll());
    }

    @Override
    public void onBackPressed() {
        if (isDeleteMode) {
            adapter.setDeleteMode(false);
            adapter.notifyDataSetChanged();

            searchItem.setVisible(true);
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

        searchItem = menu.findItem(R.id.main_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.length() == 0) {
                clearList();
                showList(db.contactDao().getAll());
            } else {
                List<Contact> list = new ArrayList<>();

                list.addAll(db.contactDao().searchByName("%" + newText + "%"));
                list.addAll(db.contactDao().searchByPhone("%" + newText + "%"));

                clearList();
                showList(list);
            }

            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_action_delete:
                adapter.setDeleteMode(true);
                adapter.notifyDataSetChanged();

                item.setVisible(false);
                addFab.setVisibility(View.INVISIBLE);

                searchItem.setVisible(false);

                buttonSelectAll.setVisible(true);
                isDeleteMode = true;

                return true;
            case R.id.main_action_select_all:
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 주어졌을때
                Log.d(TAG, "Permission Granted: " + permissions[i]);
            } else {
                // 권한이 거절되었을
                Log.d(TAG, "Permission Denied: " + permissions[i]);
            }
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

    public void showList(List<Contact> list) {
        // List<Contact> list = db.contactDao().getAll();

        for (Contact item : list) {
            items.add(item);
        }

        adapter.setItems(items);

        textViewContactCount.setText(adapter.getItemCount() + "개의 연락처");
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
                            showList(db.contactDao().getAll());
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

                afterDeleteAction();

                break;
        }
    }

    public void afterDeleteAction() {
        adapter.setDeleteMode(false);
        buttonSelectAll.setVisible(false);
        buttonDelete.setVisible(true);
        isDeleteMode = false;
        deleteFab.setVisibility(View.INVISIBLE);
        addFab.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        clearList();
        showList(db.contactDao().getAll());

        Toast.makeText(getApplicationContext(), "연락처를 삭제했습니다.", Toast.LENGTH_SHORT).show();
    }

    public void checkPermission() {
        // 현재 안드로이 버전이 6.0 미만이면 종료
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        for (String permission : permission_list) {
            int chk = checkSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list, 0);
            }
        }
    }
}