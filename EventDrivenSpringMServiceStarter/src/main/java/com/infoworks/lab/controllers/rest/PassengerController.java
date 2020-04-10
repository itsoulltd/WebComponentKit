package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.datasources.MemCache;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.ItemCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ItemCount insert(@Valid @RequestBody Passenger passenger){
        //TODO: Test with RestExecutor
        dataSource.put(passenger.getName(), passenger);
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @PutMapping @SuppressWarnings("Duplicates")
    public ItemCount update(@Valid @RequestBody Passenger passenger){
        //TODO: Test with RestExecutor
        Passenger old = dataSource.replace(passenger.getName(), passenger);
        ItemCount count = new ItemCount();
        if(old != null)
            count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("name") String name){
        //TODO: Test with RestExecutor
        return dataSource.remove(name) != null;
    }

}
