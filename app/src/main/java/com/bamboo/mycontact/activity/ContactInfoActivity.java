package com.bamboo.mycontact.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bamboo.mycontact.R;

public class ContactInfoActivity extends AppCompatActivity {

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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