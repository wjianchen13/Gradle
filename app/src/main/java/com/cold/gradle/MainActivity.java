package com.cold.gradle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cold.annotation.Cal;
import com.cold.gradle.interfaces.IFlavors;

public class MainActivity extends AppCompatActivity {

    private ImageView imgvTest;
    private TextView tvFlavorName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgvTest = findViewById(R.id.imgv_test);
        tvFlavorName = findViewById(R.id.tv_flavor_name);
        setImage();
        setFlavorsName();
    }
    
    private void setImage() {
        imgvTest.setImageResource(R.drawable.ic_test);
    }

    private void setFlavorsName() {
        IFlavors flavors = new MyFlavors();
        tvFlavorName.setText("Flavors Name: " + flavors.getFavorName());
    }
    @Cal
    public void onGetInfo(View view) {
        startActivity(new Intent(this, InfoActivity.class));
    }
    
    // 测试transform asm插入代码
//    @Cal
    public void test() {
        
    }
}
