package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/14.
 */

public class Token implements Serializable {
    private static final long serialVersionUID = 2L;

    private String token;
    private String uid;

    public static List<Token> tokens = new ArrayList<Token>();

    public Token(){
    }

    public Token(String token,String uid){
        super();
        this.token=token;
        this.uid=uid;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }
    public String getUid(){
        return  uid;
    }
    public void setUid(String uid){
        this.uid=uid;
    }
}
