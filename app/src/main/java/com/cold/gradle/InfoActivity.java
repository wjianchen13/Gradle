package com.cold.gradle;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }
    
    public void onMetaData(View v) {
        Toast.makeText(this, "meta-data: " + readMeta(), Toast.LENGTH_SHORT).show();
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

    public void onBuildConfig(View v) {
        Toast.makeText(this, "BuildConfig: " + BuildConfig.LOG_DEBUG, Toast.LENGTH_SHORT).show();
    }

}
