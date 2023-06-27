package ru.ttmf.mark;

import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import ru.ttmf.mark.preference.PreferenceController;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceController.getInstance().init(this);

        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "GooglePlayServicesRepairableException", Toast.LENGTH_LONG).show();

        } catch (GooglePlayServicesNotAvailableException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "GooglePlayServicesNotAvailableException", Toast.LENGTH_LONG).show();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "NoSuchAlgorithmException", Toast.LENGTH_LONG).show();

        } catch (KeyManagementException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "KeyManagementException", Toast.LENGTH_LONG).show();

        }
    }
}
