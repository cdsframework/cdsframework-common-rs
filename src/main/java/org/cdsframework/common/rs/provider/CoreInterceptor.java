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
import java.io.PushbackInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.HttpHeaders.ACCEPT_ENCODING;
import static javax.ws.rs.core.HttpHeaders.CONTENT_ENCODING;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
@Provider
public class CoreInterceptor implements ReaderInterceptor, WriterInterceptor {

    private static LogUtils logger = LogUtils.getLogger(CoreInterceptor.class);

    @Context
    private ResourceInfo resourceInfo;
    
    @Context
    private HttpServletRequest httpServletRequest;
    
    private static final String GZIP = "gzip";    
    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final String METHODNAME = "aroundWriteTo ";

        //
        // This checks the header to see if the requestor wants a gzipped response
        // If so, the response is gzipped and return with content-encoding for the caller to unzip
        //
        String acceptEncoding = httpServletRequest.getHeader(ACCEPT_ENCODING);
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME, "acceptEncoding=", acceptEncoding);
        }
        if (acceptEncoding != null && acceptEncoding.contains(GZIP)) {
            if (logger.isDebugEnabled()) {
                logger.debug(METHODNAME, "setOutputStream contains GZIP");
            }
            final OutputStream outputStream = writerInterceptorContext.getOutputStream();
            writerInterceptorContext.setOutputStream(new GZIPOutputStream(outputStream));
            writerInterceptorContext.getHeaders().putSingle(CONTENT_ENCODING, GZIP); 
        }
        writerInterceptorContext.proceed();
    }
    
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext readerInterceptorContext) throws IOException, WebApplicationException {
        final String METHODNAME = "aroundReadFrom ";
        
        List<String> contentEncoding = readerInterceptorContext.getHeaders().get(CONTENT_ENCODING);
        List<String> acceptEncoding = readerInterceptorContext.getHeaders().get(ACCEPT_ENCODING);
        if (logger.isDebugEnabled()) {
            logger.debug(METHODNAME, "contentEncoding=", contentEncoding);
            logger.debug(METHODNAME, "acceptEncoding=", acceptEncoding);
        }

        // If the request header indicates that context-encoding gziped, its decompressed
        if (contentEncoding != null && contentEncoding.contains(GZIP)) {
            if (logger.isDebugEnabled()) {
                logger.debug(METHODNAME, "context decompressed");
            }
            PushbackInputStream pushbackInputStream = new PushbackInputStream(readerInterceptorContext.getInputStream(), 2);
            byte [] signature = new byte[2];
            int len = pushbackInputStream.read( signature ); //read the signature
            pushbackInputStream.unread( signature, 0, len ); //push back the signature to the stream
            if( signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b ) { //check if matches standard gzip magic number
                if (logger.isDebugEnabled()) {
                    logger.debug(METHODNAME, "decompressing content");
                }
                readerInterceptorContext.setInputStream(new GZIPInputStream(pushbackInputStream));
            }
            else {
                logger.warn(METHODNAME, "content NOT GZIPPED, why?, treating as NOT GZIPPED");
                readerInterceptorContext.setInputStream(pushbackInputStream);
            }            

        }

        return readerInterceptorContext.proceed();
    }
    
}
