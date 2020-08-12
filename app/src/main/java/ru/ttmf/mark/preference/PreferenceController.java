package ru.ttmf.mark.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

import okhttp3.TlsVersion;
import ru.ttmf.mark.R;

public class PreferenceController {
    private static PreferenceController instance;
    private SharedPreferences sharedPreferences;
    private static final String TOKEN = "token";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String URL = "url";
    private static final String PROTOCOL = "secure_protocol";
    private static final String REMEMBER_AUTH = "remember_auth";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String VERSION = "version";
    private static final String LAST_VERSION = "last_version";
    private static final Integer cur_version = 124;

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

    public Integer getVersion() {
        return sharedPreferences.getInt(VERSION, cur_version);
    }

    public Integer getLastVersion() {
        return sharedPreferences.getInt(LAST_VERSION,0);
    }

    public String getUrl() {
        return sharedPreferences.getString(URL, "https://med.ttmf.ru/mark/api/mark/");
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

    public void setVersion(Integer version) {
        sharedPreferences.edit().putInt(VERSION, version).apply();
    }

    public void setLastVersion(Integer last_version) {
        sharedPreferences.edit().putInt(LAST_VERSION, last_version).apply();
    }

    public void setUrl(String url) {
        sharedPreferences.edit().putString(URL, url).apply();
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


    public void clear() {
        setUserId("");
        setUserName("");
        setToken("");
    }
}
