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
package org.cdsframework.common.rs.exception.mapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cdsframework.common.util.StringUtils;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public final class ErrorMessage implements Serializable {
    private static final long serialVersionUID = -6297158478998842202L;
    private LogUtils logger = LogUtils.getLogger(ErrorMessage.class);
    private Integer status;
    private String exceptionClass;
    private String exceptionMessage;
    private String rootCauseExceptionClass;
    private String rootCauseMessage;
    private StackTraceElement[] stackTraceElements;
    private Map<String, Object> propertyMap = new HashMap<String, Object>();
    
    public ErrorMessage() {
        
    }
    
    public ErrorMessage(Throwable throwable, boolean returnStackTrace) {
        final String METHODNAME = "Constructor ";
        setExceptionClass(throwable.getClass().getCanonicalName());
        Throwable rootCause = ExceptionUtils.getRootCause(throwable);
        // Is there a root cause?
        
        if (rootCause != null) {
            setRootCauseExceptionClass(rootCause.getClass().getCanonicalName());
            setRootCauseMessage(rootCause.getMessage());
            
            // append the root cause to the message
            String message = throwable.getMessage();
            if (StringUtils.isEmpty(message)) {
                message = rootCause.getMessage();
            }
            else {
                message += " rootCause: " + rootCause.getMessage();
            }
            setExceptionMessage(message);
        }
        else {
            rootCause = throwable;
            setExceptionMessage(throwable.getMessage());
        }
        
//        if (throwable instanceof BaseException) {
//            if (throwable instanceof ValidationException) {
//                ValidationException validationException = (ValidationException) throwable;
//                setBrokenRules(validationException.getBrokenRules());
//            }
//            propertyMap.putAll(((BaseException) throwable).getPropertyMap());
//        }
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME, "returnStackTrace=", returnStackTrace);
        }
        if (returnStackTrace) {
            setStackTraceElements(rootCause.getStackTrace());
        }
    }
    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @XmlTransient
    public String getMessage() {
        return exceptionMessage;
    }

    @XmlTransient
    public String getStackTrace() {
        StringBuilder sb = new StringBuilder();
        if (stackTraceElements != null) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                sb.append(stackTraceElement.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    public String getRootCauseExceptionClass() {
        return rootCauseExceptionClass;
    }

    public void setRootCauseExceptionClass(String rootCauseExceptionClass) {
        this.rootCauseExceptionClass = rootCauseExceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getRootCauseMessage() {
        return rootCauseMessage;
    }

    public void setRootCauseMessage(String rootCauseMessage) {
        this.rootCauseMessage = rootCauseMessage;
    }

    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" + "status=" + status + ", exceptionClass=" + exceptionClass + ", exceptionMessage=" + exceptionMessage + ", rootCauseExceptionClass=" + rootCauseExceptionClass + ", rootCauseMessage=" + rootCauseMessage + ", propertyMap=" + propertyMap + '}';
    }

}
