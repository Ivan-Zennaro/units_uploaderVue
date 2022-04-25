package org.example.rest.api;

import org.example.rest.entities.storage.File;
import org.example.rest.persistence.FileHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/gen")
public class GeneralManager {
    @GET
    @Path("/linkfile/{id}")
    public Response getFileFromLink(@PathParam("id") String id, @Context HttpServletRequest requestContext ) throws URISyntaxException {
        String ipRequest = requestContext.getRemoteAddr();
        FileHelper.setViewFile(id,ipRequest);
        File file = FileHelper.getById(File.class,id);
        URI uri=new URI(file.getUrl());
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(uri).build();
    }
}
