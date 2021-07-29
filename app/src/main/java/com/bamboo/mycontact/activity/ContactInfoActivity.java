package com.bamboo.mycontact.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bamboo.mycontact.R;

public class ContactInfoActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private TextView infoName, infoPhone;
    private ImageView profileImage;

    private int id;
    private String name, phone;
    private byte[] bytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        setSupportActionBar(findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        infoName = (TextView) findViewById(R.id.textViewInfoName);
        infoPhone = (TextView) findViewById(R.id.textViewInfoPhone);
        profileImage = (ImageView) findViewById(R.id.imageViewInfoProfileImage);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        bytes = intent.getByteArrayExtra("profile");

        infoName.setText(name);
        infoPhone.setText(phone);

        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
        } else {
            profileImage.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_bar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_action_delete:
                alertDialog();

                return true;
            case R.id.info_action_modify:
                Intent intent = new Intent(getApplicationContext(), EditContactActivity.class);

                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("bytes", bytes);

                launcher.launch(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                            Intent resultIntent = result.getData();
                            String _name = resultIntent.getStringExtra("name");
                            String _phone = resultIntent.getStringExtra("phone");
                            byte[] _bytes = resultIntent.getByteArrayExtra("bytes");

                            infoName.setText(_name);
                            infoPhone.setText(_phone);
                            if (_bytes != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length);
                                profileImage.setImageBitmap(bitmap);
                            }


                            Log.d(TAG, _name);
                            Log.d(TAG, _phone);

                            break;
                    }
                }
            }
    );

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("연락처를 삭제할까요?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        MainActivity.db.contactDao().deleteById(id);

                        Toast.makeText(getApplicationContext(), "연락처를 삭제했습니다.",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }
}