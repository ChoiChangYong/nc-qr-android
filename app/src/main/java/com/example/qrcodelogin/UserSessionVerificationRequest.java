package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserSessionVerificationRequest extends StringRequest {

    final static private String URL = "http://172.20.51.188:3000/session/verification";
    private Map<String, String> parameters;

    public UserSessionVerificationRequest(String userSession, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("sessionID", userSession);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
