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
package org.cdsframework.common.rs.provider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public class CoreJaxbAnnotationIntrospector extends JaxbAnnotationIntrospector {
    private static LogUtils logger = LogUtils.getLogger(CoreJaxbAnnotationIntrospector.class);
    
    private static final long serialVersionUID = 3970939370495699502L;
    private String[] ignoreableFields;
    
    @Override
    public boolean hasIgnoreMarker(AnnotatedMember annotatedMember) {
        final String METHODNAME = "hasIgnoreMarker ";
//        logger.info(METHODNAME, "annotatedMember.getName()=", annotatedMember.getName());
        JsonIgnoreProperties JsonIgnorePropertiesAnnotation = annotatedMember.getDeclaringClass().getAnnotation(JsonIgnoreProperties.class);
        if (JsonIgnorePropertiesAnnotation != null) {
            String[] values = JsonIgnorePropertiesAnnotation.value();
            for (String value : values) {
                if (annotatedMember.getMember().getName().equals(value)) {
                    return true;
                }
            }
        }

        boolean retValue = false;
        
        if ( annotatedMember.hasAnnotation(JsonProperty.class)) {
            retValue = false;
        }
        else if ( annotatedMember.hasAnnotation(JsonIgnore.class)) {
            retValue = true;
        } 
        else {
            retValue = super.hasIgnoreMarker(annotatedMember);
        }        
        
        if (ignoreableFields != null) {
            String fieldName = annotatedMember.getMember().getName().toString();
            for (String field : ignoreableFields) {
                if (field.equalsIgnoreCase(fieldName)) {
                    retValue = true;
                    break;
                }
            }
            
        }
        return retValue;
    }

    public void setIgnoreableFields(String[] ignoreableFields) {
        this.ignoreableFields = ignoreableFields;
    }
    
}
