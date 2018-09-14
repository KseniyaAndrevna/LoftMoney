package com.kseniyaa.loftmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 99;
    private static final String TAG = "Error";
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private Api api;
    private String user_id;
    private String token;
    private SharedPreferences sharedPreferences;
    public static final String SAVE_TOKEN = "token";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        api = ((App) getApplication()).getApi();

        Button signInButton = findViewById(R.id.sign_in_button);

        TextView version = findViewById(R.id.version);
        version.setText(getString(R.string.tv_version_title) + BuildConfig.VERSION_NAME + "\n" + getString(R.string.tv_version_year));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
    }


    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                user_id = account.getId();
                getToken();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication Failed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    public void getToken() {
        Call<LinkedHashMap<String,String>> call = api.getAuthToken(user_id);
        call.enqueue(new Callback<LinkedHashMap<String,String>>() {
            @Override
            public void onResponse(Call<LinkedHashMap<String,String>> call, Response<LinkedHashMap<String,String>> response) {
                assert response.body() != null;
                LinkedHashMap authData = response.body();
                token = (String) authData.get("auth_token");
                saveToken(token);
            }

            @Override
            public void onFailure(Call<LinkedHashMap<String,String>> call, Throwable t) {

            }
        });
    }
    
    public void saveToken(String token) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVE_TOKEN, token);
        editor.apply();
    }
}
