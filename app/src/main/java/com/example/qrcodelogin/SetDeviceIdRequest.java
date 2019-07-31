package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class SetDeviceIdRequest extends StringRequest {

    final static private String URL = "http://172.20.51.188:3000/deviceId";
    private Map<String, String> parameters;

    public SetDeviceIdRequest(String userSession, String deviceId, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("sessionID",userSession);
        parameters.put("deviceId",deviceId);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
