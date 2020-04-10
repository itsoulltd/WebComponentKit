package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.ItemCount;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        if (old != null)
            count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("name") String name){
        //TODO: Test with RestExecutor
        Passenger deleted = dataSource.remove(name);
        return deleted != null;
    }

}
