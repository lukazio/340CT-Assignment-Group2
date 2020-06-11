package com.example.mathrpg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {
    private EditText inputEmail,inputPw,inputConfirmPw,inputUser;
    private Button btnRegister,btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputEmail=findViewById(R.id.signup_email);
        inputPw=findViewById(R.id.signup_pw);
        inputConfirmPw=findViewById(R.id.signup_confirmpw);
        inputUser=findViewById(R.id.signup_username);
        btnRegister=findViewById(R.id.sign_up_button);
        btnLogIn=findViewById(R.id.log_in_button);

        //go to log in interface
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        //end of go to log in interface

        //register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                String password = inputPw.getText().toString().trim();
                String confirm_pass = inputConfirmPw.getText().toString().trim();
                String username = inputUser.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    inputUser.setError("Enter user name");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter email address");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid email format");
                    return;

                }

                if (TextUtils.isEmpty(password)) {
                    inputPw.setError("Enter password");
                    return;
                }

                if (password.length() < 6) {
                    inputPw.setError("Password too short");
                    return;
                }

                if (TextUtils.isEmpty(confirm_pass)) {
                    inputConfirmPw.setError("Enter confirm password");
                    return;
                }

                if (!password.equals(confirm_pass)) {
                    inputConfirmPw.setError("Confirm password not same with password");
                    return;
                }

        }
      });
    }
}
