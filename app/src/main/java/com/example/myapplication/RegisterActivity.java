package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText uname, emailadd, pass, fname, lname;
    TextView goToLogIn;
    Button signUp;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uname = findViewById(R.id.uname);
        emailadd = findViewById(R.id.emailadd);
        pass = findViewById(R.id.pass);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        signUp = findViewById(R.id.btnSignUp);
        goToLogIn = findViewById(R.id.goToLogIn);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();
                String username = uname.getText().toString();
                String email = emailadd.getText().toString();
                String password = pass.getText().toString();


                if (TextUtils.isEmpty(username)) {
                    fname.setError("Username is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailadd.setError("Email address is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    pass.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    pass.setError("Password must be at least 6 characters");
                    return;
                }

                if (TextUtils.isEmpty(firstname)) {
                    fname.setError("First name is required");
                    return;
                }

                if (TextUtils.isEmpty(lastname)) {
                    lname.setError("Last name is required");
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
                        userId = auth.getCurrentUser().getUid();
                        DocumentReference docref = firestore.collection("users").document(userId);

                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("firstName", firstname);
                        user.put("lastName", lastname);

                        docref.set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "onSuccess: user profile is created for " + userId);

                                        Intent registeredIntent = new Intent(RegisterActivity.this, LogInActivity.class);
                                        startActivity(registeredIntent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "onFailure: " + e.toString());
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        goToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSign = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(intentToSign);
            }
        });
    }
}
