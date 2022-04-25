package org.example.rest.api;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.example.rest.entities.proxies.ActorProxy;
import org.example.rest.entities.proxies.FileProxy;
import org.example.rest.entities.storage.Actor;
import org.example.rest.entities.storage.File;
import org.example.rest.persistence.ActorHelper;
import org.example.rest.persistence.FileHelper;
import org.example.rest.utils.Roles;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Path("/cons")
public class ConsManager {

    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("./WEB-INF/ProgWeb2020-27c3d5763b2a.json"))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

    public ConsManager() throws IOException {}

    @GET
    @Path("/getmyupls") //GET_MY_UPLS
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActorProxy> getUploaders (@Context HttpHeaders httpheaders) throws ParseException {
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        return ActorHelper.getUplByConsTok(token);
    }

    @GET
    @Path("/getfilecons/{uploader}")  //GET_FILES_CONS
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileProxy> getFileListAndTags (@Context HttpHeaders httpheaders, @PathParam("uploader") String uploader) throws ParseException {
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor actor = ActorHelper.getActorByToken(token);
        if (actor == null) return null;
        List<File> files = FileHelper.getFilesByUplCons(uploader,actor.getUsername());
        List<FileProxy> fileToSend = new ArrayList<>();
        for (File file: files) fileToSend.add(new FileProxy(file));
        return fileToSend;
    }

    @POST
    @Path("/readfilecons")      //UPD_FILE_VIEW
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readFileConsumer (@Context HttpServletRequest requestContext, String url) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject credentials = (JSONObject) parser.parse(url);
        String thisUrl = (String) credentials.get("url");

        String ipRequest = requestContext.getRemoteAddr();

        String fileId = thisUrl.substring("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorFiles/".length(),thisUrl.lastIndexOf("."));
        FileHelper.setViewFile(fileId,ipRequest);
        return Response.ok(new FileProxy(FileHelper.getById(File.class,fileId))).build();
    }

    @GET
    @Path("/getinfo")       //GET_MY_INFO
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfileInfo(@Context HttpHeaders httpheaders) throws ParseException {
            String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor actor = ActorHelper.getActorByToken(token);
        if(actor == null)
            return Response.status(Response.Status.FORBIDDEN).build();
        ActorProxy actorProxy = new ActorProxy(actor);
        return Response.ok(actorProxy).build();
    }


    @POST
    @Path("/uplimg")    //UPL_IMG
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImageFile(@FormDataParam("file") InputStream fileInputStream,
                                    @Context HttpHeaders httpheaders) throws IOException, ParseException {
        //check iniziali
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        if (token.equals("null") || fileInputStream == null)
            return Response.status(Response.Status.FORBIDDEN).build();

        //converto input stream in array di byte
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1000000];
        while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        //elimino l'immagine precedente
        Actor actor = ActorHelper.getActorByToken(token);
        String prevPath = actor.getImgPath().substring("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorImage/".length());
        if (!prevPath.equals("default.jpg") ){
            BlobId prevBlobId = BlobId.of("progweb2020-272915.appspot.com", "ActorImage/" + prevPath);
            storage.delete(prevBlobId);
        }

        //inserisco la nuova immagine
        String path = ActorHelper.setImagePath(actor.getUsername());
        BlobId blobId = BlobId.of("progweb2020-272915.appspot.com", "ActorImage/" + path + ".jpg");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
        storage.create(blobInfo, buffer.toByteArray());

        return Response.ok(new ActorProxy(ActorHelper.getActorByToken(token))).build();
    }

    @POST
    @Path("/updpass")   //UPD_PASS
    public Response updatePass (@Context HttpHeaders httpheaders, @QueryParam("prev_pass") String prev_pass, @QueryParam("new_pass")String new_pass) throws ParseException {

        String token = httpheaders.getHeaderString("Authorization").substring(7);
        //il metodo prevede che può essere mpdificata solo la password dell'attore che presenta il token
        if (ActorHelper.updatePass(token,prev_pass,new_pass))
            return Response.ok().build();
        else return Response.status(Response.Status.CONFLICT).build();
    }

    @POST
    @Path("/updactor")  //UPD_ACTOR
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateActor (@Context HttpHeaders httpheaders, ActorProxy actorProxy) throws ParseException {

        //controllo autorizzazioni aggiuntive: uploader può modificare solo consumer
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor requestActor = ActorHelper.getActorByToken(token);

        if (requestActor == null) Response.status(Response.Status.FORBIDDEN).build();

        // i consumer possono solo modificare se stessi
        if (requestActor.getRole().equals(Roles.CONSUMER) &&
                !requestActor.getUsername().equals(actorProxy.getUsername()))
            return Response.status(Response.Status.FORBIDDEN).build();

        //uploader può solo modificare se stesso e consumer
        if (requestActor.getRole().equals(Roles.UPLOADER) &&
                !actorProxy.getRole().equals(Roles.CONSUMER.toString()) &&
                !requestActor.getUsername().equals(actorProxy.getUsername()))
            return Response.status(Response.Status.FORBIDDEN).build();

        if (ActorHelper.updateActor(actorProxy))
            return Response.ok().build();
        else return Response.status(Response.Status.CONFLICT).build();

    }

}
