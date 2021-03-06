/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package com.admtel.telephonyserver.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;


/**
 * A HTTP response message.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class HttpResponseMessage {
    /** HTTP response codes */
    public static final int HTTP_STATUS_SUCCESS = 200;

    public static final int HTTP_STATUS_NOT_FOUND = 404;

    /** Map<String, String> */
    private Map<String, String> headers = new HashMap<String, String>();
    

    /** Storage for body of HTTP response. */
    private ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

    private int responseCode = HTTP_STATUS_SUCCESS;

    public HttpResponseMessage() {
        headers.put("Server", "HttpServer (ADM Telephony Server)");
        headers.put("Cache-Control", "private");
        headers.put("Content-Type", "text/html; charset=iso-8859-1");
        headers.put("Connection", "keep-alive");
        headers.put("Keep-Alive", "200");
        headers.put("Date", new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
        headers.put("Last-Modified", new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
    }

    public void addToCookie(String key, String value){
    	String cookie = headers.get("Set-Cookie");
    	String v = "";
    	if (key == null) return;
    	if (value == null){
    		v = key;
    	}
    	else{
    		v = key+"="+value;
    	}
    	
    	if (cookie == null){
    		headers.put("Set-Cookie", v);
    	}
    	else{
    		cookie +=" ; " + v;
    		headers.put("Set-Cookie", cookie);
    	}    	
    }
    public Map getHeaders() {
        return headers;
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void appendBody(byte[] b) {
        try {
            body.write(b);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void appendBody(String s) {
        try {
            body.write(s.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public IoBuffer getBody() {
        return IoBuffer.wrap(body.toByteArray());
    }

    public int getBodyLength() {
        return body.size();
    }
}
