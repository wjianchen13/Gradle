package com.cold.gradle;

public class AsmTest {
    
    private int a;
    private int b;
    
    public AsmTest() {
        
    }
    
    public void show() {
        System.out.println("=======================> a: " + a + "  b: " + b);
    }
    
    class Inner {
        int c;
        
        public Inner() {
            
        }
        
        public void show() {
            System.out.println("=======================> inner c: " + c);
        }
    }
}
