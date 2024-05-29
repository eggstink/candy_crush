package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {
    EditText unEmail, pass;
    TextView goToSignUp;
    Button logIn;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    public static String currUsername;

    public static String getCurrUsername() {
        return currUsername;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        unEmail = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        logIn = findViewById(R.id.btnLogIn);
        goToSignUp = findViewById(R.id.goToSignUp);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = unEmail.getText().toString();
                String password = pass.getText().toString();

                if(TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)) {
                    if (TextUtils.isEmpty(emailAddress) && TextUtils.isEmpty(password)) {
                        unEmail.setError("Please enter your email");
                        pass.setError("Please enter your password");
                        return;
                    }

                    if (TextUtils.isEmpty(emailAddress)){
                        unEmail.setError("Please enter your email");
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        pass.setError("Please enter your password");
                        return;
                    }
                }

                auth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String loggedUser = auth.getCurrentUser().getUid();
                            saveUserID(loggedUser);
                            startActivity(new Intent(LogInActivity.this, SelectLvlActivity.class));
                        } else {
                            Toast.makeText(LogInActivity.this, "You entered a wrong email/password", Toast.LENGTH_SHORT).show();
//                            firestore.collection("users")
//                            .whereEqualTo("username", emailAddress)
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        boolean found = false;
//                                        for (QueryDocumentSnapshot doc : task.getResult()) {
//                                            found = true;
//                                            String dbPass = doc.getString("password");
//                                            if (password.equals(dbPass)) {
//                                                startActivity(new Intent(LogInActivity.this, HomeActivity.class));
//                                                break;
//                                            } else {
//                                                pass.setError("Incorrect password");
//                                            }
//                                        }
//                                        if (!found) {
//                                            unEmail.setError("User not found");
//                                        }
//                                    } else {
//                                        unEmail.setError("Error retrieving user information");
//                                    }
//                                }
//                            });
                        }
                    }
                });
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSign = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(intentToSign);
            }
        });
    }

    private void saveUserID(String userID) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loggedID", userID);
        editor.apply();
    }
}