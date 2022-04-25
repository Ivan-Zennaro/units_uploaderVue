package org.example.rest.api;

import org.example.rest.entities.proxies.ActorProxy;
import org.example.rest.entities.storage.Actor;
import org.example.rest.persistence.ActorHelper;
import org.example.rest.persistence.FileHelper;
import org.example.rest.utils.Roles;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Path("/admin")
public class AdminManager {

    @GET
    @Path("/getstats/{id}")     //GET_STATS_ACTOR
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStats (@PathParam("id") String uplodaer) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stat1", FileHelper.getNdocLastMonth(uplodaer) );
        jsonObject.put("stat2",FileHelper.getNDifConsLastMonth(uplodaer));
        jsonObject.put("username",uplodaer);
        return Response.status(200).entity(jsonObject.toString()).build();
    }

    @GET
    @Path("/getstats/{id}/{from}/{to}")     //GET_STATS_UPDATE
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStats (@PathParam("id") String uplodaer, @PathParam("from") String from, @PathParam("to") String to) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = formatter.parse(from);
            Date endDate = formatter.parse(to);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("stat1", FileHelper.getNdocPeriod(uplodaer,startDate.getTime()/1000,endDate.getTime()/1000));
            jsonObject.put("stat2", FileHelper.getNDifConsPeriod(uplodaer,startDate.getTime()/1000,endDate.getTime()/1000));
            jsonObject.put("username", uplodaer);
            return Response.status(200).entity(jsonObject.toString()).build();
        }
        catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/listupl")       //GET_UPL_LIST
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActorProxy> getUploaders() {
        List<Actor> uploaders  = ActorHelper.getAllActorsByRole(Roles.UPLOADER);
        List<ActorProxy> actorProxies = new ArrayList<>();
        for (Actor uploader: uploaders) {
            actorProxies.add(new ActorProxy(uploader));
        }
        return actorProxies;
    }

    @GET
    @Path("/listadmin")     //GET_ADMIN_LIST
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActorProxy> getAdmins() {
        List<Actor> admins  = ActorHelper.getAllActorsByRole(Roles.ADMIN);
        List<ActorProxy> actorProxies = new ArrayList<>();
        for (Actor admin: admins) {
            actorProxies.add(new ActorProxy(admin));
        }
        return actorProxies;
    }
}
