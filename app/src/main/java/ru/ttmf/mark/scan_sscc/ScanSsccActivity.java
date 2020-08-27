package ru.ttmf.mark.scan_sscc;

import android.arch.lifecycle.Observer;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.positions.PositionsAdapter;
import ru.ttmf.mark.preference.PreferenceController;

public class ScanSsccActivity extends ScanActivity {

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.textView1)
    TextView textView1;

    @BindView(R.id.textView_count)
    TextView textView_count;

    @BindView(R.id.btn_clear)
    Button btn_clear;

    @BindView(R.id.btn_left)
    Button btn_left;

    @BindView(R.id.btn_right)
    Button btn_right;

    @BindView(R.id.ll_1_1)
    LinearLayout ll_1_1;

    private float current_screenBrightness;
    private int current_sscc_number;
    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sscc);

        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.scan_sscc2));

        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }
        });
        registerReceiver(intentBarcodeDataReceiver, intentFilter);

        current_sscc_number = 0;

        if (PreferenceController.getInstance().sscc_items_list.size() > 0)
        {
            textView_count.setText("1/" + PreferenceController.getInstance().sscc_items_list.size());
            textView1.setText(PreferenceController.getInstance().sscc_items_list.get(0).sscc);
            imageView.setImageBitmap(PreferenceController.getInstance().sscc_items_list.get(0).bitmap);
            current_sscc_number = 1;
        }
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

    public void onDecodeComplete(int symbology, int length, String barcode) {
        successfulScan(barcode);
    }

    private void successfulScan(String code)
    {
        DataMatrix matrix = new DataMatrix();
        try {
            DataMatrixHelpers.splitStr(matrix, code, 29);
            Toast toast;
            if (matrix.SGTIN() != null) {
                toast = Toast.makeText(getApplicationContext(), "Только SSCC коды!", Toast.LENGTH_LONG);
                toast.show();
            } else {

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode("00" + matrix.SSCC(), BarcodeFormat.CODE_128, ll_1_1.getWidth(), 120);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap2 = barcodeEncoder.createBitmap(bitMatrix);

                    boolean find = false;
                    for (Sscc_item var : PreferenceController.getInstance().sscc_items_list)
                    {
                        if (var.sscc.equals(matrix.SSCC()))
                        {
                            find = true;
                            toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG);
                            toast.show();
                            break;
                        }
                    }

                    if (!find) {
                        Sscc_item tmp_item = new Sscc_item();
                        textView1.setText(matrix.SSCC());
                        tmp_item.sscc = matrix.SSCC();
                        PreferenceController.getInstance().sscc_items_list.add(tmp_item);
                        tmp_item.number = PreferenceController.getInstance().sscc_items_list.size();
                        tmp_item.bitmap = bitmap2;
                        textView_count.setText(PreferenceController.getInstance().sscc_items_list.size() + "/" + PreferenceController.getInstance().sscc_items_list.size());
                        imageView.setImageBitmap(bitmap2);
                        current_sscc_number = PreferenceController.getInstance().sscc_items_list.size();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(),"Некорректный штрихкод!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
    }

    //rotation on 90
    /*Bitmap bitmap_r;
    float degrees = 90;
    Matrix matrix1 = new Matrix();
    matrix1.setRotate(degrees);
    bitmap_r = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix1, true);*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_clear)
    public void onbtn_clearyClick() {
        textView_count.setText("0/0");
        textView1.setText("");
        PreferenceController.getInstance().sscc_items_list.clear();
        imageView.setImageResource(android.R.color.transparent);
    }

    @OnClick(R.id.btn_left)
    public void onbtn_leftClick() {
        if (current_sscc_number > 1) {
            current_sscc_number = current_sscc_number - 1;
            textView_count.setText(current_sscc_number + "/" + PreferenceController.getInstance().sscc_items_list.size());
            textView1.setText(PreferenceController.getInstance().sscc_items_list.get(current_sscc_number - 1).sscc);
            imageView.setImageBitmap(PreferenceController.getInstance().sscc_items_list.get(current_sscc_number - 1).bitmap);
        }
    }

    @OnClick(R.id.btn_right)
    public void onbtn_rightClick() {
        if (current_sscc_number < PreferenceController.getInstance().sscc_items_list.size()) {
            current_sscc_number = current_sscc_number + 1;
            textView_count.setText(current_sscc_number + "/" + PreferenceController.getInstance().sscc_items_list.size());
            textView1.setText(PreferenceController.getInstance().sscc_items_list.get(current_sscc_number - 1).sscc);
            imageView.setImageBitmap(PreferenceController.getInstance().sscc_items_list.get(current_sscc_number - 1).bitmap);
        }
    }
}
