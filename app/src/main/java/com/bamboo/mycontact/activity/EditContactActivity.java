package com.bamboo.mycontact.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bamboo.mycontact.R;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

public class EditContactActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private EditText editTextEditName, editTextEditPhone;
    private ImageView imageViewEditProfile;

    private int id;
    private String name, phone;
    private byte[] bytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        editTextEditName = (EditText) findViewById(R.id.editTextEditName);
        editTextEditPhone = (EditText) findViewById(R.id.editTextEditPhone);
        imageViewEditProfile = (ImageView) findViewById(R.id.imageViewEditProfile);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        bytes = intent.getByteArrayExtra("bytes");

        editTextEditName.setText(name);
        editTextEditPhone.setText(phone);

        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageViewEditProfile.setImageBitmap(bitmap);
        }
    }

    public void onClick(View view) {
        Intent resultIntent = new Intent(getApplicationContext(), ContactInfoActivity.class);

        switch (view.getId()) {
            case R.id.buttonEditCancel:
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                break;
            case R.id.buttonEditSave:

                String className = imageViewEditProfile.getDrawable().getClass().getSimpleName();

                if (className.equals("BitmapDrawable")) {
                    bytes = bitmapToByteArray(imageViewEditProfile);
                }

                name = editTextEditName.getText().toString().trim();
                phone = editTextEditPhone.getText().toString().trim();

                if (name.length() == 0) {
                    if (phone.length() == 0) {
                        Toast.makeText(getApplicationContext(), "저장할 내용이 없어 저장하지 않았습니다.",
                                Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED, resultIntent);
                        finish();
                        break;
                    } else {
                        name = phone;
                    }
                }

                Log.d(TAG, name);
                Log.d(TAG, phone);

                Toast.makeText(getApplicationContext(), "저장!",
                        Toast.LENGTH_SHORT).show();

                MainActivity.db.contactDao().updateById(id, name, phone, bytes);

                resultIntent.putExtra("name", name);
                resultIntent.putExtra("phone", phone);
                resultIntent.putExtra("bytes", bytes);

                setResult(RESULT_OK, resultIntent);
                finish();
                break;
        }
    }

    private byte[] bitmapToByteArray(ImageView imageView) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        float scale = (float) 1024 / (float) bitmap.getWidth();
        int width = (int) (bitmap.getWidth() * scale);
        int height = (int) (bitmap.getHeight() * scale);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        return stream.toByteArray();
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, Integer.toString(result.getResultCode()));

                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Glide.with(EditContactActivity.this)
                                .load(uri)
                                .into(imageViewEditProfile);
                    }
                }
            }
    );

    public void imageViewOnClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }
}