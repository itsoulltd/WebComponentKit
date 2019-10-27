package com.infoworks.lab.controllers;

import com.infoworks.lab.components.rest.Payload;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerService;
import com.infoworks.lab.jsql.JsqlConfig;
import com.infoworks.lab.rest.models.ItemCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Autowired
    private JsqlConfig jsqlConfig;

    @Autowired @Qualifier("AppDBNameKey")
    private String dbKey;

    @Autowired
    private Environment env;

    @Autowired
    private PassengerService passengerService;

    @GetMapping("/rowCount")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(passengerService.count());
        return count;
    }

    @GetMapping
    public List<Passenger> query(@RequestParam("limit") Integer limit
            , @RequestParam("offset") Integer offset){
        //TODO: Test with RestExecutor
        return new ArrayList<>();
    }

    @PostMapping
    public ItemCount insert(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        return new ItemCount();
    }

    @PutMapping
    public ItemCount update(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        return new ItemCount();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody Payload payload){
        //TODO: Test with RestExecutor
        return false;
    }

}
