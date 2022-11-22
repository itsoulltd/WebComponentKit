package com.infoworks.lab.cache;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class LRUCacheTest {

    @Test
    public void lruCacheClient() {
        //
        int cacheSize = 5;
        Map<Integer, String> mapVehicleNoAndOwner = new LRUCache<>(cacheSize);
        //
        mapVehicleNoAndOwner.put(1000, "Federer");
        mapVehicleNoAndOwner.put(2000, "Bradman");
        mapVehicleNoAndOwner.put(3000, "Jordan");
        mapVehicleNoAndOwner.put(4000, "Woods");
        mapVehicleNoAndOwner.put(5000, "Ali");

        System.out.println("1. Iterating initial cache of size = "+cacheSize);
        demoIterateCache(mapVehicleNoAndOwner);

        int key = 1000;
        System.out.printf("2. Accessting value at key: %d is %s\n",key,mapVehicleNoAndOwner.get(key));

        key = 3000;
        System.out.printf("3. Accessting value at key: %d is %s\n",key,mapVehicleNoAndOwner.get(key));

        System.out.println("4. Iterating cache after accessing its keys: ");
        demoIterateCache(mapVehicleNoAndOwner);

        key = 6000;
        String value = "Don";
        System.out.printf("5. Adding new entry to cache, key=%d, value=%s\n",key,value);
        mapVehicleNoAndOwner.put(6000, "Don");
        key = 7000;
        value = "Campbell";
        System.out.printf("6. Adding new entry to cache, key=%d, value=%s\n",key,value);
        mapVehicleNoAndOwner.put(7000, "Campbell");

        System.out.println("7. Iterating cache after adding entries beyond its size: ");
        demoIterateCache(mapVehicleNoAndOwner);
    }

    private void demoIterateCache(Map<Integer, String> mapVehicleNoAndOwner) {
        mapVehicleNoAndOwner.forEach((key, value) -> {
            System.out.println("Key:" + key + ", Value:" + value);
        });
    }

}