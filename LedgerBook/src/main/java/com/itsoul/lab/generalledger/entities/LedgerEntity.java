package com.itsoul.lab.generalledger.entities;

import com.infoworks.entity.Entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

/**
 * @author towhid
 * @since 19-Aug-19
 */
public class LedgerEntity extends Entity implements Externalizable {

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        Map<String, Object> data = marshalling(true);
        out.writeObject(data);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Object data = in.readObject();
        if (data instanceof Map){
            Map<String, Object> rdData = (Map) data;
            unmarshalling(rdData, true);
        }
    }
}
