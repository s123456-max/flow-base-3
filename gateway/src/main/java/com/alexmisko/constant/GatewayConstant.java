package com.alexmisko.constant;

public class GatewayConstant {
    public static final String LOGIN_IN_URI = "/auth/signIn";
    public static final String LOGIN_UP_URI = "/auth/signUp";

    // public static final String VIDEO_LIST_URI = "^/video/[0-9]+$";
    public static final String VIDEO_LIST_URI = "/video";
    public static final String AUTH_CENTER_LOGIN_URL_FORMAT = "http://%s:%s/auth/signIn";
    public static final String AUTH_CENTER_REGISTER_URL_FORMAT = "http://%s:%s/auth/signUp";
}
