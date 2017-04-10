// IYYService.aidl
package com.example.tickcoder.yyservice;

// Declare any non-default types here with import statements

interface IYYService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
    String bindYYTest(String clientName);
    String askYYForAnswer(String clientName);
}
