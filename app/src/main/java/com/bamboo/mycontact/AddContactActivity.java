package com.bamboo.mycontact;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bamboo.mycontact.database.Contact;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

public class AddContactActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    EditText editTextAddName, editTextAddPhone;
    ImageView imageViewAddProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editTextAddName = (EditText) findViewById(R.id.editTextAddName);
        editTextAddPhone = (EditText) findViewById(R.id.editTextAddPhone);
        imageViewAddProfile = (ImageView) findViewById(R.id.imageViewAddProfile);
    }

    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        switch (view.getId()) {
            case R.id.buttonAddCancel:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.buttonAddSave:

                String className = imageViewAddProfile.getDrawable().getClass().getSimpleName();

                byte[] byteArray = null;

                if (className.equals("BitmapDrawable")) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    Bitmap bitmap = ((BitmapDrawable) imageViewAddProfile.getDrawable()).getBitmap();

                    float scale = (float) 1024 / (float) bitmap.getWidth();
                    int width = (int) (bitmap.getWidth() * scale);
                    int height = (int) (bitmap.getHeight() * scale);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();
                }

                String name = editTextAddName.getText().toString().trim();
                String phone = editTextAddPhone.getText().toString().trim();

                if (name.length() == 0) {
                    if (phone.length() == 0) {
                        Toast.makeText(getApplicationContext(), "저장할 내용이 없어 저장하지 않았습니다.",
                                Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                        break;
                    } else {
                        name = phone;
                    }
                }

                Toast.makeText(getApplicationContext(), "저장!",
                        Toast.LENGTH_SHORT).show();

                MainActivity.db.contactDao().insert(new Contact(name, phone, byteArray));
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
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

                        Glide.with(AddContactActivity.this)
                                .load(uri)
                                .into(imageViewAddProfile);
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