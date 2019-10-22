package com.cold.gradle;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MetaActivity extends AppCompatActivity {

    private TextView tvInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meta);
        tvInfo = findViewById(R.id.tv_info);
        tvInfo.setText(readMeta());
    }

    private String readMeta() {
        String metaStr = "";
        try {
            ApplicationInfo applicationInfo =
                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                metaStr = (String) applicationInfo.metaData.get("name"); // 这里为对应meta-data的name
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaStr;
    }
}
