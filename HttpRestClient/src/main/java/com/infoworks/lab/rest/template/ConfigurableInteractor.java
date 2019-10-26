package com.infoworks.lab.rest.template;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.EntityInterface;

public interface ConfigurableInteractor<P extends Response, C extends EntityInterface> extends Interactor<P,C>{
    @Override
    default P apply(C consume) throws HttpInvocationException {
        return null;
    }
    void configure(Object... config) throws InstantiationException;
}
