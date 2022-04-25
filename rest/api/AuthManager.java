package org.example.rest.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.example.rest.entities.storage.Actor;
import org.example.rest.persistence.ActorHelper;
import org.example.rest.utils.JToken;

import org.example.rest.utils.Roles;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




@Path("/auth")
public class AuthManager {

    @POST
    @Path("/registration")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registration(String newActor) {
        try {

            JSONParser parser = new JSONParser();
            JSONObject nwAct = (JSONObject) parser.parse(newActor);

            String username = (String) nwAct.get("username");
            String name = (String) nwAct.get("name");
            String email = (String) nwAct.get("email");
            String role = (String) nwAct.get("role");
            String password = (String) nwAct.get("password");

            //controllo se username non è stato usato
            Actor temp = ActorHelper.getById(Actor.class,username);
            if (temp != null)
                return Response.status(Response.Status.CONFLICT).build();
            // creazione salvataggio Actor in db
            Actor actor = new Actor(username,email,name,password, Roles.valueOf(role.toUpperCase()));
            ActorHelper.saveDelayed(actor);
            String token = JToken.geneateToken(actor.getUsername(),role);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token",token);
            jsonObject.put("role",actor.getRole().toString());
            return Response.status(200).entity(jsonObject.toString()).build();


        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("/check_credential")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(String cred) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject credentials = (JSONObject) parser.parse(cred);

        String username = (String) credentials.get("username");
        String password = (String) credentials.get("password");

        Actor actor = ActorHelper.getById(Actor.class, username);
        if (actor == null || !actor.getPassword().equals(password))
            return Response.status(Response.Status.FORBIDDEN).build();

        String token = JToken.geneateToken(username,actor.getRole().toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token",token);
        jsonObject.put("role",actor.getRole().toString());
        return Response.status(200).entity(jsonObject.toString()).build();
    }

    @GET
    @Path("/access_token")
    public Response accessWithToken(@Context HttpHeaders httpheaders) throws ParseException {
        String token = httpheaders.getHeaderString("Authorization");

        if (token == null || token.equals("") || token.equals("null") || !token.startsWith("Bearer "))
            Response.status(Response.Status.FORBIDDEN).build();

        token = token.substring(7);
        Actor actor;
        if (JToken.verifyToken(token) && ((actor = ActorHelper.getActorByToken(token)) != null))
            return Response.ok(actor.getRole().toString()).build();
        return Response.status(Response.Status.FORBIDDEN).build();

    }


}









