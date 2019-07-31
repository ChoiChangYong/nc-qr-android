package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class QRTokenValidationRequest extends StringRequest {

    final static private String URL = "http://172.20.51.188:3000/qrcode-auth";
    private Map<String, String> parameters;

    public QRTokenValidationRequest(String userSession, String qrcodeSession, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userSessionID", userSession);
        parameters.put("qrcodeSessionID", qrcodeSession);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
