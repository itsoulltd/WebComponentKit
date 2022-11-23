package com.infoworks.lab.cache;

import com.infoworks.lab.cache.models.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CacheDataSourceTest {

    CacheDataSource<Object> dataSource;
    CacheDataSource<Object> intDataSource;

    private void loadDataSource(){
        dataSource = new CacheDataSource<>(5);

        dataSource.put("p-1", new Person()
                .setName("John")
                .setEmail("john@gmail.com")
                .setAge(36)
                .setGender("male"));

        dataSource.put("p-2", new Person()
                .setName("Eve")
                .setEmail("eve@gmail.com")
                .setAge(21)
                .setGender("female"));

        dataSource.put("p-3", new Person()
                .setName("Mosses")
                .setEmail("mosses@gmail.com")
                .setAge(31)
                .setGender("male"));

        dataSource.put("p-4", new Person()
                .setName("Abraham")
                .setEmail("abraham@gmail.com")
                .setAge(31)
                .setGender("male"));

        dataSource.put("p-5", new Person()
                .setName("Ahmed")
                .setEmail("ahmed@gmail.com")
                .setAge(31)
                .setGender("male"));

        dataSource.put("p-6", new Person()
                .setName("Adam")
                .setEmail("adam@gmail.com")
                .setAge(31)
                .setGender("male"));
    }

    private void loadIntDataSource(){
        intDataSource = new CacheDataSource<>(10);

        intDataSource.add(new Person()
                .setName("John")
                .setEmail("john@gmail.com")
                .setAge(36)
                .setGender("male"));

        intDataSource.add(new Person()
                .setName("Eve")
                .setEmail("eve@gmail.com")
                .setAge(21)
                .setGender("female"));

        intDataSource.add(new Person()
                .setName("Mosses")
                .setEmail("mosses@gmail.com")
                .setAge(31)
                .setGender("male"));

        intDataSource.add(new Person()
                .setName("Abraham")
                .setEmail("abraham@gmail.com")
                .setAge(31)
                .setGender("male"));

        intDataSource.add(new Person()
                .setName("Ahmed")
                .setEmail("ahmed@gmail.com")
                .setAge(31)
                .setGender("male"));

        intDataSource.add(new Person()
                .setName("Adam")
                .setEmail("adam@gmail.com")
                .setAge(31)
                .setGender("male"));
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void readTest(){
        //
        loadDataSource();
        //
        System.out.println("===========================0-(datasource.size())======================");
        int maxItem = dataSource.size();
        Object[] readAll = dataSource.readSync(0, maxItem);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================1-2==========================");
        readAll = dataSource.readSync(1, 2);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================2-3=========================");
        readAll = dataSource.readSync(2, 3);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================0-3=========================");
        readAll = dataSource.readSync(0, 3);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================0-2========================");
        readAll = dataSource.readSync(0, 2);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("==========================100-10=======================");
        readAll = dataSource.readSync(100, 10);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("==========================0-0=======================");
        readAll = dataSource.readSync(0, 0);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("==========================1-0=======================");
        readAll = dataSource.readSync(1, 0);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("==========================(Asynch)===================");
        dataSource.readAsync(4, 1, (items) -> {
            System.out.println("==========================4-1=======================");
            for (Object p : items) {
                System.out.println(p.toString());
            }
        });
        //
        dataSource.readAsync(4, 2, (items) -> {
            System.out.println("==========================4-2=======================");
            for (Object p : items) {
                System.out.println(p.toString());
            }
        });
        //
        dataSource.readAsync(4, 3, (items) -> {
            System.out.println("==========================4-3=======================");
            for (Object p : items) {
                System.out.println(p.toString());
            }
        });
    }

    @Test
    public void addTest(){
        //
        loadIntDataSource();
        System.out.println(intDataSource.size());
        //
        System.out.println("===========================0-(datasource.size())======================");
        int maxItem = intDataSource.size();
        Object[] readAll = intDataSource.readSync(0, maxItem);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================1-2==========================");
        readAll = intDataSource.readSync(1, 2);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================2-3=========================");
        readAll = intDataSource.readSync(2, 3);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
        System.out.println("===========================0-3=========================");
        readAll = intDataSource.readSync(0, 3);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }
    }

    @Test
    public void additionFuncTest(){

        ////When String is Key:
        CacheDataSource<Person> dataSource = new CacheDataSource<>(5);

        dataSource.put("p-1", new Person()
                .setName("John")
                .setEmail("john@gmail.com")
                .setAge(36)
                .setGender("male"));

        dataSource.add(new Person()
                .setName("Abraham")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male"));

        System.out.println("===========================0-2==========================");
        Object[] readAll = dataSource.readSync(0, 2);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }

        ////When Integer is Key:
        CacheDataSource<Person> intDataSource = new CacheDataSource<>(5);

        intDataSource.put("key-01", new Person()
                .setName("John")
                .setEmail("john@gmail.com")
                .setAge(36)
                .setGender("male"));

        intDataSource.add(new Person()
                .setName("Abraham")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male"));

        System.out.println("===========================1-1==========================");
        Person person = intDataSource.read("key-01");
        System.out.println(person.toString());
        //
        System.out.println("");
    }

    @Test
    public void addDeleteTests(){
        CacheDataSource<Person> dataSource = new CacheDataSource<>(5);

        Person a = new Person()
                .setName("John")
                .setEmail("john@gmail.com")
                .setAge(36)
                .setGender("male");

        Person b = new Person()
                .setName("Abraham")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        if(!dataSource.contains(a)) dataSource.add(a);
        if(!dataSource.contains(b)) dataSource.add(b);

        System.out.println("===========================0-2==========================");
        Object[] readAll = dataSource.readSync(0, 2);
        for (Object p : readAll) {
            System.out.println(p.toString());
        }

        System.out.println("Size before delete: " + dataSource.size());
        if(dataSource.contains(a)) dataSource.delete(a);
        System.out.println("Size after delete: " + dataSource.size());

    }

    @Test
    public void cacheEvictionTest() {

        CacheDataSource<Person> dataSource = new CacheDataSource<>(3);

        Person a = new Person()
                .setName("Abraham-a")
                .setEmail("Abraham@gmail.com")
                .setAge(36)
                .setGender("male");

        Person b = new Person()
                .setName("Abraham-b")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        Person c = new Person()
                .setName("Abraham-c")
                .setEmail("Abraham@gmail.com")
                .setAge(36)
                .setGender("male");

        Person d = new Person()
                .setName("Abraham-d")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        Person e = new Person()
                .setName("Abraham-e")
                .setEmail("Abraham@gmail.com")
                .setAge(36)
                .setGender("male");

        Person f = new Person()
                .setName("Abraham-f")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        dataSource.add(a);
        dataSource.add(c);
        dataSource.add(b);
        dataSource.fetch(0, 3).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        dataSource.add(d);
        dataSource.add(f);
        dataSource.fetch(0, 3).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        dataSource.add(a, e, c);
        dataSource.fetch(0, 3).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        //dataSource.read(String.valueOf(c.hashCode()));
        dataSource.read(String.valueOf(a.hashCode()));
        dataSource.fetch(0, 3).forEach(person -> System.out.println(person.getName()));
    }

    @Test
    public void cacheAccessTest() {

        CacheDataSource<Person> dataSource = new CacheDataSource<>(5);

        Person a = new Person()
                .setName("Abraham-a")
                .setEmail("Abraham@gmail.com")
                .setAge(36)
                .setGender("male");

        Person b = new Person()
                .setName("Abraham-b")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        Person c = new Person()
                .setName("Abraham-c")
                .setEmail("Abraham@gmail.com")
                .setAge(36)
                .setGender("male");

        Person d = new Person()
                .setName("Abraham-d")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        Person e = new Person()
                .setName("Abraham-e")
                .setEmail("Abraham@gmail.com")
                .setAge(36)
                .setGender("male");

        Person f = new Person()
                .setName("Abraham-f")
                .setEmail("Abraham@gmail.com")
                .setAge(45)
                .setGender("male");

        dataSource.add(a,b,c,d,e);
        dataSource.fetch(0, 5).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        dataSource.read(String.valueOf(b.hashCode()));
        dataSource.read(String.valueOf(a.hashCode()));
        dataSource.fetch(0, 5).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        dataSource.add(f);
        dataSource.fetch(0, 5).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        dataSource.readSync(1, 2);
        dataSource.fetch(0, 5).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");

        dataSource.replace(String.valueOf(a.hashCode()), new Person().setName("Abraham-g").setAge(45).setEmail("").setGender(""));
        dataSource.fetch(0, 5).forEach(person -> System.out.println(person.getName()));
        System.out.println("===================================");
    }

}