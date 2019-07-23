package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class SetGUIDRequest extends StringRequest {

    final static private String URL = "http://172.19.144.61:3000/guid";
    private Map<String, String> parameters;

    public SetGUIDRequest(String userToken, String uniqueID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userToken",userToken);
        parameters.put("uuid",uniqueID);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
