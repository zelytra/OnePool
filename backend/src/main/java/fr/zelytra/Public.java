package fr.zelytra;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("ping")
public class Public {
    
    @GET
    public Response get(){
        return Response.ok("Pong").build();
    }
}
