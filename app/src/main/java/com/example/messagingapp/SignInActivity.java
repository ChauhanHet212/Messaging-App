package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.messagingapp.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog dialog1;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }

        binding.getotpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.edtNumber.getText().toString().isEmpty()){
                    if (binding.edtNumber.getText().toString().length() == 10){
                        dialog1 = new ProgressDialog(SignInActivity.this);
                        dialog1.setTitle("Please Wait");
                        dialog1.setMessage("Sending OTP...");
                        dialog1.setCancelable(false);
                        dialog1.show();
                        phoneSignIn();
                    } else {
                        binding.edtNumber.setError("Enter Proper Number");
                    }
                } else {
                    binding.edtNumber.setError("Enter Number");
                }
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.edtOtp.getText().toString().isEmpty()){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, binding.edtOtp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    binding.edtOtp.setError("Enter OTP");
                }
            }
        });
    }

    private void phoneSignIn() {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
                dialog1.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog1.dismiss();
                System.out.println(e.getLocalizedMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                binding.registerBtn.setEnabled(true);
                dialog1.dismiss();
            }
        };


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + binding.edtNumber.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        ProgressDialog dialog = new ProgressDialog(SignInActivity.this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Verifying OTP...");
        dialog.setCancelable(false);
        dialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                startActivity(new Intent(SignInActivity.this, AddProfileActivity.class).putExtra("Number", binding.edtNumber.getText().toString()));
                            } else {
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            }
                            finish();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}