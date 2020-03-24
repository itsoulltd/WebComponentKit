package com.infoworks.lab.domain.datasources;

import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.entity.EntityInterface;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemCache<Entity extends EntityInterface> implements DataSource<String, Entity> {

    private Logger LOG = Logger.getLogger(this.getClass().getSimpleName());
    private RedissonClient client;
    private static final String CLASS_NAME_KEY = "classname";
    private int itemCount;

    public MemCache(RedissonClient client) {
        this.client = client;
    }

    public Entity read(String key){
        RMap rData = client.getMap(key);
        if (rData != null && rData.size() > 0){
            Map<String, Object> data = new HashMap<>(rData.size());
            Iterator<Map.Entry<String, Object>> itr = rData.entrySet().iterator();
            while (itr.hasNext()){
                Map.Entry<String, Object> entry = itr.next();
                data.put(entry.getKey(), entry.getValue());
            }
            try {
                //Retrieving: Type
                Entity instance = initFromClassname(data.get(CLASS_NAME_KEY));
                instance.unmarshallingFromMap(data, true);
                return instance;
            } catch (InstantiationException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
        return null;
    }

    private Entity initFromClassname(Object classname)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        //
        if (classname == null) return null;
        String classFullName = classname.toString();
        if (classFullName.isEmpty()) return null;
        //
        Entity instance = (Entity) Class.forName(classFullName).newInstance();
        return instance;
    }

    @Override
    public Entity remove(String key) {
        Entity value = read(key);
        //then clear the cache:
        RMap rData = client.getMap(key);
        if (rData.size() > 0){
            rData.clear();
            if(itemCount > 0) itemCount--;
        }
        return value;
    }

    @Override
    public void put(String key, Entity entity) {
        Map<String, Object> data = entity.marshallingToMap(true);
        RMap rData = client.getMap(key);
        if (rData.size() > 0){
            rData.clear();
        }else{
            itemCount++;
        }
        data.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> rData.put(entry.getKey(), entry.getValue()));
        //Saving: Type
        String classFullName = entity.getClass().getName();
        rData.put(CLASS_NAME_KEY, classFullName);
    }

    @Override
    public boolean containsKey(String key) {
        return client.getMap(key).size() > 0;
    }

    @Override
    public int size() {
        return itemCount;
    }
}
