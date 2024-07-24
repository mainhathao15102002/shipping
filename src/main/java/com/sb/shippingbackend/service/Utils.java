package com.sb.shippingbackend.service;

public class Utils {
    public static String getToken(String token)
    {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return null;
        }
        jwtToken = token.substring(7);
        return jwtToken;
    }

}
