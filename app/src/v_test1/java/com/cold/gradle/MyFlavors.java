package com.cold.gradle;

import com.cold.gradle.interfaces.IFlavors;

public class MyFlavors implements IFlavors {

    @Override
    public String getFavorName() {
        return "test1";
    }
}
