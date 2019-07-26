package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class LoginRequest extends StringRequest {

    final static private String URL = "http://172.19.148.51:3000/login";
    private Map<String, String> parameters;

    public LoginRequest(String id, String password, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("id",id);
        parameters.put("password",password);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
