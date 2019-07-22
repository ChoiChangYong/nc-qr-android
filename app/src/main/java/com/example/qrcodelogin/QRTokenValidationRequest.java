package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class QRTokenValidationRequest extends StringRequest {

    final static private String URL = "http://172.19.144.61:3000/qrcode-auth";
    private Map<String, String> parameters;

    public QRTokenValidationRequest(String user_token, String qr_token, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("user_token", user_token);
        parameters.put("qr_token", qr_token);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
