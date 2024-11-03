package com.example.mts_hotel.UI;

import static android.app.PendingIntent.getActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mts_hotel.Model.Users;
import com.example.mts_hotel.Prevalent.Prevalent;
import com.example.mts_hotel.R;
import com.example.mts_hotel.UI.Users.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private ImageButton loginButton, swipeButton;
    private EditText loginPhoneInput, loginPasswordInput;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;
    private TextView AdminLink, NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        swipeButton = (ImageButton)findViewById(R.id.swipe_btn);
        loginButton = (ImageButton)findViewById(R.id.login_button);


        loginButton = (ImageButton)findViewById(R.id.login_button);
        loginPasswordInput = (EditText) findViewById(R.id.login_password_input);
        loginPhoneInput = (EditText) findViewById(R.id.login_phone_input);
        loadingBar = new ProgressDialog(this);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.login_checkbox);
        Paper.init(this);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(homeIntent);
            }
        });

        System.out.println("create and check");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String phone = loginPhoneInput.getText().toString();
        String password = loginPasswordInput.getText().toString();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(String phone, String password) {
        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone = loginPhoneInput.getText().toString();

                if (snapshot.child(parentDbName).child(phone).exists()){

                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password)){

                            if (parentDbName.equals("Users")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("phone", phone);
                                editor.apply();

                                startActivity(homeIntent);

                            }

                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Aккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}