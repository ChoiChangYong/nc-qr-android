package com.example.qrcodelogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    Button loginBtn;
    EditText idText, pwText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        validationUserToken();
        initData();
        initView();
    }

    private void initData() {
    }

    private void initView() {
        loginBtn = (Button) findViewById(R.id.login_btn);
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

                                    validationUserToken();
                                } else if (result.equals("0")) {
                                    String message = jsonResponse.getString("message");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage(message)
                                            .setNegativeButton("AGAIN", null)
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
        String userToken = mPrefs.getString("userToken", "empty");

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
//                    else if (result.equals("0")) {
//                        String message = jsonResponse.getString("message");
//                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                        builder.setMessage(message)
//                                .setNegativeButton("확인", null)
//                                .create()
//                                .show();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        UserTokenValidationRequest userTokenValidationRequest = new UserTokenValidationRequest(userToken, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(userTokenValidationRequest);
    }
}
