package com.example.qrcodelogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AuthenticationActivity extends AppCompatActivity {

    public static final String QRLOGIN_DEEP_LINK = "/qrlogin";
    String qrcodeSession, userSession;
    AlertDialog alertDialog;
    String id, name;
    Uri uri;
    String redirectQrLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Intent intent = getIntent();
//        if (intent == null || intent.getData() == null) {
//            finish();
//        }

        redirectQrLogin = intent.getStringExtra("redirectQrLogin");
        System.out.println("redirectQrLogin : "+redirectQrLogin);
        if(redirectQrLogin!=null){
            qrcodeSession = redirectQrLogin;
            SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
            userSession = mPrefs.getString("userSession", "empty");

            validationQRToken();
        }
        else {
            openDeepLink(intent.getData());
        }
    }

    private void openDeepLink(Uri deepLink) {
        String path = deepLink.getPath();

        uri = getIntent().getData();
        System.out.println("[openDeepLink] uri : "+uri);
        qrcodeSession = uri.getQueryParameter("key");
        redirectQrLogin = qrcodeSession;
        System.out.println("[openDeepLink] qrcodeSession : "+qrcodeSession);

        if (QRLOGIN_DEEP_LINK.equals(path)) {
            validationQRToken();
        }
    }

    private void validationQRToken(){
        SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
        userSession = mPrefs.getString("userSession", "empty");
        System.out.println("userSession : "+userSession);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/qrcode-auth response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");
                    if (result.equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
                        builder.setMessage("QR코드 로그인 성공!!")
                                .setPositiveButton("확인", null);
                        alertDialog = builder.create();
                        alertDialog.show();
                    } else if (result.equals("2")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
                        builder.setMessage("만료된 QR코드입니다.\n웹페이지에서 다시 발급받아주세요!")
                                .setPositiveButton("확인", null);
                        alertDialog = builder.create();
                        alertDialog.show();
                    } else if (result.equals("3")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
                        builder.setMessage("QR코드 로그인 실패!")
                                .setPositiveButton("확인", null);
                        alertDialog = builder.create();
                        alertDialog.show();
                    } else if (result.equals("0")) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("redirectQrLogin", redirectQrLogin);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        QRTokenValidationRequest qrTokenValidationRequest = new QRTokenValidationRequest(userSession, qrcodeSession, responseListener);
        RequestQueue queue = Volley.newRequestQueue(AuthenticationActivity.this);
        queue.add(qrTokenValidationRequest);
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
