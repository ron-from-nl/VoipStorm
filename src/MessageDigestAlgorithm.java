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

import java.security.*;

/**
 *
 * @author ron
 */
public class MessageDigestAlgorithm
{
    private boolean debugging = false;

    /**
     * Calculates an http authentication response in accordance with rfc2617.
     * <p>
     * @param algorithm a string indicating a pair of algorithms (MD5 (default),
     * or MD5-sess) used to produce the digest and a checksum.
     * @param username_value username_value (see rfc2617)
     * @param realm_value A string that has been displayed to the user in order
     * to determine the context of the username and password to use.
     * @param passwd the password to encode in the challenge response.
     * @param nonce_value A server-specified data string provided in the
     * challenge.
     * @param cnonce_value an optional  client-chosen value whose purpose is
     * to foil chosen plaintext attacks.
     * @param method the SIP method of the request being challenged.
     * @param digest_uri_value the value of the "uri" directive on the
     * Authorization header in the request.
     * @param entity_body the entity-body
     * @param qop_value Indicates what "quality of protection" the client has
     * applied to the message.
     * @param nc_value  the hexadecimal count of the number of requests
     * (including the current request) that the client has sent with the nonce
     * value in this request.
     * @return a digest response as defined in rfc2617
     * @throws NullPointerException in case of incorrectly null parameters.
     */
    String calculateResponse(String algorithm,
                                    String username_value,
                                    String realm_value,
                                    String passwd,
                                    String nonce_value,
                                    String nc_value,
                                    String cnonce_value,
                                    String method,
                                    String digest_uri_value,
                                    String entity_body,
                                    String qop_value)
    {
        if (username_value == null || realm_value == null || passwd == null
            || method == null || digest_uri_value == null || nonce_value == null)
        {  throw new NullPointerException("Null parameter to MessageDigestAlgorithm.calculateResponse()"); }

        //The following follows closely the algorithm for generating a response
        //digest as specified by rfc2617
        String A1 = null;

        if (algorithm == null
            || algorithm.trim().length() == 0
            || algorithm.trim().equalsIgnoreCase("MD5"))
        {
            if (debugging == true) {System.out.print(" 1 ");}
            A1 = username_value + ":" + realm_value + ":" + passwd;
        }
        else
        {
            if (debugging == true) {System.out.print(" 2 ");}
            if (cnonce_value == null || cnonce_value.length() == 0) { if (debugging == true) {System.out.print("3");} throw new NullPointerException("cnonce_value may not be absent for MD5-Sess algorithm.");}

            if (debugging == true) {System.out.print(" 4 ");}
            A1 = H(username_value + ":" + realm_value + ":" + passwd)
                + ":" + nonce_value + ":" + cnonce_value;
        }

        String A2 = null;
        if (qop_value == null
            || qop_value.trim().length() == 0
            || qop_value.trim().equalsIgnoreCase("auth"))
        {
            if (debugging == true) {System.out.print(" 5 ");}
            A2 = method + ":" + digest_uri_value;
        }
        else
        {
            if (debugging == true) {System.out.print(" 6 ");}
            if (entity_body == null) { if (debugging == true) {System.out.print(" 7 ");} entity_body = ""; }
            A2 = method + ":" + digest_uri_value + ":" + H(entity_body);
        }


        String request_digest = null;

        if( cnonce_value != null
            && qop_value != null
            && nc_value!=null
            && (qop_value.equalsIgnoreCase("auth")
                || qop_value.equalsIgnoreCase("auth-int")))

        {
            if (debugging == true) {System.out.print(" 8 ");}
            request_digest  = KD ( H(A1), nonce_value
                                          + ":" + nc_value
                                          + ":" + cnonce_value
                                          + ":" + qop_value
                                          + ":" + H(A2)
                              );

        }
        else
        {
            if (debugging == true) {System.out.print(" 9 ");}
            request_digest = KD(H(A1), nonce_value + ":" + H(A2)); // 2 params. 1) secret. 2) data.
        }

        return request_digest;
    }

    /**
     * Defined in rfc 2617 as
     * H(data) = MD5(data);
     *
     * @param data data
     * @return MD5(data)
     */
    private String H(String data)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            return toHexString(digest.digest(data.getBytes()));
        }
        catch (NoSuchAlgorithmException error)
        {
            //shouldn't happen
            System.out.println("Failed to instantiate an MD5 algorithm" + error);
            return null;
        }
    }

    /**
    * Defined in rfc 2617 as
    * KD(secret, data) = H(concat(secret, ":", data))
    * @param data data
    * @param secret secret
    * @return H(concat(secret, ":", data));
    */
   private String KD(String secret, String data)
   {
       return H(secret + ":" + data); // secret = HA1, data = nonce_value:HA2 = response = H(HA1:nonce:HA2)
   }


    // the following code was copied from the NIST-SIP instant
    // messenger (its author is Olivier Deruelle). Thanks for making it public!
    /**
     * to hex converter
     */
    private final char[] toHex = {
        '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'a', 'b', 'c', 'd',
        'e', 'f'};

    /**
     * Converts b[] to hex string.
     * @param b the bte array to convert
     * @return a Hex representation of b.
     */
    private String toHexString(byte b[])
    {
        int pos = 0;
        char[] c = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            c[pos++] = toHex[ (b[i] >> 4) & 0x0F];
            c[pos++] = toHex[b[i] & 0x0f];
        }
        return new String(c);
    }
}
