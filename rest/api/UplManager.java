package org.example.rest.api;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.example.rest.entities.proxies.ActorProxy;
import org.example.rest.entities.proxies.FileProxy;
import org.example.rest.entities.storage.Actor;
import org.example.rest.entities.storage.File;
import org.example.rest.persistence.ActorHelper;
import org.example.rest.persistence.FileHelper;
import org.example.rest.utils.EmailSender;
import org.example.rest.utils.Md5;
import org.example.rest.utils.Roles;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Path("/upl")
public class UplManager {

    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("./WEB-INF/ProgWeb2020-27c3d5763b2a.json"))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

    public UplManager() throws IOException {}

    @POST
    @Path("/uploadfile")    //ADD_FILE
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFile (@FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileMetaData,
                                @FormDataParam("file") FormDataBodyPart body,
                                @FormDataParam("filename") String nameFile,
                                @FormDataParam("tags") String tags,
                                @FormDataParam("consumer") String cfCons,
                                @FormDataParam("emailcons") String email,
                                @FormDataParam("namecons") String nameCons,
                                @Context HttpHeaders httpheaders,
                                @Context HttpServletRequest request) throws IOException, ParseException {

        String mimeType = body.getMediaType().toString();
        String ext = fileMetaData.getFileName().substring(fileMetaData.getFileName().lastIndexOf(".") + 1);

        //check iniziali: token + file not null
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        if (token.equals("null") || fileInputStream == null)
            return Response.status(Response.Status.FORBIDDEN).build(); //"token o file a null";

        Actor uploader = ActorHelper.getActorByToken(token);
        if(uploader == null)
            return Response.status(Response.Status.CONFLICT).build(); //uploader non presente
        if(!validateCf(cfCons.toUpperCase()))
            return Response.status(Response.Status.CONFLICT).build(); // cf non valido

        //check se consumer esiste
        Actor consumer = ActorHelper.getById(Actor.class, cfCons);
        // creo il consumer se non esiste
        if (consumer == null){
            consumer = new Actor(cfCons, email, nameCons, Md5.getMd5("password"),Roles.CONSUMER);
            ActorHelper.saveNow(consumer);
        }

        nameFile = nameFile.replace(" ","_");

        //controllo se è già presente un file con stesso id
        File checkExist = FileHelper.getById(File.class,uploader.getUsername() + "_" + consumer + "_" + nameFile);
        if (checkExist != null)
            return Response.status(Response.Status.CONFLICT).build();

        //converto input stream in array di byte
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1000000];
        while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        //inserisco il nuovo file
        File file = new File(uploader.getUsername(),consumer.getUsername(), nameFile, tags, ext);
        FileHelper.saveDelayed(file);
        BlobId blobId = BlobId.of("progweb2020-272915.appspot.com", "ActorFiles/" + file.getId() + "." + ext);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
        storage.create(blobInfo, buffer.toByteArray());

        Actor cons = ActorHelper.getById(Actor.class, consumer.getUsername());
        EmailSender.sendEmail(uploader.getName(),cons.getEmail(),nameFile,request,file.getId());
        return Response.ok(new FileProxy(file)).build();
    }



    @GET
    @Path("/getfilesupl/{consumer}")   //GET_FILES_UPL
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileProxy>  getFileList (@Context HttpHeaders httpheaders, @PathParam("consumer") String consumer) throws ParseException {
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor actor = ActorHelper.getActorByToken(token);
        if (actor == null) return null;
        List<File> files = FileHelper.getFilesByUplCons(actor.getUsername(),consumer);
        List<FileProxy> fileToSend = new ArrayList<>();
        for (File file: files) fileToSend.add(new FileProxy(file));
        return fileToSend;
    }

    @DELETE
    @Path("/delfile/{id}")  //DEL_FILE
    public Response removeFile (@PathParam("id") String id){
        id = id.replace(" ","_");
        File file = FileHelper.getById(File.class, id.replace(" ","_"));
        if (file == null) return Response.status(Response.Status.CONFLICT).build();
        String pathToDelete = file.getUrl().substring("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorFiles/".length());
        BlobId prevBlobId = BlobId.of("progweb2020-272915.appspot.com", "ActorFiles/" + pathToDelete);
        storage.delete(prevBlobId);
        FileHelper.deleteById(File.class, id);
        return Response.ok().build();
    }

    @POST
    @Path("/newactor")  //CREATE_NEW
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newActor (@Context HttpHeaders httpheaders, String newActor) throws ParseException {

        JSONParser parser = new JSONParser();

        //attore non nullo
        if (newActor == null)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        JSONObject nwAct = (JSONObject) parser.parse(newActor);

        String username = (String) nwAct.get("username");
        String name = (String) nwAct.get("name");
        String email = (String) nwAct.get("email");
        String role = (String) nwAct.get("role");
        String password = (String) nwAct.get("password");

        //controllo autorizzazioni aggiuntive: uploader può aggiungere solo consumer
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor requestActor = ActorHelper.getActorByToken(token);
        if (requestActor.getRole().equals(Roles.UPLOADER) && !role.equals(Roles.CONSUMER.toString()))
            return Response.status(Response.Status.FORBIDDEN).build();

        //controllo se già presente
        Actor temp = ActorHelper.getById(Actor.class,username);
        if (temp != null)
            return Response.status(Response.Status.CONFLICT).build();

        //se non è presente
        Actor actor = new Actor(username,email,name,password,Roles.valueOf(role.toUpperCase()));
        ActorHelper.saveDelayed(actor);
        ActorProxy actorProxy = new ActorProxy(actor);
        return Response.ok(actorProxy).build();
    }

    @GET
    @Path("/getactor/{id}")     //GET_ACTOR
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActor(@Context HttpHeaders httpheaders, @PathParam("id") String username) throws ParseException {

        String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor requestActor = ActorHelper.getActorByToken(token);
        Actor actor  = ActorHelper.getById(Actor.class,username);

        if( requestActor == null || actor == null)
            return Response.status(Response.Status.FORBIDDEN).build();

        //controllo autorizzazioni aggiuntive: uploader può accedere solo ai consumer
        if (requestActor.getRole().equals(Roles.UPLOADER) && !actor.getRole().equals(Roles.CONSUMER))
            return Response.status(Response.Status.FORBIDDEN).build();

        return Response.ok(new ActorProxy(actor)).build();
    }


    @GET
    @Path("/listcons")  //GET_CONSUMER_LIST
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActorProxy> getConsumers() {
        List<Actor> consumers  = ActorHelper.getAllActorsByRole(Roles.CONSUMER);
        List<ActorProxy> actorProxies = new ArrayList<>();
        for (Actor consumer: consumers) {
            actorProxies.add(new ActorProxy(consumer));
        }
        return actorProxies;
    }


    @DELETE
    @Path("delactor/{id}")  //DEL_ACTOR
    public Response removeActor(@Context HttpHeaders httpheaders, @PathParam("id") String username) throws ParseException {

        Actor actor = ActorHelper.getById(Actor.class, username);
        if (actor == null)
            return Response.status(Response.Status.FORBIDDEN).build();

        //controllo autorizzazioni aggiuntive: uploader può eliminare solo consumer
        String token = httpheaders.getHeaderString("Authorization").substring(7);
        Actor requestActor = ActorHelper.getActorByToken(token);
        if (requestActor.getRole().equals(Roles.UPLOADER) && !actor.getRole().equals(Roles.CONSUMER))
            return Response.status(Response.Status.FORBIDDEN).build();


        //rimuovo la sua immagine profilo se diversa da quelle di default
        String prevPath = actor.getImgPath().substring("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorImage/".length());
        if (!prevPath.equals("default.jpg") && !prevPath.equals("defaultAdmin.jpg")){
            BlobId prevBlobId = BlobId.of("progweb2020-272915.appspot.com", "ActorImage/" + prevPath);
            storage.delete(prevBlobId);
        }

        // rimuovo tutti i file associati a lui
        if (actor.getRole().equals(Roles.CONSUMER)){
            List<File> fileToDelete = FileHelper.getFilesByConsumer(actor.getUsername());
            for (File file: fileToDelete) {
                String pathToDelete = file.getUrl().substring("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorFiles/".length());
                BlobId blobId = BlobId.of("progweb2020-272915.appspot.com", "ActorFiles/" + pathToDelete);
                storage.delete(blobId);
                FileHelper.deleteById(File.class, file.getId());
            }
        }

        //se è uploader rimuovo tutti i file da lui caricati
        if (actor.getRole().equals(Roles.UPLOADER)){
            List<File> fileToDelete = FileHelper.getFilesByUploader(actor.getUsername());
            for (File file: fileToDelete) {
                String pathToDelete = file.getUrl().substring("https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorFiles/".length());
                BlobId blobId = BlobId.of("progweb2020-272915.appspot.com", "ActorFiles/" + pathToDelete);
                storage.delete(blobId);
                FileHelper.deleteById(File.class, file.getId());
            }
        }

        //rimuovo l'entità
        ActorHelper.deleteById(Actor.class,username);
        return Response.ok().build();
    }


    private boolean validateCf(String cf){
        return cf.toUpperCase().matches("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");
    }

}
