package com.example.qrcodelogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity{

    Button loginBtn, resetBtn;
    EditText idText, pwText;
    String redirectQrToken;
    SharedPreferences mPrefsUUID;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefsUUID = getSharedPreferences("uuid", MODE_PRIVATE);
        uuid = mPrefsUUID.getString("uuid", "empty");
        if(uuid!="empty")
            validationGUID();
        else
            validationUserToken();
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        redirectQrToken = intent.getStringExtra("redirectQrToken");
    }

    private void initView() {
        loginBtn = (Button) findViewById(R.id.login_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        idText = (EditText) findViewById(R.id.login_id_input);
        pwText = (EditText) findViewById(R.id.login_pw_input);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = idText.getText().toString();
                final String password = pwText.getText().toString();
                System.out.println("login onclick : " + id + ", " + password);

                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println("/login response : " + response);
                                JSONObject jsonResponse = new JSONObject(response);

                                String result = jsonResponse.getString("result");
                                if (result.equals("1")) {
                                    //Creating a shared preference
                                    SharedPreferences mPrefs = getSharedPreferences("token", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                    String userToken = jsonResponse.getString("user_token");
                                    prefsEditor.putString("userToken", userToken);
                                    prefsEditor.commit();

                                    if(redirectQrToken!=null){
                                        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                                        intent.putExtra("redirectQrToken", redirectQrToken);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        if(uuid!="empty")
                                            validationGUID();
                                        else
                                            validationUserToken();
                                    }
                                } else if (result.equals("0")) {
                                    String message = jsonResponse.getString("message");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage(message)
                                            .setPositiveButton("확인", null)
                                            .create()
                                            .show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    LoginRequest loginRequest = new LoginRequest(id, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = idText.getText().toString();
                final String password = pwText.getText().toString();
                System.out.println("login onclick : " + id + ", " + password);

                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println("/login response : " + response);
                                JSONObject jsonResponse = new JSONObject(response);

                                String result = jsonResponse.getString("result");
                                if (result.equals("1")) {

                                    SharedPreferences mPrefs = getSharedPreferences("uuid", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    prefsEditor.remove("uuid");
                                    prefsEditor.commit();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("디바이스 인증이 초기화되었습니다.")
                                            .setPositiveButton("확인", null)
                                            .create()
                                            .show();
                                } else if (result.equals("0")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("아이디 또는 비밀번호가 맞지 않습니다.")
                                            .setPositiveButton("확인", null)
                                            .create()
                                            .show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    LoginRequest loginRequest = new LoginRequest(id, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            }
        });
    }

    private void validationUserToken(){
        SharedPreferences mPrefs = getSharedPreferences("token", MODE_PRIVATE);
        String userToken = mPrefs.getString("userToken", "");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/user-token/validation response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");
                    if (result.equals("1")) {
                        String id = jsonResponse.getString("id");
                        String name = jsonResponse.getString("name");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("name", name);
                        startActivity(intent);
                        finish();

                    }
                    else if (result.equals("0")) {
                        String message = jsonResponse.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(message)
                                .setNegativeButton("확인", null)
                                .create()
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        UserTokenValidationRequest userTokenValidationRequest = new UserTokenValidationRequest(userToken, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(userTokenValidationRequest);
    }

    private void validationGUID(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/guid/validation response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");
                    if (result.equals("1")) {
                        String message = jsonResponse.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(message)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        validationUserToken();
                                    }
                                })
                                .create()
                                .show();
                    }
                    else if (result.equals("0")) {
                        String message = jsonResponse.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(message)
                                .setPositiveButton("확인", null)
                                .create()
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        GUIDValidationRequest guidValidationRequest = new GUIDValidationRequest(uuid, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(guidValidationRequest);
    }

}
