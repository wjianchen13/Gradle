package com.cold.utils

class Log {

    private static boolean isLog = true;

    public static void println(String str) {
        if(isLog) {
            System.out.println("========================> " + str);
        }
    }
    
}

