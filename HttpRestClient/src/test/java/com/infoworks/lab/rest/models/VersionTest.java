package com.infoworks.lab.rest.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VersionTest {
    @Test
    public void test1(){
        Version a = new Version("1.1");
        Version b = new Version("1.1.1");
        a.compareTo(b); // return -1 (a<b)
        a.equals(b);   // return false
    }

    @Test
    public void test2(){
        Version a = new Version("2.0");
        Version b = new Version("1.9.9");
        a.compareTo(b); // return 1 (a>b)
        a.equals(b) ;   // return false

    }

    @Test
    public void test3(){
        Version a = new Version("1.0");
        Version b = new Version("1");
        a.compareTo(b); // return 0 (a=b)
        a.equals(b);    // return true
    }

    @Test
    public void test4(){
        Version a = new Version("1");
        Version b = null;
        a.compareTo(b); // return 1 (a>b)
        a.equals(b) ;   // return false
    }

    @Test
    public void test5(){
        List<Version> versions = new ArrayList<Version>();
        versions.add(new Version("2"));
        versions.add(new Version("1.0.5"));
        versions.add(new Version("1.01.0"));
        versions.add(new Version("1.00.1"));
        Collections.min(versions).get(); // return min version
        Collections.max(versions).get(); // return max version

    }

    @Test
    public void test6(){
        // WARNING
        Version a = new Version("2.06");
        Version b = new Version("2.060");
        a.equals(b) ;   // return false
    }
}