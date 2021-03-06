/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;

// Weblog tries to log Voipstorm activity to a webserver

/**
 *
 * @author ron
 */
public class WebLog
{
    private HttpHost target;
    private SchemeRegistry supportedSchemes;
    private SocketFactory socketFactory;
    private HttpParams params;
    private HttpRequest req;
    private HttpContext webContext;
    private ClientConnectionOperator connector;

    /**
     *
     * @throws Exception
     */
    public WebLog() throws Exception
    {
        target = new HttpHost("www.voipstorm.nl", 80, "http");

        supportedSchemes = new SchemeRegistry();
        socketFactory = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", socketFactory, 80));
        webContext = new BasicHttpContext();
        connector = new DefaultClientConnectionOperator(supportedSchemes);
    }

    /**
     *
     * @param messageParam
     * @throws Exception
     */
    public void send(String messageParam) throws Exception
    {
        params = new BasicHttpParams();
        params.setParameter("Origin", "VoipStorm Manager");
        HttpProtocolParams.setUseExpectContinue(params, false);
        params.setParameter("Message", messageParam);
        HttpProtocolParams.setUseExpectContinue(params, false);
        req = new BasicHttpRequest("OPTIONS", "VoipStorm " + messageParam, HttpVersion.HTTP_1_1);
        req.addHeader("Host", target.getHostName());

        OperatedClientConnection conn = connector.createConnection();
        try
        {
            connector.openConnection(conn, target, null, webContext, params);
            conn.sendRequestHeader(req);
            conn.flush();
            HttpResponse rsp = conn.receiveResponseHeader();
        }
        finally
        {
            conn.close();
        }
    }
}

