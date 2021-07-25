package com.bamboo.mycontact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bamboo.mycontact.database.Contact;

public class AddContactActivity extends AppCompatActivity {

    EditText editTextAddName, editTextAddPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editTextAddName = (EditText) findViewById(R.id.editTextAddName);
        editTextAddPhone = (EditText) findViewById(R.id.editTextAddPhone);
    }

    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        switch (view.getId()) {
            case R.id.buttonAddCancel:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.buttonAddSave:
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

                MainActivity.db.contactDao().insert(new Contact(name, phone));
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}