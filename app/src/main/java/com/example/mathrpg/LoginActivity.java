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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPass;
    private Button btnSignup, btnLogin, btnlogout, btnlogin2;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ConstraintLayout activityLayout;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.login_email);
        inputPass = findViewById(R.id.login_pw);
        btnLogin = findViewById(R.id.login_button);
        btnSignup = findViewById(R.id.signup_button);
        btnlogout = findViewById(R.id.btn_logout);
        activityLayout = findViewById(R.id.loginCL);
        btnlogin2 = findViewById(R.id.btn_login);

        //Firebase initializations
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //go to sign up interface
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        //end of go to sign up interface

        //log in button settings
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                final String password = inputPass.getText().toString();

                if(validation(email,password)){
                    loginUser(email,password);
                }

            }
        });
    }

    /**
     * Sign in user with Firebase Auth and Retrieve data from Firestore, then save to Shared Preferences
     * @param email
     * @param password
     */
    private void loginUser(final String email, String password){
        //Show loading
        dialog = new Dialog(LoginActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        LinearLayout ll = new LinearLayout(LoginActivity.this);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(32,32,32,32);
        ProgressBar pb = new ProgressBar(LoginActivity.this);
        ll.addView(pb);
        builder.setTitle("Log In")
                .setMessage("Logging in...Please Wait...")
                .setCancelable(false)
                .setView(ll);
        dialog = builder.create();
        dialog.show();

        //Sign in user with Firebase Auth
        auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //Retrieve data from Firestore
                        db.collection("Users").document(authResult.getUser().getUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        //Saving locally into Shared Preferences
                                        SharedPreferences prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("uid",task.getResult().getId().toString());
                                        editor.putString("name",task.getResult().getString("name"));
                                        editor.putString("email",task.getResult().getString("email"));
                                        editor.putString("gender",task.getResult().getString("gender"));
                                        editor.putBoolean("login",true);
                                        editor.apply();
                                        dialog.dismiss();
                                        //Go to Home Page after success login and save to database
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        btnlogout.setVisibility(View.VISIBLE);
                                        btnlogin2.setVisibility(View.INVISIBLE);
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
                        if(error.contains("InvalidUser") || error.contains("disabled") || error.contains("password")) {
                            if (error.contains("InvalidUser")) {
                                Snackbar.make(activityLayout, "Sign In Failed!\nNo User Found in this Email Address!", Snackbar.LENGTH_LONG).show();
                            }
                            if (error.contains("password")) {
                                Snackbar.make(activityLayout, "Sign In Failed!\nWrong Password!", Snackbar.LENGTH_LONG).show();
                            }
                            if (error.contains("disabled")) {
                                Snackbar.make(activityLayout, "Sign In Failed!\nThis Account has been Disabled!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Snackbar.make(activityLayout,error,Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Login Form Validation
     * @param email
     * @param password
     * @return
     */
    private boolean validation(String email, String password){
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Enter email address");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid email format");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            inputPass.setError("Enter password");
            return false;
        }
        if (password.length()<6) {
            inputPass.setError("Password has at least 6 Characters!");
            return false;
        }

        return true;
    }

}