package com.cold.gradle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cold.gradle.interfaces.IFlavors;

public class MainActivity extends AppCompatActivity {

    private TextView tvFlavorName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFlavorName = findViewById(R.id.tv_flavor_name);
        setFlavorsName();
    }

    private void setFlavorsName() {
        IFlavors flavors = new MyFlavors();
        tvFlavorName.setText("Flavors Name: " + flavors.getFavorName());
    }
    
    public void onGetInfo(View view) {
        startActivity(new Intent(this, InfoActivity.class));
    }
    
    
}
