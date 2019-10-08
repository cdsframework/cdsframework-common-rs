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

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.WebApplicationException;
import static javax.ws.rs.core.HttpHeaders.CONTENT_ENCODING;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.cdsframework.common.rs.client.RSClient;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
@Provider
public class CoreRequestWriteInterceptor implements WriterInterceptor{

    private static LogUtils logger = LogUtils.getLogger(CoreRequestWriteInterceptor.class);
    private static final String GZIP = "gzip";
    
    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final String METHODNAME = "aroundWriteTo ";

        //
        // This checks the header to see if the client wants a gzipped request
        // If so, the request is gzipped
        //
        boolean gzipContent = false;
        MultivaluedMap<String, Object> headers = writerInterceptorContext.getHeaders();
        if (!headers.isEmpty()) {
            Object contentEncoding = headers.getFirst(CONTENT_ENCODING);
            logger.debug(METHODNAME, "contentEncoding=", contentEncoding);
            if (contentEncoding != null && ((String) contentEncoding).equalsIgnoreCase((RSClient.GZIP))) {
                gzipContent = true;
            }
        }

        /*
        Set<Map.Entry<String, List<Object>>> entrySet = headers.entrySet();
        for (Map.Entry<String, List<Object>> entry : entrySet) {
            String key = entry.getKey();
            List<Object> value = entry.getValue();
            logger.info(METHODNAME, "key=", key);
            logger.info(METHODNAME, "value=", value);
        }
        */

        logger.debug(METHODNAME, "gzipContent=", gzipContent);
        if (gzipContent) {
            logger.debug(METHODNAME, "compressing content");
            final OutputStream outputStream = writerInterceptorContext.getOutputStream();
            writerInterceptorContext.setOutputStream(new GZIPOutputStream(outputStream));
        }
        writerInterceptorContext.proceed();        
        
    }
    
}