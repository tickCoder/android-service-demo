// IXXService.aidl
package com.example.tickcoder.xxservice;

// Declare any non-default types here with import statements

interface IXXService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
    String bindXXTest(String clientName);
    String askXXForAnswer(String clientName);
}
