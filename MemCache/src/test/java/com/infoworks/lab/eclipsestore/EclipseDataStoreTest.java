package com.infoworks.lab.eclipsestore;

import com.infoworks.lab.cache.models.Person;
import org.eclipse.store.storage.exceptions.StorageExceptionInitialization;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;

public class EclipseDataStoreTest {

    @Test
    public void initTest() throws Exception {
        String storagePath = "target/EclipseStore/EclipseDataStoreTest";
        //Single Cons:
        EclipseDataStore<String, Person> mData = new EclipseDataStore<>(storagePath + "/One");
        mData.close();
        //Double Cons: [Will Not Set]
        EclipseDataStore<String, Person> mData2 = new EclipseDataStore<>(storagePath + "/Two", true);
        mData2.close();
        //Three Cons: [Will Not Set]
        EclipseDataStore<String, Person> mData3 = new EclipseDataStore<>(storagePath + "/Three", true, Duration.ofMinutes(0));
        mData3.close();
        //Same: [Will Not Set]
        EclipseDataStore<String, Person> mData4 = new EclipseDataStore<>(storagePath + "/Four", true, Duration.ofMinutes(-1));
        mData4.close();
        //Same: [Will Not Set]
        EclipseDataStore<String, Person> mData5 = new EclipseDataStore<>(storagePath + "/Five", false, Duration.ofMinutes(20));
        mData5.close();
        //Three Cons: [Will Be Set]
        EclipseDataStore<String, Person> mData6 = new EclipseDataStore<>(storagePath + "/Six", true, Duration.ofMinutes(1));
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
        String storagePath = "target/EclipseStore/EclipseDataStoreTest";
        //
        EclipseDataStore<String, Person> mData = new EclipseDataStore<>(storagePath + "/TwoInstance");
        mData.put("james", new Person().setName("james").setAge(29));
        mData.save(false);
        //
        EclipseDataStore<String, Person> mData2 = new EclipseDataStore<>(storagePath + "/TwoInstance");
        Person person = mData2.read("james");
        Assert.assertNotNull(person);
    }

    @Test
    public void simpleStoreTest() throws Exception {
        String storagePath = "target/EclipseStore/EclipseDataStoreTest";
        //
        EclipseDataStore<String, Person> mData = new EclipseDataStore<>(storagePath + "/SimpleStore");
        mData.put("james", new Person().setName("james").setEmail("james@gmail.com").setAge(29));
        //mData.save(false);
        mData.close(); //close() -> also do save(...) then shutdown().
        //
        //After closing a running db, we can re-open and access any data:
        //
        EclipseDataStore<String, Person> mData2 = new EclipseDataStore<>(storagePath + "/SimpleStore");
        Person person = mData2.read("james");
        Assert.assertNotNull(person);
        System.out.println(person.getName());
        mData2.close();
    }

}