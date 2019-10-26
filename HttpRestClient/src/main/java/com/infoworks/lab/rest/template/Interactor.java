package com.infoworks.lab.rest.template;


import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.EntityInterface;

@FunctionalInterface
public interface Interactor<P extends Response, C extends EntityInterface> {

    P apply(C consume) throws HttpInvocationException;

    static <I extends Interactor> I create(Class<I> type, Object... config) throws IllegalAccessException
            , InstantiationException {
        //Like this: Where we are checking whither or not ConfigurableInteractor has been
        //implemented by the given type and we create the new instance and call the configuration method with
        //supplied parameters and return the instance:
        if (ConfigurableInteractor.class.isAssignableFrom(type)){
            I inferredInstance = type.newInstance();
            ((ConfigurableInteractor) inferredInstance).configure(config);
            return inferredInstance;
        }
        return type.newInstance();
    }
}
