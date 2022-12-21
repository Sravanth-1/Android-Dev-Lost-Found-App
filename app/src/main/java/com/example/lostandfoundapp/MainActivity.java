package com.example.lostandfoundapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    TextView VerifyMsg;
    Button resendCode;
    Button resetPassLocal;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    Button postLost;
    Button postFound,postLostFeed,postFoundFeed,mylostpost,myfoundpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VerifyMsg = findViewById(R.id.notVerified);
        resendCode = findViewById(R.id.verifyBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        resetPassLocal = findViewById(R.id.changePassword);
        user = fAuth.getCurrentUser();
        postLost = findViewById(R.id.lostBtn);
        postFound = findViewById(R.id.foundBtn);
        postLostFeed = findViewById(R.id.checkLost);
        postFoundFeed = findViewById(R.id.checkFound);

        if(!user.isEmailVerified()){
            VerifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification email has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent" + e.getMessage());
                        }
                    });
                }
            });

        }
            resetPassLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText resetPassword = new EditText(view.getContext());
                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                    passwordResetDialog.setTitle("Reset Password?");
                    passwordResetDialog.setMessage("Enter the new password > 6 characters long");
                    passwordResetDialog.setView(resetPassword);

                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // extract the email and send reset link

                            String newPassword = resetPassword.getText().toString();
                            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Password has been changed successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Password set Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the dialogue
                        }
                    });
                    passwordResetDialog.create().show();
                }
            });
        postLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),postLost.class));
            }
        });
        postFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),PostFound.class));
            }
        });
        postLostFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),dataforlost.class));
            }
        });
        postFoundFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),dataforfound.class));
            }
        });
    }
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();//logout of the user
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

}