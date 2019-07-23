package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GUIDValidationRequest extends StringRequest {

    final static private String URL = "http://172.19.144.61:3000/guid/validation";
    private Map<String, String> parameters;

    public GUIDValidationRequest(String uuid, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("uuid",uuid);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
