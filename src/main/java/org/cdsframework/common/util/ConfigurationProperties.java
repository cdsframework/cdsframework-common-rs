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
package org.cdsframework.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public class ConfigurationProperties {
    private static final LogUtils logger = LogUtils.getLogger(ConfigurationProperties.class);
    
    public static void getProperties(String instanceName, String instancePropertyUriName, 
            String instanceDirSystemName, Properties properties, boolean failOnNotFound) {
        final String METHODNAME = "getProperties ";

        logger.info(METHODNAME, "instanceName=", instanceName, " instancePropertyUriName=", instancePropertyUriName, " instanceDirSystemName=", instanceDirSystemName);
        InputStream tmpInstancePropertiesStream = ConfigurationProperties.class.getClassLoader().getResourceAsStream(instanceName);
        logger.info(METHODNAME, "first attempt: tmpInstancePropertiesStream=", tmpInstancePropertiesStream);
        
        if (tmpInstancePropertiesStream == null) {
            tmpInstancePropertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(instanceName);
            logger.info(METHODNAME, "second attempt: tmpInstancePropertiesStream=", tmpInstancePropertiesStream);
            if (tmpInstancePropertiesStream == null) {
                tmpInstancePropertiesStream = ConfigurationProperties.class.getResourceAsStream(instanceName);
                logger.info(METHODNAME, "third attempt: tmpInstancePropertiesStream=", tmpInstancePropertiesStream);
            }
        }
        
        // If null throw exception based on value of failOnNotFound
        if (tmpInstancePropertiesStream == null) {
            if ( failOnNotFound ) {
                throw new IllegalArgumentException("tmpInstancePropertiesStream was null!");
            }
        }
        else {
            Properties tmpInstanceProperties = new Properties();
            try {
                tmpInstanceProperties.load(tmpInstancePropertiesStream);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }

            //
            // This is the application instance root, retrieve the system property name that contains 
            // the instance root path for this application server
            //
            String instanceDirSystem = tmpInstanceProperties.getProperty(instanceDirSystemName);
            logger.info(METHODNAME, "instanceDirSystem=", instanceDirSystem);
            // Get the system property
            String instanceRoot = System.getProperty(instanceDirSystem);
            logger.info(METHODNAME, "instanceRoot=", instanceRoot);

            // Check if JBoss is running
            String jbossConfigPath = getJBossConfigPath();
            if (!StringUtils.isEmpty(jbossConfigPath)) {
                instanceRoot = jbossConfigPath;
            }
            
            // Append the relative path of the property file to the instance root location
            String propertiesUri = instanceRoot + "/" + tmpInstanceProperties.getProperty(instancePropertyUriName);
            logger.info(METHODNAME, "Reading propertiesUri properties from: ", propertiesUri);

            // load the properties from the property file location
            try {
                // Check if file exists
                boolean fileExists = true;
                if (!failOnNotFound) {
                    fileExists = Files.exists(Paths.get(propertiesUri));
                }

                // File exists
                if (fileExists) {
                    // Will throw an error when file does not exist
                    properties.load(new FileInputStream(propertiesUri));
                }
                else {
                    logger.warn(METHODNAME, "propertiesUri=", propertiesUri, " was NOT found");
                }
                
                // Close stream
                tmpInstancePropertiesStream.close();

            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }   
            
        }
    }
    
 
    public static String getJBossConfigPath() {
        final String METHODNAME = "getJBossConfigPath ";
        String jbossConfigPath = null;
        String jbossHome = System.getProperty("jboss.home.dir");
        logger.info(METHODNAME, "jbossHome=", jbossHome);

        // For Jboss
        if (!StringUtils.isEmpty(jbossHome)) {
            // Check Server/Domain
            String jbossServerConfig = System.getProperty("jboss.server.config.dir");
            String jbossDomainConfig = System.getProperty("jboss.domain.config.dir");

            logger.info(METHODNAME, "jbossServerConfig=", jbossServerConfig);
            logger.info(METHODNAME, "jbossDomainConfig=", jbossDomainConfig);
            System.out.println("jbossServerConfig=" +  jbossServerConfig + " jbossDomainConfig=" + jbossDomainConfig);
            String jbossConfig = null;
            if (!StringUtils.isEmpty(jbossServerConfig)) {
                jbossConfig = jbossServerConfig;
            }
            else if (!StringUtils.isEmpty(jbossDomainConfig)) {
                jbossConfig = jbossDomainConfig;
            }
            if (!StringUtils.isEmpty(jbossConfig)) {
                File propertyConfigDir = new File(jbossConfig);
                logger.info(METHODNAME, "propertyConfigDir.getPath()=", propertyConfigDir.getPath());
                jbossConfigPath = propertyConfigDir.getPath();
            }
            else {
                throw new IllegalArgumentException("Unable to locate JBoss Config");
            }
        }
        return jbossConfigPath;        
    }
}
