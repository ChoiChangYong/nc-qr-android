package com.example.qrcodelogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class JoinRequest extends StringRequest {

    final static private String URL = "http://172.19.148.51:3000/join";
    private Map<String, String> parameters;

    public JoinRequest(String id, String password, String name, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        System.out.println("id : "+id);
        System.out.println("password : "+password);
        System.out.println("name : "+name);

        parameters = new HashMap<>();
        parameters.put("id",id);
        parameters.put("password",password);
        parameters.put("name",name);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
