package com.example.qrcodelogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView welcomText;
    Button logoutBtn, deviceBtn;
    String id, name;
    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
    }

    private void initView() {
        welcomText = (TextView) findViewById(R.id.welcome);
        welcomText.setText(name+"님\n환영합니다 ^___^");

        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.remove("userSession");
                prefsEditor.commit();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        deviceBtn = (Button) findViewById(R.id.device_btn);
        deviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("디바이스를 등록하시면 해당 휴대폰이 아닌, 다른 휴대폰에서는 로그인이 불가능해집니다.\n등록하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setGUID();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create()
                        .show();
            }
        });

    }

    private void setGUID(){
        deviceId = UUID.randomUUID().toString();

        SharedPreferences mPrefsGUID = getSharedPreferences("deviceId", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefsGUID.edit();

        prefsEditor.putString("deviceId", deviceId);
        prefsEditor.commit();

        SharedPreferences mPrefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String userSession = mPrefs.getString("userSession", "empty");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/deviceId response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String msg = jsonResponse.getString("message");

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(msg)
                            .setPositiveButton("확인", null)
                            .create()
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        SetDeviceIdRequest setDeviceIdRequest = new SetDeviceIdRequest(userSession, deviceId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(setDeviceIdRequest);
    }
}
