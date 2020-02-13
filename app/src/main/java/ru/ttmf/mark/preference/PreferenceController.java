package ru.ttmf.mark.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

import okhttp3.TlsVersion;

public class PreferenceController {
    private static PreferenceController instance;
    private SharedPreferences sharedPreferences;
    private static final String TOKEN = "token";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String URL = "url";
    private static final String MARK_ID = "mark_id";
    private static final String PROTOCOL = "secure_protocol";
    private static final String REMEMBER_AUTH = "remember_auth";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    private List<String> ttmf_mark_id_list = Arrays.asList("00000000003089",  // Тихорецкая 11
                                                           "00000000178920",  // Тихорецкая 11, корпус 1
                                                           "00000000178919"); // Тихорецкая 11, корпус 2

    public static PreferenceController getInstance() {
        if (instance == null) {
            instance = new PreferenceController();
        }
        return instance;
    }

    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences("TSD", Context.MODE_PRIVATE);
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public String getUserId() {
        return sharedPreferences.getString(USER_ID, "");
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, "");
    }

    public String getUrl() {
        return sharedPreferences.getString(URL, "https://med.ttmf.ru/mark/api/mark/");
    }

    public List<String> getTTMFmarkId() {
        return ttmf_mark_id_list;
    }

    public String getMarkId() {
        return sharedPreferences.getString(MARK_ID, "00000000000000");
    }

    public String getSecureProtocol() {
        return sharedPreferences.getString(PROTOCOL, TlsVersion.TLS_1_0.javaName());
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(USER_ID, userId).apply();
    }

    public void setUserName(String name) {
        sharedPreferences.edit().putString(USER_NAME, name).apply();
    }

    public void setUrl(String url) {
        sharedPreferences.edit().putString(URL, url).apply();
    }

    public void setMarkId(String mark_id) {
        sharedPreferences.edit().putString(MARK_ID, mark_id).apply();
    }

    public void setSecureProtocol(String protocol) {
        sharedPreferences.edit().putString(PROTOCOL, protocol).apply();
    }

    public void setRememberAuth(Boolean isRemember) {
        sharedPreferences.edit().putBoolean(REMEMBER_AUTH, isRemember).apply();
    }

    public boolean isRememberAuth() {
        return sharedPreferences.getBoolean(REMEMBER_AUTH, true);
    }

    public void setLogin(String login) {
        sharedPreferences.edit().putString(LOGIN, login).apply();
    }

    public String getLogin() {
        return sharedPreferences.getString(LOGIN, "");
    }

    public void setPassword(String password) {
        sharedPreferences.edit().putString(PASSWORD, password).apply();
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, "");
    }


    public void clear(){
        setUserId("");
        setUserName("");
        setToken("");
    }
}
