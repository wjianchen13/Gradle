package com.cold.utils

class Log {

    private static boolean isLog = true;

    private static void println(String str) {
        if(isLog) {
            System.out.println("========================> " + str);
        }
    }
    
}

