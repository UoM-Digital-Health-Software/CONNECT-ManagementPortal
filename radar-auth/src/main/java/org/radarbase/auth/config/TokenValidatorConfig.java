package org.radarbase.auth.config;

import java.net.URI;
import java.util.List;

public interface TokenValidatorConfig {

    /**
     * Get the public key endpoint as a URI.
     * @return The public key endpoint URI, or <code>null</code> if not defined
     */
    List<URI> getPublicKeyEndpoints();

    /**
     * The name of this resource. It should be in the list of allowed resources for the OAuth
     * client.
     * @return the name of the resource
     */
    String getResourceName();

}
