package org.cdsframework.common.rs.support;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Properties;
import org.cdsframework.common.enumeration.Environment;
import org.cdsframework.common.util.ConfigurationProperties;
import org.cdsframework.util.LogUtils;


/**
 *
 * @author HLN Consulting, LLC
 */
public class CoreConfiguration {
    
    private static final LogUtils logger = LogUtils.getLogger(CoreConfiguration.class);
    private static final Properties INSTANCE_PROPERTIES = new Properties();
    private static final boolean RETURN_STACK_TRACE;
    private static final JsonInclude.Include JSON_INCLUDE;
    private static final String EJB_HOST;
    private static final String EJB_PORT;
    private static final String EJB_JNDI_ROOT;
    private static final boolean EJB_USE_REMOTE;
    private static final String ENVIRONMENT;
    private static final boolean GZIP_SUPPORT;
    private static final boolean LOGGING_FILTER;
    private static final boolean GENERIC_EXCEPTION_MAPPER;
    
    static {
        final String METHODNAME = "CoreConfiguration static contructor ";
        
        String instanceName = "appServer.properties";
        String instancePropertyUriName = "rsCorePropertiesUri";
        String instanceDirSystemName = "instanceDirSystemPropertyName";
        
        // Log instance attributes, everything must match
        logger.info(METHODNAME, "instanceName=", instanceName, " instancePropertyUriName=", instancePropertyUriName, " instanceDirSystemName=", instanceDirSystemName);
        
        // Load the properties, failOnNotFound = true
        ConfigurationProperties.getProperties(instanceName, instancePropertyUriName, instanceDirSystemName, INSTANCE_PROPERTIES, true);
        
        EJB_HOST = INSTANCE_PROPERTIES.getProperty("EJB_HOST");
        EJB_PORT = INSTANCE_PROPERTIES.getProperty("EJB_PORT");
        EJB_JNDI_ROOT = INSTANCE_PROPERTIES.getProperty("EJB_JNDI_ROOT");
        ENVIRONMENT = INSTANCE_PROPERTIES.getProperty("ENVIRONMENT");
        EJB_USE_REMOTE = Boolean.parseBoolean(INSTANCE_PROPERTIES.getProperty("EJB_USE_REMOTE", "true"));
        RETURN_STACK_TRACE = Boolean.parseBoolean(INSTANCE_PROPERTIES.getProperty("RETURN_STACK_TRACE", "false"));
        // No long read from site properties, hard code to NON_NULL
        //JSON_INCLUDE = JsonInclude.Include.valueOf(INSTANCE_PROPERTIES.getProperty("JSON_INCLUDE", "NON_NULL"));
        JSON_INCLUDE = JsonInclude.Include.valueOf("NON_NULL");
        GZIP_SUPPORT = Boolean.parseBoolean(INSTANCE_PROPERTIES.getProperty("GZIP_SUPPORT", "true"));
        LOGGING_FILTER = Boolean.parseBoolean(INSTANCE_PROPERTIES.getProperty("LOGGING_FILTER", "true"));
        GENERIC_EXCEPTION_MAPPER = Boolean.parseBoolean(INSTANCE_PROPERTIES.getProperty("GENERIC_EXCEPTION_MAPPER", "true"));

        logger.info(METHODNAME, "EJB_HOST=", EJB_HOST);
        logger.info(METHODNAME, "EJB_PORT=", EJB_PORT);
        logger.info(METHODNAME, "EJB_JNDI_ROOT=", EJB_JNDI_ROOT);
        logger.info(METHODNAME, "EJB_USE_REMOTE=", EJB_USE_REMOTE);
        logger.info(METHODNAME, "RETURN_STACK_TRACE=", RETURN_STACK_TRACE);
        logger.info(METHODNAME, "JSON_INCLUDE=", JSON_INCLUDE);
        logger.info(METHODNAME, "ENVIRONMENT=", ENVIRONMENT);
        logger.info(METHODNAME, "LOGGING_FILTER=", LOGGING_FILTER);
        logger.info(METHODNAME, "GZIP_SUPPORT=", GZIP_SUPPORT);
        logger.info(METHODNAME, "GENERIC_EXCEPTION_MAPPER=", GENERIC_EXCEPTION_MAPPER);        
    }

    public static boolean isReturnStackTrace() {
        return RETURN_STACK_TRACE;
    }
    
    public static JsonInclude.Include getJsonInclude() {
        return JSON_INCLUDE;
    }

    public static String getEjbHost() {
        return EJB_HOST;
    }

    public static String getEjbPort() {
        return EJB_PORT;
    }

    public static String getEjbJndiRoot() {
        return EJB_JNDI_ROOT;
    }

    public static Environment getEnvironment() {
        return Environment.valueOf(ENVIRONMENT);
    }

    public static boolean isEjbUseRemote() {
        return EJB_USE_REMOTE;
    }

    public static boolean isGzipSupport() {
        return GZIP_SUPPORT;
    }

    public static boolean isLoggingFilter() {
        return LOGGING_FILTER;
    }

    public static boolean isGenericExceptionMapper() {
        return GENERIC_EXCEPTION_MAPPER;
    }
    
    public static Properties getInstanceProperties() {
        return INSTANCE_PROPERTIES;
    }
    

}
