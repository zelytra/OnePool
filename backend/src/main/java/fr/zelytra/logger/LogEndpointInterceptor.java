package fr.zelytra.logger;

import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

@LogEndpoint
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LogEndpointInterceptor {

    @Context
    ContainerRequestContext requestContext;

    @AroundInvoke
    public Object logMethod(InvocationContext context) throws Exception {
        if (requestContext != null) {
            String method = requestContext.getMethod();
            String path = requestContext.getUriInfo().getPath();
            Log.info("[" + method + "] " + path);
        }

        return context.proceed();
    }
}
