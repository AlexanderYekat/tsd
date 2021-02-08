package ru.ttmf.mark.logs;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ttmf.mark.R;

import static ru.ttmf.mark.Helpers.Helpers.JsonBeautify;

public class LogsActivity extends AppCompatActivity {

    private ExpandableListAdapter expListAdapter;

    private List<String> expandableListTitle;
    private HashMap<String, String> expandableListDetail;

    @BindView(R.id.logs_toolbar)
    Toolbar toolbar;

    @BindView(R.id.expListView)
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        ButterKnife.bind(this);
        initToolbar(toolbar, "Ранее сканированные накладные");

        expandableListTitle = getScanLogFilesTitle();
        expandableListDetail = getScanLogFilesTitleDetails();
        expListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);

        expListView.setAdapter(expListAdapter);
    }

    /**
     * Получение названий всех файлов в папке MarkScanPosition
     *
     * @return коллекция строк с названиями
     */
    private List<String> getScanLogFilesTitle() {
        File root = new File(Environment.getExternalStorageDirectory(), "MarkScanPositions");
        List<String> files = new ArrayList<>();
        if (root.length() > 0) {
            files = Arrays.asList(root.list());
        }
        return files;
    }

    /**
     * Получение содержимых файлов в папке MarkScanPosition с названиями самих файлов
     *
     * @return коллекция HashMap с ключом (название файла) и значением (содержимое файла)
     */
    private HashMap<String, String> getScanLogFilesTitleDetails() {

        HashMap<String, String> files = new HashMap<String, String>();
        File root = new File(Environment.getExternalStorageDirectory(), "MarkScanPositions");

        BufferedReader br;
        String st;

        try {
            if (root.length() > 0) {
                for (File file :
                        root.listFiles()) {

                    br = new BufferedReader(new FileReader(file));
                    while ((st = br.readLine()) != null)
                        files.put(file.getName(), JsonBeautify(st));
                }
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return files;

    }

    /**
     * Инициализация верхней панели
     *
     * @param toolbar объект toolbar
     * @param title   наименование заголовка
     */
    public void initToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    //region EVENTS

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_log_clear)
    public void onBtnLogClear(){

    }
    //endregion
}
