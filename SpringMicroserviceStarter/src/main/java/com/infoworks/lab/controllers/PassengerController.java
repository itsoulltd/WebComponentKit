package com.infoworks.lab.controllers;

import com.infoworks.lab.components.rest.Payload;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerRepository;
import com.infoworks.lab.jsql.JsqlConfig;
import com.infoworks.lab.rest.models.ItemCount;
import com.it.soul.lab.data.simple.SimpleDataSource;
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
    private SimpleDataSource<String, Passenger> dataSource;

    @GetMapping("/rowCount")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @GetMapping
    public List<Passenger> query(@RequestParam("limit") Integer limit
            , @RequestParam("offset") Integer offset){
        //TODO: Test with RestExecutor
        List<Passenger> passengers = Arrays.asList(dataSource.readSynch(offset, limit));
        return passengers;
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
        dataSource.replace(passenger.getName(), passenger);
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @DeleteMapping
    public Boolean delete(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        Passenger passenger = new Passenger();
        passenger.unmarshallingFromMap(payload.getPayload(), true);
        dataSource.remove(passenger.getName());
        return true;
    }

}
