package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeviceIdVerificationRequest extends StringRequest {

    final static private String URL = "http://172.20.51.188:3000/guid/validation";
    private Map<String, String> parameters;

    public DeviceIdVerificationRequest(String userSession, String deviceId, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userSession",userSession);
        parameters.put("deviceId",deviceId);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
