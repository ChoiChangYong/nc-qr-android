package com.example.qrcodelogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class JoinNickActivity extends AppCompatActivity {

    Button joinBtn;
    EditText nickText;
    String password, nickname;
    String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_nick);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
    }

    private void initView() {
        nickText = (EditText) findViewById(R.id.join_nick_input);
        joinBtn = (Button) findViewById(R.id.join_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = nickText.getText().toString();
                if (nickname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    requestJoin();
                }
            }
        });
    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> values, String uniqueID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray uuidJson = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            uuidJson.put(values.get(i));
        }
        uuidJson.put(uniqueID);
        editor.putString(key, uuidJson.toString());
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> uuids = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray uuidJson = new JSONArray(json);
                for (int i = 0; i < uuidJson.length(); i++) {
                    String uuid = uuidJson.optString(i);
                    uuids.add(uuid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return uuids;
    }

    private void requestJoin(){
        uniqueID = UUID.randomUUID().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("/join response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);

                    String result = jsonResponse.getString("result");
                    if (result.equals("1")) {
                        setStringArrayPref(JoinNickActivity.this, "uuid", getStringArrayPref(JoinNickActivity.this, "uuid"), uniqueID);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (result.equals("0")) {
                        String message = jsonResponse.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinNickActivity.this);
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

        JoinRequest joinRequest = new JoinRequest(uniqueID, password, nickname, responseListener);
        RequestQueue queue = Volley.newRequestQueue(JoinNickActivity.this);
        queue.add(joinRequest);
    }
}
