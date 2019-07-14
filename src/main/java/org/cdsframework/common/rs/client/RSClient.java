/**
 * The MTS support core project contains client related utilities, data transfer objects and remote EJB interfaces for communication with the CDS Framework Middle Tier Service.
 *
 * Copyright (C) 2016 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see https://www.hln.com/services/open-source/ or send
 * correspondence to ice@hln.com.
 */
package org.cdsframework.common.rs.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.cdsframework.common.rs.exception.mapper.ErrorException;
import org.cdsframework.common.rs.exception.mapper.ErrorMessage;
import org.cdsframework.common.rs.provider.CoreJacksonJsonProvider;
import org.cdsframework.common.rs.support.CoreRsConstants;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.EncodingFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.GZipEncoder;

/**
 *
 * @author HLN Consulting, LLC
 */
public class RSClient {

    private WebTarget webTarget;
    private final Client client;
    private final String rootPath;
    private final String baseURI;
    private final JsonInclude.Include jsonInclude;
    private final boolean loggingFilter;
    private final boolean gzipSupport;
    private static final Logger logger = Logger.getLogger(RSClient.class.getCanonicalName());
    private final boolean useResourceInPath;
    private CoreJacksonJsonProvider coreJacksonJsonProvider;
            

    /**
     *
     * @param baseURI base of rest service
     * @param rootPath root of rest service
     */
    
    public RSClient(String baseURI, String rootPath) {
        this(baseURI, rootPath, false, false);
    }
    
    /**
     *
     * @param baseURI base of rest service
     * @param rootPath root of rest service
     * @param loggingFilter false is the default, true turns on logging, false turns off logging
     * @param gzipSupport false is the default, true turns on GZIP decompression, provided that 
     *                    the RS service is configured to use GZIP compression 
     *                    when returning findByPrimaryKey or findByQueryList responses
     */
    public RSClient(String baseURI, String rootPath, boolean loggingFilter, boolean gzipSupport) {
        this(baseURI, rootPath, loggingFilter, true, gzipSupport);
    }

    /**
     *
     * @param baseURI base of rest service
     * @param rootPath root of rest service
     * @param loggingFilter false is the default, true turns on logging, false turns off logging
     * @param useResourceInPath false expects a Typed DTO RS service, 
     *                          true expects a Generic RS service,
     *                          When true it passes up the resourceName using the DTO converted into a resourceName
     *                          In the RS tier POST/PUT extract the resourceName from the json payload to deserialize the DTO
     * @param gzipSupport false is the default, true turns on GZIP decompression, provided that 
     *                    the RS service is configured to use GZIP compression 
     *                    when returning findByPrimaryKey or findByQueryList responses
     */
    public RSClient(String baseURI, String rootPath, boolean loggingFilter, boolean useResourceInPath, boolean gzipSupport) {
        this(baseURI, rootPath, loggingFilter, useResourceInPath, gzipSupport, JsonInclude.Include.NON_NULL);
    }    
    
    /**
     *
     * @param baseURI base of rest service
     * @param rootPath root of rest service
     * @param loggingFilter false is the default, true turns on logging, false turns off logging
     * @param useResourceInPath false expects a Typed DTO RS service, 
     *                          true expects a Generic RS service,
     *                          When true it passes up the resourceName using the DTO converted into a resourceName
     *                          In the RS tier POST/PUT extract the resourceName from the json payload to deserialize the DTO
     * @param gzipSupport false is the default, true turns on GZIP decompression, provided that 
     *                    the RS service is configured to use GZIP compression 
     *                    when returning findByPrimaryKey or findByQueryList responses
     * @param jsonInclude default is NON_NULL, used to exclude NULLs in json string
     */
    public RSClient(String baseURI, String rootPath, boolean loggingFilter, boolean useResourceInPath, boolean gzipSupport, JsonInclude.Include jsonInclude) {
        this.useResourceInPath = useResourceInPath;
        this.loggingFilter = loggingFilter;
        this.gzipSupport = gzipSupport;
        this.jsonInclude = jsonInclude;
        this.baseURI = baseURI + "/" + CoreRsConstants.RESOURCE_ROOT;
        this.rootPath = rootPath;
        client = getClient(jsonInclude);
       
    }    

    public void setConnectTimeout(Integer value ) {
        setClientProperties(ClientProperties.CONNECT_TIMEOUT, value);
    }    
    
    public void setReadTimeout(Integer value ) {
        setClientProperties(ClientProperties.READ_TIMEOUT, value);
    }    

    public void setClientProperties(String clientProperty, Integer value ) {
        client.property(clientProperty, value);
    }
    
    public Client getClient() {
        return client;
    }
    
    /**
     *
     * @return root path
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     *
     * @return WebTarget
     */
    public WebTarget getWebTarget() {
        if (webTarget == null) {
            webTarget = getWebTarget(client);
        }
        return webTarget;
    }
    
    private WebTarget getWebTarget(Client client) {
        return client.target(baseURI).path(rootPath);
    }
    
    private Client getClient(JsonInclude.Include jsonInclude) {
        Client client = ClientBuilder.newClient()
                .register(new JacksonFeature());
        if (loggingFilter) {
            client.register(new LoggingFilter(logger, true));
        }
        coreJacksonJsonProvider = new CoreJacksonJsonProvider(jsonInclude);
        client.register(coreJacksonJsonProvider);
        if (gzipSupport) {
            client.register(new EncodingFeature("gzip", GZipEncoder.class));
        }
        client.register(new ApacheConnectorProvider());
        return client;
    }    

    /**
     * closes client
     */
    public void close() {
        client.close();
    }
    
    protected <T> T getResponse(Class<T> responseType, WebTarget resource) throws ErrorException {
        Response response = null;
        try {
            // Get the response
            response = resource.request().get(Response.class);

            // Check the status and return
            return evaluateResponse(response, responseType);
        }
        finally {
            // Just in case we get here and the response is not closed, readEntity closes the response by default
            // But if during the Read operation a Jersey exception occurs we want to close out the response
            if (response != null && responseType != Response.class) {
                response.close();
            }
        }
    }
    
    /**
     *
     * @param message used to diagnose connectivity
     * @return
     */
    public String ping(String path, String message) {
        WebTarget resource = getWebTarget().path(path);
        if (message != null) {
            resource = resource.queryParam("message", message);
        }
        resource = resource.path("ping");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     *
     * @param objects list of string objects to construct rest service path
     * @return
     */
    public String getPath(List<Object> objects) {
        final String METHODNAME = "getPath ";
        String path = "";
        if (objects != null) {
            int size = objects.size();
            int counter = 0;
            String format = "";
            String[] stringArray = new String[objects.size()];
            for (Object o : objects) {
                format += "{" + counter + "}";
                if (counter != size - 1) {
                    format += "/";
                }
                stringArray[counter] = o.toString();
                counter++;
            }
            path = MessageFormat.format(format, stringArray);
        }
        return path;
    }

    /* Common function to evaluate the outcome of the response and return the appropriate type */
    protected <T> T evaluateResponse(Response response, Class<T> responseType) throws ErrorException {
        final String METHODNAME = "evaluateResponse ";
        
        T returnType = null;
        // Check the status
        boolean success = (response.getStatus() == Response.Status.OK.getStatusCode());

        if (!success) {
            // Caller not interested in the response, translate and throw
            if (responseType != Response.class) {
                throwException(response.readEntity(ErrorMessage.class));
            }
        }

        if (response != null) {
            if (responseType != Response.class) {
                returnType = response.readEntity(responseType);
            }
            else {
                returnType = (T) response;
            }
        }
        return returnType;
    }

    protected boolean evaluateResponse(Response response) throws ErrorException {
        final String METHODNAME = "evaluateResponse ";
        
        // Check the status
        boolean success = (response.getStatus() == Response.Status.OK.getStatusCode());

        if (!success) {
            // Caller not interested in the response, translate and throw
            throwException(response.readEntity(ErrorMessage.class));
        }
        return success;
    }
    
    public <T> List<T> getList(WebTarget resource, Object requestEntity, final Class<T> responseType) throws ErrorException  {

        ParameterizedType parameterizedGenericType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { responseType };
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return List.class;
            }
        };
        GenericType<List<T>> genericType = new GenericType<List<T>>(parameterizedGenericType){};
        return getList(resource, requestEntity, genericType);
    }        
    
    private <T> List<T> getList(WebTarget resource, Object requestEntity, GenericType genericType) throws ErrorException {
        final String METHODNAME = "getList ";
        logger.info(METHODNAME);
        Response response = null;
        List<T> list = null;
        try {
            if (requestEntity == null) {
                response = resource.request().get(Response.class);
            }
            else {
                response = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                        .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);            
            }

            // Check the status
            boolean success = (response.getStatus() == Response.Status.OK.getStatusCode());

            // Success
            if (!success) {
                throwException(response.readEntity(ErrorMessage.class));
            }
            else {
                list = (List<T>) response.readEntity(genericType);
            }
        }
        finally {
            // Just in case we get here and the response is not closed, readEntity closes the response by default
            // But if during the Read operation a Jersey exception occurs we want to close out the response
            if (response != null) {
                response.close();
            }
        }
        return list;
    }        
    
    public static void throwException(ErrorMessage errorMessage) throws ErrorException {
        throw new ErrorException(errorMessage);
    }    
    
    public CoreJacksonJsonProvider getCoreJacksonJsonProvider() {
        return coreJacksonJsonProvider;
    }
}
