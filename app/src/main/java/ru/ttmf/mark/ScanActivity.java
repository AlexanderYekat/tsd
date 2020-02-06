package ru.ttmf.mark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import ru.ttmf.mark.common.DataWedgeConstants;

public abstract class ScanActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        DisableJanamScanner();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EnableJanamScanner();
    }


    public void DisableJanamScanner() {
        Intent TriggerButtonIntent = new Intent();
        TriggerButtonIntent.setAction(DataWedgeConstants.SOFTSCANTRIGGER);
        TriggerButtonIntent.putExtra(DataWedgeConstants.EXTRA_PARAMETER, DataWedgeConstants.DISABLE_TRIGGERBUTTON);
        this.sendBroadcast(TriggerButtonIntent);

        Intent BeepSoundIntent = new Intent();
        BeepSoundIntent.setAction(DataWedgeConstants.SOFTSCANTRIGGER);
        BeepSoundIntent.putExtra(DataWedgeConstants.EXTRA_PARAMETER, DataWedgeConstants.DISABLE_BEEP);
        this.sendBroadcast(BeepSoundIntent);

        Intent i = new Intent();
        i.setAction(DataWedgeConstants.SCANNERINPUTPLUGIN);
        i.putExtra(DataWedgeConstants.EXTRA_PARAMETER, "DISABLE_PLUGIN");
        this.sendBroadcast(i);
    }

    public void EnableJanamScanner() {
        Intent i = new Intent();
        i.setAction(DataWedgeConstants.SCANNERINPUTPLUGIN);
        i.putExtra(DataWedgeConstants.EXTRA_PARAMETER, "ENABLE_PLUGIN");
        this.sendBroadcast(i);

        Intent TriggerButtonIntent = new Intent();
        TriggerButtonIntent.setAction(DataWedgeConstants.SOFTSCANTRIGGER);
        TriggerButtonIntent.putExtra(DataWedgeConstants.EXTRA_PARAMETER, DataWedgeConstants.ENABLE_TRIGGERBUTTON);
        this.sendBroadcast(TriggerButtonIntent);

        Intent BeepSoundIntent = new Intent();
        BeepSoundIntent.setAction(DataWedgeConstants.SOFTSCANTRIGGER);
        BeepSoundIntent.putExtra(DataWedgeConstants.EXTRA_PARAMETER, DataWedgeConstants.ENABLE_BEEP);
        this.sendBroadcast(BeepSoundIntent);

        Intent VibrateIntent = new Intent();
        VibrateIntent.setAction(DataWedgeConstants.SOFTSCANTRIGGER);
        VibrateIntent.putExtra(DataWedgeConstants.EXTRA_PARAMETER, DataWedgeConstants.ENABLE_VIBRATE);
        this.sendBroadcast(VibrateIntent);
    }
}
