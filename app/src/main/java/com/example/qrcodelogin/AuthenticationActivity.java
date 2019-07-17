package com.example.qrcodelogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AuthenticationActivity extends AppCompatActivity {

    public static final String QRLOGIN_DEEP_LINK = "/qrlogin";
    String qrToken, userToken;
    AlertDialog alertDialog;
    String id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            finish();
        }

        openDeepLink(intent.getData());
    }

    private void openDeepLink(Uri deepLink) {
        String path = deepLink.getPath();

        Uri uri = getIntent().getData();
        qrToken = uri.getQueryParameter("qr_token");
        System.out.println("[openDeepLink] qrToken : "+qrToken);

        if (QRLOGIN_DEEP_LINK.equals(path)) {
            validationUserToken();
            validationQRToken();
        }
    }

    private void validationQRToken(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/qrcode-token/validation response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");
                    if (result.equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
                        builder.setMessage("QR코드 로그인 성공!!")
                                .setNegativeButton("확인", null);
                        alertDialog = builder.create();
                        alertDialog.show();
                    } else if (result.equals("0")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
                        builder.setMessage("만료된 QR코드입니다.\n웹페이지에서 다시 발급받아주세요!")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("name", name);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        QRTokenValidationRequest qrTokenValidationRequest = new QRTokenValidationRequest(userToken, qrToken, responseListener);
        RequestQueue queue = Volley.newRequestQueue(AuthenticationActivity.this);
        queue.add(qrTokenValidationRequest);
    }

    private void validationUserToken(){
        SharedPreferences mPrefs = getSharedPreferences("token", MODE_PRIVATE);
        userToken = mPrefs.getString("userToken", "empty");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/user-token/validation response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");

                    if (result.equals("1")) {
                        id = jsonResponse.getString("id");
                        name = jsonResponse.getString("name");
                    } else if (result.equals("0")) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        UserTokenValidationRequest userTokenValidationRequest = new UserTokenValidationRequest(userToken, responseListener);
        RequestQueue queue = Volley.newRequestQueue(AuthenticationActivity.this);
        queue.add(userTokenValidationRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
