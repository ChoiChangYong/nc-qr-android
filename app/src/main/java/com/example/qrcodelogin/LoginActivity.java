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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity{

    Button loginBtn, joinBtn, resetBtn;
    EditText idText, pwText;
    String redirectQrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences mPrefsUUID = getSharedPreferences("deviceId", MODE_PRIVATE);
        String deviceId = mPrefsUUID.getString("deviceId", "empty");
        System.out.println("deviceId : "+deviceId);
        if(deviceId!="empty")
            verifyDeviceId();
        else
            verifyUserSession();
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        redirectQrLogin = intent.getStringExtra("redirectQrLogin");
    }

    private void initView() {
        loginBtn = (Button) findViewById(R.id.login_btn);
        joinBtn = (Button) findViewById(R.id.join_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        idText = (EditText) findViewById(R.id.login_id_input);
        pwText = (EditText) findViewById(R.id.login_pw_input);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefsUUID = getSharedPreferences("deviceId", MODE_PRIVATE);
                final String deviceId = mPrefsUUID.getString("deviceId", "empty");

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
                                    SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                    String userSession = jsonResponse.getString("sessionID");
                                    prefsEditor.putString("userSession", userSession);
                                    prefsEditor.commit();

                                    if(redirectQrLogin!=null){
                                        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                                        intent.putExtra("redirectQrLogin", redirectQrLogin);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        if(deviceId!="empty")
                                            verifyDeviceId();
                                        else
                                            verifyUserSession();
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

        joinBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
               startActivity(intent);
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

                                    SharedPreferences mPrefsUUID = getSharedPreferences("deviceId", MODE_PRIVATE);
                                    mPrefsUUID.edit().clear().commit();

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

    private void verifyUserSession(){
        SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
        final String userSession = mPrefs.getString("userSession", "");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/session/verification response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");
                    if (result.equals("1")) {
                        getUserInfoBySessionID(userSession);
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

        UserSessionVerificationRequest userSessionVerificationRequest = new UserSessionVerificationRequest(userSession, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(userSessionVerificationRequest);
    }

    private void getUserInfoBySessionID(String userSession){
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("/users/:sessionID response : " + response);
                    JSONObject jsonResponse = response;

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
        String url = "http://172.20.51.188:3000/users/"+userSession;
        CommonGetHttpRequest commonGetHttpRequest = new CommonGetHttpRequest(Request.Method.GET, url, null, responseListener, null);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(commonGetHttpRequest);
    }

    private void verifyDeviceId(){
        SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String userSession = mPrefs.getString("userSession", "");

        SharedPreferences mPrefsUUID = getSharedPreferences("deviceId", MODE_PRIVATE);
        String deviceId = mPrefsUUID.getString("deviceId", "empty");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/deviceId/verification response : " + response);
                    final JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");

                    if (result.equals("1")) {
                        final String id = jsonResponse.getString("id");
                        final String name = jsonResponse.getString("name");
                        String message = jsonResponse.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(message)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("id", id);
                                    intent.putExtra("name", name);
                                    startActivity(intent);
                                    finish();
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

        DeviceIdVerificationRequest deviceIdVerificationRequest = new DeviceIdVerificationRequest(userSession, deviceId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(deviceIdVerificationRequest);
    }

}
