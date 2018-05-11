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

import javax.sdp.*;
import javax.sdp.SdpFactory;
import java.util.*;

/**
 *
 * @author ron
 */
public class SDPConvert
{
    byte[] mySDPBytes;
    SDPInfo mySDPInfo;
    Version myVersion;
    Origin myOrigin;
    SessionName mySessionName;
    Connection myConnection;
    Time myTime;
    Long ss;
    Vector myTimeVector;
//    Vector audioFormats;
    javax.sdp.SdpFactory mySDPFactory;
    SessionDescription receiveSessionDescription;
    String[] myCodecArray;

    /**
     *
     */
    public SDPConvert()
    {
        mySDPFactory = SdpFactory.getInstance();
        
        myCodecArray = new String[9];
        myCodecArray[0] = "ULAW/8000";
        myCodecArray[1] = "";
        myCodecArray[2] = "";
        myCodecArray[3] = "GSM/8000";
        myCodecArray[4] = "G723/8000";
        myCodecArray[5] = "";
        myCodecArray[6] = "";
        myCodecArray[7] = "";
        myCodecArray[8] = "ALAW/8000";
    }

    /**
     *
     * @param mySDPInfo
     * @param audioFormatParam
     * @return
     */
    @SuppressWarnings("static-access")
    public byte[] info2Bytes(SDPInfo mySDPInfo, int audioFormatParam)
    {
//        System.out.println("SDPInfo to byte[]: " + mySDPInfo.toString());
        myVersion = mySDPFactory.createVersion(0);
        ss = null; try { ss = mySDPFactory.getNtpTime(new Date()); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: ss = mySDPFactory.getNtpTime(new Date()" + ex.getMessage()); }
//        myOrigin = null; try { myOrigin = mySDPFactory.createOrigin(mySDPInfo.getUser(), ss, ss, "IN", "IP4", mySDPInfo.getIPAddress()); } catch (SdpException ex) { System.out.println("Error: SdpException: myOrigin = mySDPFactory.createOrigin(\"-\", ss, ss, \"IN\", \"IP4\", " + mySDPInfo.getIPAddress() + ") " + ex.getMessage()); }
        myOrigin = null; try { myOrigin = mySDPFactory.createOrigin("-", 0L, 0L, "IN", "IP4", mySDPInfo.getIPAddress()); } catch (SdpException ex) { System.out.println("Error: SdpException: myOrigin = mySDPFactory.createOrigin(\"-\", 0L, 0L, \"IN\", \"IP4\", " + mySDPInfo.getIPAddress() + ") " + ex.getMessage()); }
        mySessionName   = mySDPFactory.createSessionName("Voice Session");
        Connection myConnection = null; try { myConnection = mySDPFactory.createConnection("IN", "IP4", mySDPInfo.getIPAddress()); } catch (SdpException ex) { System.out.println("Error: SdpException: myConnection = mySDPFactory.createConnection(\"IN\", \"IP4\", " + mySDPInfo.getIPAddress() + ") " + ex.getMessage()); }
        myTime = null; try { myTime = mySDPFactory.createTime(); } catch (SdpException ex) { System.out.println("Error: SdpException: myTime = mySDPFactory.createTime() " + ex.getMessage()); }
        myTimeVector         = new Vector();
        myTimeVector.add(myTime);
        int[] audioFormatArray = new int[2];
        audioFormatArray[0] = mySDPInfo.getAudioFormat();
        audioFormatArray[1] = 100; // telephone-event
        MediaDescription myMediaDescription = null;

        Vector myMediaDescriptionVector  = new Vector();

        try { myMediaDescription = mySDPFactory.createMediaDescription("audio", mySDPInfo.getAudioPort(), 1, "RTP/AVP", audioFormatArray); }
        catch (IllegalArgumentException ex) { System.out.println("Error: IllegalArgumentException: myAudioDescription = mySDPFactory.createMediaDescription(\"audio\", mySDPInfo.getAudioPort(), 1, \"RTP/AVP\", audioFormat) " + ex.getMessage()); }
        catch (SdpException ex) { System.out.println("Error: SdpException: myAudioDescription = mySDPFactory.createMediaDescription(\"audio\", mySDPInfo.getAudioPort(), 1, \"RTP/AVP\", audioFormat) " + ex.getMessage()); }

        Attribute myAudioAttribute = null; myAudioAttribute = mySDPFactory.createAttribute("rtpmap", "3 " + myCodecArray[audioFormatParam]);
        Attribute myTelephoneEvent = null; myTelephoneEvent = mySDPFactory.createAttribute("rtpmap", "100 telephone-event/8000"); // Don't forget to add to Vector

        myMediaDescriptionVector.add(myMediaDescription);
        myMediaDescriptionVector.add(myAudioAttribute);
        myMediaDescriptionVector.add(myTelephoneEvent);

//        if (mySDPInfo.getVideoPort() > 0)
//        {
//            int[] videoFormat = new int[1];
//            videoFormat[0] = mySDPInfo.getVideoFormat();
//            MediaDescription myVideoDescription = null; try { myVideoDescription = mySDPFactory.createMediaDescription("video", mySDPInfo.getVideoPort(), 1, "RTP/AVP", videoFormat); }
//            catch (IllegalArgumentException ex) { System.out.println("Error: IllegalArgumentException: myVideoDescription = mySDPFactory.createMediaDescription(\"video\", mySDPInfo.getVideoPort(), 1, \"RTP/AVP\", videoFormat) " + ex.getMessage()); }
//            catch (SdpException ex) { System.out.println("Error: SdpException: myVideoDescription = mySDPFactory.createMediaDescription(\"video\", mySDPInfo.getVideoPort(), 1, \"RTP/AVP\", videoFormat) " + ex.getMessage()); }
//            myMediaDescriptionVector.add(myVideoDescription);
//        }

        SessionDescription mySessionDescription = null; try { mySessionDescription = mySDPFactory.createSessionDescription(); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription = mySDPFactory.createSessionDescription() " + ex.getMessage()); }
        try { mySessionDescription.setVersion(myVersion); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setVersion(myVersion) " + ex.getMessage()); }
        try { mySessionDescription.setOrigin(myOrigin); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setOrigin(myOrigin) " + ex.getMessage()); }
        try { mySessionDescription.setSessionName(mySessionName); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setSessionName(mySessionName) " + ex.getMessage()); }
        try { mySessionDescription.setConnection(myConnection); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setConnection(myConnection) " + ex.getMessage()); }
        try { mySessionDescription.setTimeDescriptions(myTimeVector); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setTimeDescriptions(myTimeVector) " + ex.getMessage()); }
        try { mySessionDescription.setMediaDescriptions(myMediaDescriptionVector); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setMediaDescriptions(myMediaDescriptionVector) " + ex.getMessage()); }
//        try { mySessionDescription.setAttributes(myAttributeVector); } catch (SdpException ex) { System.out.println("Error: SdpException: mySessionDescription.setAttributes(myAudioAttributeVector) " + ex.getMessage()); }
        
//        byte[] mySDPBytes = new byte[mySessionDescription.toString().getBytes().length+1];
        byte[] mySDPBytes = mySessionDescription.toString().getBytes();

        return mySDPBytes;
    }

    /**
     *
     * @param mySDPBytes
     * @param audioFormatParam
     * @return
     */
    public SDPInfo bytes2Info(byte[] mySDPBytes, int audioFormatParam) // input byte[] output SDPInfo
    {
        //receiveSessionDescription = null;

        String sdpString = new String(mySDPBytes.clone());

        SessionDescription receiveSessionDescription = null; try { receiveSessionDescription = mySDPFactory.createSessionDescription(sdpString); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: receiveSessionDescription = mySDPFactory.createSessionDescription(stringContent) " + ex.getMessage()); }
                
        String myPeerIP = null; try { myPeerIP = receiveSessionDescription.getConnection().getAddress(); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: myPeerIP = receiveSessionDescription.getConnection().getAddress() " + ex.getMessage()); }
        String myUser = null; try { myUser = receiveSessionDescription.getOrigin().getUsername(); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: myPeerIP = receiveSessionDescription.getConnection().getAddress() " + ex.getMessage()); }
//        System.out.println(receiveSessionDescription.getConnection().toString()); // This is only for debugging purposes

        String myPeerName = null; try { myPeerName = receiveSessionDescription.getOrigin().getUsername(); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: myPeerName = receiveSessionDescription.getOrigin().getUsername() " + ex.getMessage()); }
        Vector receiveMediaDescriptionVector = null; receiveMediaDescriptionVector = new Vector(); try { receiveMediaDescriptionVector = receiveSessionDescription.getMediaDescriptions(false); } catch (SdpException ex) { System.out.println("Error: SdpParseException: receiveMediaDescriptionVector = receiveSessionDescription.getMediaDescriptions(false) " + ex.getMessage()); }

        // First MediaDescription (audio)
        MediaDescription myAudioDescription = (MediaDescription)receiveMediaDescriptionVector.elementAt(0);
        Media myAudio = null; myAudio = myAudioDescription.getMedia();

        Integer myAudioPort = 0;try { myAudioPort = myAudio.getMediaPort(); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: myAudioPort = myAudio.getMediaPort() " + ex.getMessage()); }
        Vector audioFormats = null; audioFormats = new Vector(); try { audioFormats = myAudio.getMediaFormats(false); } catch (SdpParseException ex) { System.out.println("Error: SdpParseException: audioFormats = myAudio.getMediaFormats(false) " + ex.getMessage()); }
//        int myAudioMediaFormat = (Integer) audioFormats.elementAt(0); // zeer problematisch audioFormat momenteel niet automatisch maar statisch

//        int myVideoPort = -1;
//        Integer myVideoMediaFormat = new Integer(-1);

//        //Optional second video medialine
//        if (receiveMediaDescriptionVector.capacity()>1)
//        {
//            MediaDescription myVideoDescription = (MediaDescription)receiveMediaDescriptionVector.elementAt(1);
//            Media myVideo = null; myVideo = myVideoDescription.getMedia();
//            try { myVideoPort = myVideo.getMediaPort(); } catch (SdpParseException error) { SoftPhoneGUI.showStatus("Error: myVideo.getMediaPort(): SdpParseException: " + error.getMessage()); }
//            Vector videoFormats = null; try { videoFormats = myVideo.getMediaFormats(false); } catch (SdpParseException error) { SoftPhoneGUI.showStatus("Error: myVideo.getMediaFormats(false): SdpParseException: " + error.getMessage()); }
//            myVideoMediaFormat = (Integer) videoFormats.elementAt(0);
//        }

        SDPInfo mySDPInfo = new SDPInfo();
        mySDPInfo.setIPAddress(myPeerIP);
        mySDPInfo.setUser(myUser);
        mySDPInfo.setAudioPort(myAudioPort);
//        mySDPInfo.setAudioFormat(myAudioMediaFormat);
        mySDPInfo.setAudioFormat(audioFormatParam);
//        mySDPInfo.setAudioFormat(3);
//        mySDPInfo.setVideoPort(myVideoPort);
//        mySDPInfo.setVideoFormat(myVideoMediaFormat.intValue());
        return mySDPInfo;
    }
}
