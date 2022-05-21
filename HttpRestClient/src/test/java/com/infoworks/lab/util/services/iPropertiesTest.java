package com.infoworks.lab.util.services;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class iPropertiesTest {

    @Test
    public void test() throws URISyntaxException {
        Path resourceDirectory = Paths.get("src","test","resources","app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        String val = properties.read("last.read");
        Assert.assertTrue(val.equals("100"));
        System.out.println(val);
    }

    @Test
    public void testRead() {
        Path resourceDirectory = Paths.get("src","test","resources","app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        Map data = new HashMap();
        data.put("last.read", "100");
        iProperties properties = iProperties.create(absolutePath, data);
        String val = properties.read("last.read");
        Assert.assertTrue(val.equals("100"));
        System.out.println(val);
    }

    @Test
    public void testAddAndFlush() {
        Path resourceDirectory = Paths.get("src","test","resources","app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        String val = properties.read("last.read");
        Assert.assertTrue(val.equals("100"));
        //
        int nVal = 100 + (new Random().nextInt(9) + 1);
        properties.put("last.read.new", nVal + "");
        properties.flush();
        val = properties.read("last.read.new");
        Assert.assertTrue(val.equals(nVal + ""));
        System.out.println(val);
    }

    @Test
    public void testFalse() {
        Path resourceDirectory = Paths.get("src","test","resources","app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        String val = properties.read("last.read.vb");
        Assert.assertTrue(val.equals(""));
        System.out.println(val);
    }

    @Test
    public void testReplace() {
        Path resourceDirectory = Paths.get("src","test","resources","app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        Map data = new HashMap();
        data.put("last.read", "100");
        iProperties properties = iProperties.create(absolutePath, data);
        properties.replace("last.read", "109");
        String val = properties.read("last.read");
        Assert.assertTrue(val.equals("109"));
        System.out.println(val);
    }

    @Test
    public void testRemove() {
        Path resourceDirectory = Paths.get("src","test","resources","app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        Map data = new HashMap();
        data.put("last.read", "100");
        iProperties properties = iProperties.create(absolutePath, data);
        properties.remove("last.read");
        String val = properties.read("last.read");
        Assert.assertTrue(val.equals(""));
        System.out.println(val);
    }

    @Test
    public void testFileName() {
        Path resourceDirectory = Paths.get("src", "test", "resources", "app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        Assert.assertTrue(properties.fileName().equals("app.properties"));
    }

}