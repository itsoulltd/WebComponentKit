package com.infoworks.lab.microstream;

import com.infoworks.lab.cache.models.Person;
import one.microstream.storage.exceptions.StorageExceptionInitialization;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.Assert.*;

public class MicroDataStoreTest {

    @Test
    public void initTest() throws Exception {
        //String storagePath = "/Users/Public/MicroStream/MicroDataStoreTest";
        String storagePath = "C:\\Users\\Public\\MicroStream\\MicroDataStoreTest";
        //Single Cons:
        MicroDataStore<String, Person> mData = new MicroDataStore<>(storagePath + "\\One");
        mData.close();
        //Double Cons: [Will Not Set]
        MicroDataStore<String, Person> mData2 = new MicroDataStore<>(storagePath + "\\Two", true);
        mData2.close();
        //Three Cons: [Will Not Set]
        MicroDataStore<String, Person> mData3 = new MicroDataStore<>(storagePath + "\\Three", true, Duration.ofMinutes(0));
        mData3.close();
        //Same: [Will Not Set]
        MicroDataStore<String, Person> mData4 = new MicroDataStore<>(storagePath + "\\Four", true, Duration.ofMinutes(-1));
        mData4.close();
        //Same: [Will Not Set]
        MicroDataStore<String, Person> mData5 = new MicroDataStore<>(storagePath + "\\Five", false, Duration.ofMinutes(20));
        mData5.close();
        //Three Cons: [Will Be Set]
        MicroDataStore<String, Person> mData6 = new MicroDataStore<>(storagePath + "\\Six", true, Duration.ofMinutes(1));
        mData6.close();
    }

    /**
     * https://docs.microstream.one/manual/storage/application-life-cycle.html
     * The consequence of this is:
     * If two EmbeddedStorageManager instances are started,
     * each one with a different location for its persistend data,
     * then the application has two live databases! If three or ten or 100 are started,
     * then that’s the number of live databases the application has.
     * There is no limit and no conflict between different databases inside the same application process.
     * ****************
     * The only important thing is that no two running StorageManagers can access the same data location.
     * ****************
     */
    @Test(expected = StorageExceptionInitialization.class)
    public void TwoInstanceToSameStore() {
        //String storagePath = "/Users/Public/MicroStream/MicroDataStoreTest";
        String storagePath = "C:\\Users\\Public\\MicroStream\\MicroDataStoreTest";
        //
        MicroDataStore<String, Person> mData = new MicroDataStore<>(storagePath + "\\TwoInstance");
        mData.put("james", new Person().setName("james").setAge(29));
        mData.save(false);
        //
        MicroDataStore<String, Person> mData2 = new MicroDataStore<>(storagePath + "\\TwoInstance");
        Person person = mData2.read("james");
        Assert.assertNotNull(person);
    }

    @Test
    public void simpleStoreTest() throws Exception {
        //String storagePath = "/Users/Public/MicroStream/MicroDataStoreTest";
        String storagePath = "C:\\Users\\Public\\MicroStream\\MicroDataStoreTest";
        //
        MicroDataStore<String, Person> mData = new MicroDataStore<>(storagePath + "\\SimpleStore");
        mData.put("james", new Person().setName("james").setEmail("james@gmail.com").setAge(29));
        //mData.save(false);
        mData.close(); //close also do save before shutdown.
        //
        //After closing a running db, we can re-open and access any data:
        //
        MicroDataStore<String, Person> mData2 = new MicroDataStore<>(storagePath + "\\SimpleStore");
        Person person = mData2.read("james");
        Assert.assertNotNull(person);
        System.out.println(person.getName());
        mData2.close();
    }

}