package org.example.rest.persistence;

import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;

public abstract class AbstractHelper<T> {

    public static <T> void saveDelayed(T t){
        ofy().save().entity(t);
    }

    public static <T> void saveNow(T t){
        ofy().save().entity(t).now();
    }

    public static <T> T getById(Class<T> c,String id){
        if (id == null ||id.equals("")) return null;
        Key<T> k = Key.create(c,id);
        return ofy().load().key(k).now();
    }

    public static <T> void deleteById(Class<T> c,String id){
        ofy().delete().type(c).id(id);
    }

}
