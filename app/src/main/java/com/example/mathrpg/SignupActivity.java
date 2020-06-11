package com.example.mathrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText inputEmail,inputPw,inputConfirmPw,inputUser;
    private RadioGroup inputGender;
    private Button btnRegister,btnLogIn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ConstraintLayout activityLayout;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputEmail=findViewById(R.id.signup_email);
        inputPw=findViewById(R.id.signup_pw);
        inputConfirmPw=findViewById(R.id.signup_confirmpw);
        inputUser=findViewById(R.id.signup_username);
        inputGender = findViewById(R.id.signup_gender);
        btnRegister=findViewById(R.id.sign_up_button);
        btnLogIn=findViewById(R.id.log_in_button);
        activityLayout = findViewById(R.id.signupCL);

        //Firebase initialization
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
                String email = inputEmail.getText().toString().trim();
                String password = inputPw.getText().toString().trim();
                String confirm_pass = inputConfirmPw.getText().toString().trim();
                String username = inputUser.getText().toString().trim();
                String gender = "";
                int selectedRadio = inputGender.getCheckedRadioButtonId();

                switch (selectedRadio){
                    case R.id.radioBoy:
                        gender = "boy";
                        break;
                    case R.id.radioGirl:
                        gender = "girl";
                        break;
                    default: gender = "";
                }

                if (validation(email,password,confirm_pass,username,gender)) {
                   registerUser(email,password,username,gender);
                }

        }
      });
    }

    /**
     * Create user at Firebase Auth and then store details to Firestore as well as Shared Preferences
     * @param email
     * @param password
     * @param username
     * @param gender
     */
    private void registerUser(final String email, String password, final String username, final String gender){
        //Show loading
        dialog = new Dialog(SignupActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        LinearLayout ll = new LinearLayout(SignupActivity.this);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(32,32,32,32);
        ProgressBar pb = new ProgressBar(SignupActivity.this);
        ll.addView(pb);
        builder.setTitle("Sign Up")
                .setMessage("Signing up...Please Wait...")
                .setCancelable(false)
                .setView(ll);
        dialog = builder.create();
        dialog.show();

        //Sign up user with Email in Firebase Auth
        auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(final AuthResult authResult) {
                        //Store details into database upon success Sign up
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", username);
                        data.put("email", email);
                        data.put("gender", gender);

                        //Saving into Firestore database upon success signup on Firebase Auth
                        db.collection("User").document(authResult.getUser().getUid().toString()).set(data)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Saving locally into Shared Preferences
                                        SharedPreferences prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("uid",authResult.getUser().getUid().toString());
                                        editor.putString("name",username);
                                        editor.putString("email",email);
                                        editor.putString("gender",gender);
                                        editor.putBoolean("login",true);
                                        editor.apply();
                                        dialog.dismiss();
                                        //Go to Home Page after success login and save to database
                                        startActivity(new Intent(SignupActivity.this,MainActivity.class));
                                        finish();
                                    }
                                })
                                //Failure in Firestore
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Snackbar.make(activityLayout,e.toString(),Snackbar.LENGTH_LONG).show();
                                    }
                                });

                    }
                })
                //Failure in Firebase Auth
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        String error = e.toString();
                        if(error.contains("in use")){
                            Snackbar.make(activityLayout,"Sign Up Failed!\nUser with the same Email Address already Exist!",Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            Snackbar.make(activityLayout,error,Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

    }

    /**
     * Sign up form validation
     * @param email
     * @param password
     * @param confirm_pass
     * @param username
     * @param gender
     * @return
     */
    private boolean validation(String email, String password, String confirm_pass, String username, String gender){
        if(TextUtils.isEmpty(username)){
            inputUser.setError("Enter user name");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Enter email address");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            inputPw.setError("Enter password");
            return false;
        }

        if (password.length() < 6) {
            inputPw.setError("Password too short");
            return false;
        }

        if (TextUtils.isEmpty(confirm_pass)) {
            inputConfirmPw.setError("Enter confirm password");
            return false;
        }

        if (!password.equals(confirm_pass)) {
            inputConfirmPw.setError("Confirm password not same with password");
            return false;
        }

        if(gender.length()<1){
            Snackbar.make(activityLayout,"Please select a gender",Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
