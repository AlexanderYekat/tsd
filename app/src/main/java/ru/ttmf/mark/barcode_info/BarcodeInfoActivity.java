package ru.ttmf.mark.barcode_info;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;

public class BarcodeInfoActivity extends ScanActivity {

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sscc);

        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.scan_sscc2));
    }

    public void initToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}