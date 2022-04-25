package org.example.rest.persistence;

import com.googlecode.objectify.Work;
import org.example.rest.entities.proxies.ActorProxy;
import org.example.rest.entities.storage.Actor;
import org.example.rest.entities.storage.File;
import org.example.rest.utils.JToken;
import org.example.rest.utils.Roles;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ActorHelper extends AbstractHelper<Actor>{

    public static Actor getActorByToken(String token) throws ParseException {
        if (token == null || token.equals("null") ) return null;
        String username = JToken.getUsrByToken(token);
        return getById(Actor.class, username);
    }

    public static List<Actor> getAllActorsByRole(Roles role){
        List<Actor> actors = ofy().load().type(Actor.class).list();
        List<Actor> toRemove = new ArrayList<>();

        for (Actor actor : actors) {
            if (!actor.getRole().equals(role))
                toRemove.add(actor);
        }
        actors.removeAll(toRemove);
        return actors;
    }

    public static List<ActorProxy> getUplByConsTok (String token) throws ParseException { //token del consumer
        if (token == null || token.equals("null") ) return null;
        Actor consumer = getActorByToken(token);
        // prendo tutti i file di quel consumers
        List<File> files = FileHelper.getFilesByConsumer(consumer.getUsername());
        List <String> supp = new ArrayList<>();     //supporto
        List <ActorProxy> uploadersOfConsumer = new ArrayList<>();

        //scorro i file e prendo tutti gli uploader diersi presenti
        for (File file: files) {
            String uplUser = getById(Actor.class,file.getUploader()).getUsername();
            if (!supp.contains(uplUser))
                supp.add(uplUser);
        }
        for (String s: supp) {
            uploadersOfConsumer.add(new ActorProxy(getById(Actor.class,s)));
        }
        return uploadersOfConsumer;
    }


    public static boolean updateActor (ActorProxy actorProxy){
        if (actorProxy == null || actorProxy.getUsername() == null) return false;
        ofy().transact(() -> {
            Actor actor = getById(Actor.class, actorProxy.getUsername());
            actor.setName(actorProxy.getName());
            actor.setEmail(actorProxy.getEmail());
            saveDelayed(actor);
        });
        return true;
    }

    public static boolean updatePass (String token, String prev_pass, String new_pass) throws ParseException {
        Actor actor = getActorByToken(token);
        if (actor == null || !actor.getPassword().equals(prev_pass))
            return  false;
        ofy().transact(() -> {
            Actor act = getById(Actor.class,actor.getUsername());
            act.setPassword(new_pass);
            saveDelayed(act);
        });
        return true;
    }


    public static String setImagePath (String username) {
        int random = (int) (Math.random() * 1000);
        ofy().transact(() -> {
            Actor actor = getById(Actor.class, username);
            actor.setImgPath("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorImage/" + actor.getUsername() + random + ".jpg");
            saveDelayed(actor);
        });

        return username + random;
    }

}
