/*
 * Copyright (C) 2015 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about the this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */
package org.cdsframework.common.rs.provider;

import javax.ws.rs.ClientErrorException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.cdsframework.common.rs.exception.mapper.ErrorMessage;
import org.cdsframework.common.rs.support.CoreConfiguration;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static LogUtils logger = LogUtils.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        final String METHODNAME = "toResponse ";
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME);
        }
        ErrorMessage errorMessage = new ErrorMessage(ex, CoreConfiguration.isReturnStackTrace());

        setHttpStatus(ex, errorMessage);
        return Response.status(errorMessage.getStatus()).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
    }

    protected void setHttpStatus(Throwable ex, ErrorMessage errorMessage) {
        final String METHODNAME = "setHttpStatus ";

        if (ex != null) {
            Throwable cause = ex;
            if (ex.getCause() != null) {
                cause = ex.getCause();
            }
        }
        

        if (ex instanceof ClientErrorException) {
            ClientErrorException clientErrorException = (ClientErrorException) ex;
            errorMessage.setStatus(clientErrorException.getResponse().getStatus());
        } else {
            logger.error(METHODNAME, "An Unexpected Exception has occurred; Message: ", ex.getMessage(), ex);
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); //defaults to internal server error 500
        }
    }
}
