package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.components.rest.Payload;
import com.infoworks.lab.domain.datasources.MemCache;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerRepository;
import com.infoworks.lab.jsql.JsqlConfig;
import com.infoworks.lab.rest.models.ItemCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    private MemCache<Passenger> dataSource;

    @GetMapping("/rowCount")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @GetMapping
    public Passenger query(@RequestParam("key") String key){
        //TODO: Test with RestExecutor
        Passenger passenger = dataSource.read(key);
        return passenger;
    }

    @PostMapping @SuppressWarnings("Duplicates")
    public ItemCount insert(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        Passenger passenger = new Passenger();
        passenger.unmarshallingFromMap(payload.getPayload(), true);
        dataSource.put(passenger.getName(), passenger);
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @PutMapping @SuppressWarnings("Duplicates")
    public ItemCount update(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        Passenger passenger = new Passenger();
        passenger.unmarshallingFromMap(payload.getPayload(), true);
        dataSource.put(passenger.getName(), passenger);
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @DeleteMapping
    public Boolean delete(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        Passenger passenger = new Passenger();
        passenger.unmarshallingFromMap(payload.getPayload(), true);
        return dataSource.remove(passenger.getName()) != null;
    }

}
