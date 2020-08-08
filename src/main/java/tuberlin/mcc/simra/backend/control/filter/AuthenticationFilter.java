package tuberlin.mcc.simra.backend.control.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import tuberlin.mcc.simra.backend.WebServer;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            String token = requestContext.getUriInfo().getQueryParameters().get("clientHash").get(0);
            if (!validateSecret(token)) {
                abortWithUnauthorized(requestContext);
            }

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    private boolean validateSecret(String token) throws Exception {
        return WebServer.secret.equals(token);
    }

}
