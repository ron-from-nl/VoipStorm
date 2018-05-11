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

public class SDPInfo
{
    private String ipAddress;
    private String user;
    private int audioPort = 0;
    private int audioFormat = 0;
    private int videoPort = 0;
    private int videoFormat = 0;

    /**
     *
     */
    public SDPInfo() { ipAddress = "";} // Constructor without parameters

    /**
     *
     * @param ipAddressParam
     */
    public void setIPAddress(String ipAddressParam)     { ipAddress   = ipAddressParam; }

    /**
     *
     * @param userParam
     */
    public void setUser(String userParam)               { user   = userParam; }

    /**
     *
     * @param audioPortParam
     */
    public void setAudioPort(int audioPortParam)        { audioPort   = audioPortParam; }

    /**
     *
     * @param audioFormatParam
     */
    public void setAudioFormat(int audioFormatParam)    { audioFormat = audioFormatParam; }

    /**
     *
     * @param videoPortParam
     */
    public void setVideoPort(int videoPortParam)        { videoPort   = videoPortParam; }

    /**
     *
     * @param videoFormatParam
     */
    public void setVideoFormat(int videoFormatParam)    { videoFormat = videoFormatParam; }
    
    /**
     *
     * @return
     */
    public String getIPAddress()    { return ipAddress; }

    /**
     *
     * @return
     */
    public String getUser()         { return user; }

    /**
     *
     * @return
     */
    public int    getAudioPort()    { return audioPort; }

    /**
     *
     * @return
     */
    public int    getAudioFormat()  { return audioFormat; }

    /**
     *
     * @return
     */
    public int    getVideoPort()    { return videoPort; }

    /**
     *
     * @return
     */
    public int    getVideoFormat()  { return videoFormat; }

    @Override
    public String toString()
    {
        String sdpString = "\r\n" +
        "ipAddress: " +     ipAddress + "\r\n" +
        "user: " +          user + "\r\n" +
        "audioPort: " +     audioPort + "\r\n" +
        "audioFormat: " +   audioFormat + "\r\n" +
        "videoPort: " +     videoPort + "\r\n" +
        "videoFormat: " +   videoFormat + "\r\n";
        return sdpString;
    }
}
