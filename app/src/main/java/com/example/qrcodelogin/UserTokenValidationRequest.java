package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserTokenValidationRequest extends StringRequest {

    final static private String URL = "http://172.19.144.61:3000/user-token/validation";
    private Map<String, String> parameters;

    public UserTokenValidationRequest(String user_token, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("user_token",user_token);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
