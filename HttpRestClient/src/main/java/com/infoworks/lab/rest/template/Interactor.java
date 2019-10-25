package com.infoworks.lab.rest.template;


import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.Entity;

@FunctionalInterface
public interface Interactor<P extends Response, C extends Entity> {

    P apply(C consume) throws HttpInvocationException;

    static <I extends Interactor> I create(Class<I> type, Object... config) throws IllegalAccessException, InstantiationException {
        //Hello :-
        //This means, the given generic type has implemented the Interface Interactor.
        //Interactor.class.isAssignableFrom(type);
        //So, We can rewrite the following as:
        /*if (HttpInteractor.class.isAssignableFrom(type)){
            I inferredInstance = type.newInstance();
            ((HttpInteractor) inferredInstance).configure(config);
            return inferredInstance;
        }else if(StompInteractor.class.isAssignableFrom(type)){
            I inferred = type.newInstance();
            ((StompInteractor) inferred).configure(config);
            return inferred;
        }*/
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
