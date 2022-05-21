package com.infoworks.lab.util.services;

import com.it.soul.lab.sql.entity.Entity;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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

    @Test
    public void testObject() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        Date dob = new Date();
        properties.putObject("obj.val", new Person("Subha", 23, dob));
        properties.save(false);
        //
        Person subha = properties.getObject("obj.val", Person.class);
        Assert.assertTrue(subha.name.equals("Subha"));
        Assert.assertTrue(subha.age == 23);
        Assert.assertTrue(subha.dob.getTime() == dob.getTime());
    }

    @Test
    public void testObject2() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        properties.putObject("obj.val.car", new Car("KHA-324490", 4));
        properties.flush();
        //
        Car drive4D = properties.getObject("obj.val.car", Car.class);
        Assert.assertTrue(drive4D.regNo.equals("KHA-324490"));
        Assert.assertTrue(drive4D.wheels == 4);
    }

    @Test
    public void testObjectFailed() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "app.properties");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        //
        iProperties properties = iProperties.create(absolutePath, null);
        Car drive4D = properties.getObject("obj.val", Car.class);
        Assert.assertTrue(drive4D.regNo == null);
        Assert.assertTrue(drive4D.wheels == 0);
    }

    private static class Person extends Entity{
        private String name;
        private int age;
        private Date dob;

        public Person() {}

        public Person(String name, int age, Date dob) {
            this.name = name;
            this.age = age;
            this.dob = dob;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }
    }

    private static class Car extends Entity{
        private String regNo;
        private int wheels;

        public Car() {}

        public Car(String regNo, int wheels) {
            this.regNo = regNo;
            this.wheels = wheels;
        }

        public String getRegNo() {
            return regNo;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        public int getWheels() {
            return wheels;
        }

        public void setWheels(int wheels) {
            this.wheels = wheels;
        }
    }

}