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

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

/**
 *
 * @author ron
 */
public class PublicIP
{

    /**
     *
     * @return
     */
    public static String getPublicIP()
    {
        String publicIP = null;
        try
        {
            URL tempURL = new URL("http://www.whatismyip.org/");
            HttpURLConnection tempConn = (HttpURLConnection)tempURL.openConnection();
            InputStream tempInStream = tempConn.getInputStream();
            InputStreamReader tempIsr = new InputStreamReader(tempInStream);
            BufferedReader tempBr = new BufferedReader(tempIsr);
            publicIP = tempBr.readLine();
            tempBr.close();
            tempInStream.close();
        }
        catch (Exception ex)
        {
            publicIP = "<Could-Not-Resolve-Public-IP-Address>";
        }
        return publicIP;
    }
}

