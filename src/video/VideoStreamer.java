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

package video;

import video.VideoFrame;
import java.net.*;
import java.io.IOException;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.control.*;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import com.sun.media.rtp.*;
import java.awt.*;

/**
 *
 * @author ron
 */
public class VideoStreamer implements ReceiveStreamListener
{
    private RTPSessionMgr   myVideoSessionManager = null;
    private Processor       myProcessor = null;
    private SendStream      sendStream = null;
    private ReceiveStream   receiveStream = null;
    private Player          player = null;
    private VideoFormat     videoFormat = null;
    private DataSource      iDS = null;
    private DataSource      oDS = null;
    private DataSource      rDS = null;
    private DataSource      tDS = null;

    private VideoFrame      videoFrame = null;
    private InetAddress     sourceIP = null;
    private InetAddress     destIP = null;
    private Integer         sourcePort = null;
    private Integer         destPort = null;
    private SessionAddress  localAddress = null;
    private SessionAddress  remoteAddress = null;
    private SessionAddress  destAddress = null;
    private String          fileName = null;
    String[] status = new String[2];

    /**
     *
     */
    @SuppressWarnings("static-access")
    public VideoStreamer() {} // Constructor

    /**
     *
     * @param sourceIPField
     * @param sourcePortField
     * @param destIPField
     * @param destPortField
     * @return
     */
    public String[] startListener( String sourceIPField, String sourcePortField, String destIPField, String destPortField)
    {
        status[0] = "0"; status[1] = "";
        try { sourceIP = InetAddress.getByName(sourceIPField); } catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: sourceIP = InetAddress.getByName(sourceIPField): UnknownHostException: " + error.getMessage(); return status; }

//        //Create SessionManager object and invoke initSession & startSession & ReceiveStreamEvents
        myVideoSessionManager = new RTPSessionMgr();
        myVideoSessionManager.addReceiveStreamListener(this);
        SessionAddress senderAddr = new SessionAddress();

        try { myVideoSessionManager.initSession(senderAddr, null, 0.05, 0.25); }
        catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: myVideoSessionManager.initSession(senderAddr, null, 0.05, 0.25): InvalidSessionAddressException: " + error.getMessage(); return status; }

        try { destIP = InetAddress.getByName(destIPField); }   catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: destIP = InetAddress.getByName(destIPField): UnknownHostException: " + error.getMessage(); return status; }

        sourcePort = Integer.parseInt(sourcePortField);
        destPort = Integer.parseInt(destPortField);

        localAddress = new SessionAddress(sourceIP, sourcePort, sourceIP, sourcePort + 1);
        remoteAddress = new SessionAddress(destIP, destPort, destIP, destPort + 1);

        try { myVideoSessionManager.startSession(localAddress, localAddress, remoteAddress, null); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: myVideoSessionManager.startSession(localAddress, localAddress, remoteAddress, null): IOException: " + error.getMessage(); return status; }
        catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: myVideoSessionManager.startSession(localAddress, localAddress, remoteAddress, null): InvalidSessionAddressException: " + error.getMessage(); return status; }
        return status;
    }

    /**
     *
     * @return
     */
    public String[] stopListener()
    {
        status[0] = "0"; status[1] = "";

        myVideoSessionManager.closeSession();
        myVideoSessionManager.removeReceiveStreamListener(this);
        myVideoSessionManager.dispose();
        return status;
    }

    /**
     *
     * @param destIPField
     * @param destPortField
     * @param fileName
     * @param fmt
     * @return
     */
    @SuppressWarnings("static-access")
    public String[] startStreamer(String destIPField, String destPortField, String fileName, int fmt)
    {
        status[0] = "0"; status[1] = "";
        InetAddress myDestIP = null; try { myDestIP = InetAddress.getByName(destIPField); }
        catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: myDestIP = InetAddress.getByName(destIPField): UnknownHostException: " + error.getMessage(); return status; }
        Integer myDestPort = Integer.parseInt(destPortField);

        // Define medialocator
        MediaLocator mediaLocator = new MediaLocator(fileName);

        try { iDS = Manager.createDataSource(mediaLocator); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: iDS = Manager.createDataSource(mediaLocator): IOException: " + error.getMessage(); return status; }
        catch (NoDataSourceException error) { status[0] = "1"; status[1] = "Error: iDS = Manager.createDataSource(mediaLocator): NoDataSourceException: " + error.getMessage(); return status; }

        //Create processor and setup processing rules
        try { myProcessor = Manager.createProcessor(iDS); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: myProcessor = Manager.createProcessor(iDS): IOException: " + error.getMessage(); return status; }
        catch (NoProcessorException error) { status[0] = "1"; status[1] = "Error: myProcessor = Manager.createProcessor(iDS): NoProcessorException: " + error.getMessage(); return status; }

        myProcessor.configure(); while ( myProcessor.getState() != Processor.Configured )
        { try { Thread.sleep(20); } catch (InterruptedException error) { status[0] = "1"; status[1] = "Error: Thread.sleep(20): InterruptedException: " + error.getMessage(); return status; } }

        myProcessor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN));
        TrackControl track[] = myProcessor.getTrackControls();
        switch (fmt)
        {
            case 0: { videoFormat = new VideoFormat(VideoFormat.JPEG_RTP); break; }
            case 1: { videoFormat = new VideoFormat(VideoFormat.H263); break; }
            case 2: { videoFormat = new VideoFormat(VideoFormat.H263_RTP); break; }
            case 3: { videoFormat = new VideoFormat(VideoFormat.H263_1998_RTP); break; }
            case 4: { videoFormat = new VideoFormat(VideoFormat.MPEG_RTP); break; }
        }

        // Check whether format is supported
        boolean formatMatch = false;
        Format mySupportedFormats[] = track[0].getSupportedFormats();
        for (int counter=0; counter < mySupportedFormats.length; counter++)
        {
            status[0] = "1"; status[1] = "Fmt: " + mySupportedFormats[counter].toString();
            if ( videoFormat.matches(mySupportedFormats[counter]) ) {formatMatch=true;}
        }
        if (formatMatch == false)
        { status[0] = "1"; status[1] = "Error: videoFormat not supported: " + Integer.toString(fmt); return status; } // else the program will get beyond this testpoint

        track[0].setFormat(videoFormat);
        myProcessor.realize();
        while ( myProcessor.getState() != Processor.Realized ) { try { Thread.sleep(20); } catch (InterruptedException error) { status[0] = "1"; status[1] = "Error: Thread.sleep(20): InterruptedException: " + error.getMessage(); return status; } }

        //Next obtain the output DataSource
        tDS = myProcessor.getDataOutput();

        destAddress = new SessionAddress(myDestIP, myDestPort, myDestIP, myDestPort + 1);

        try { myVideoSessionManager.startSession(localAddress, localAddress, destAddress, null); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: myVideoSessionManager.startSession(localAddress, localAddress, destAddress, null): IOException: " + error.getMessage(); return status; }
        catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: myVideoSessionManager.startSession(localAddress, localAddress, destAddress, null): InvalidSessionAddressException: " + error.getMessage(); return status; }

        //Next we obtain a sendstream from the DataSources obtained as output of the processor
        try { sendStream = myVideoSessionManager.createSendStream(tDS, 0); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: sendStream = myVideoSessionManager.createSendStream(tDS, 0): IOException: " + error.getMessage(); return status; }
        catch (UnsupportedFormatException error) { status[0] = "1"; status[1] = "Error: sendStream = myVideoSessionManager.createSendStream(tDS, 0): UnsupportedFormatException: " + error.getMessage(); return status; }

        //Start capture and transmission
        try { sendStream.start(); myProcessor.start(); } catch (IOException error) { status[0] = "1"; status[1] = "Error: sendStream.start(); myProcessor.start();: IOException: " + error.getMessage(); return status; } // Starts send the stream
        return status;
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("static-access")
    public String[] getMultiMediaStatus()
    {
        status[1] = "MultiMediaStatus:\n\n";
        if ( myProcessor != null)
        {
            switch (myProcessor.getState())
            {
                case Processor.Unrealized:  { status[1] += "Processor Unrealized: "     + Processor.Unrealized + "\n"; break;}
                case Processor.Realizing:   { status[1] += "Processor Realizing: "      + Processor.Realizing + "\n"; break;}
                case Processor.Realized:    { status[1] += "Processor Realized: "       + Processor.Realized + "\n"; break;}
                case Processor.Configuring: { status[1] += "Processor Configuring: "    + Processor.Configuring + "\n"; break;}
                case Processor.Configured:  { status[1] += "Processor Configured: "     + Processor.Configured + "\n"; break;}
                case Processor.Prefetching: { status[1] += "Processor Prefetching: "    + Processor.Prefetching + "\n"; break;}
                case Processor.Prefetched:  { status[1] += "Processor Prefetched: "     + Processor.Prefetched + "\n"; break;}
                case Processor.Started:     { status[1] += "Processor Started: "        + Processor.Started + "\n"; break;}
                default:                    { status[1] += "Processor Unknown state: "  + myProcessor.getState() + "\n"; }
            }
        }   else { status[1] += "myProcessor not instantiated\n"; }

        if ( player != null)
        {
            switch (player.getState())
            {
                case Player.Unrealized:  { status[1] += "Player Unrealized: " + Player.Unrealized + "\n"; break;}
                case Player.Realizing:   { status[1] += "Player Realizing: " + Player.Realizing + "\n"; break;}
                case Player.Realized:    { status[1] += "Player Realized: " + Player.Realized + "\n"; break;}
                case Player.Prefetching: { status[1] += "Player Prefetching: " + Player.Prefetching + "\n"; break;}
                case Player.Prefetched:  { status[1] += "Player Prefetched: " + Player.Prefetched + "\n"; break;}
                case Player.Started:     { status[1] += "Player Started: " + Player.Started + "\n"; break;}
                default:                 { status[1] += "Player Unknown state: " + player.getState() + "\n"; }
            }
        }   else { status[1] += "player not instantiated\n"; }

        return status;
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("static-access")
    public String[] stopMedia()
    {
        status[0] = "0"; status[1] = "";
        //Check whether we are the sending side
        //myProcessor.getState();
        //First stop the player
        if ( myProcessor != null)
        {
            switch (myProcessor.getState())
            {
                case Processor.Unrealized:  { status[1] = "Processor Unrealized..."; break; }
                case Processor.Realizing:   { status[1] = "Processor Realizing..."; break;  }
                case Processor.Realized:    { status[1] = "Processor Realized..."; break;   }
                case Processor.Configuring: { status[1] = "Processor Configuring..."; break; }
                case Processor.Configured:  { status[1] = "Processor Configured..."; break; }
                case Processor.Prefetching: { status[1] = "Processor Prefetching..."; break; }
                case Processor.Prefetched:  { status[1] = "Processor Prefetched..."; myProcessor.close(); break; }
                case Processor.Started:
                {
                    status[1] = "Processor Started...";
                    myProcessor.stop(); myProcessor.close();
                    try { sendStream.stop(); sendStream.close(); }
                    catch (IOException error) { status[0] = "1"; status[1] = "Error: : : " + error.getMessage(); return status; }
                    break;
                }
                default: { status[1] = "Processor Unknown state: " + myProcessor.getState(); }
            }
        }

        if ( player != null)
        {
            switch (player.getState())
            {
                case Player.Unrealized:  { status[1] = "Player Unrealized..."; break; }
                case Player.Realizing:   { status[1] = "Player Realizing..."; break; }
                case Player.Realized:    { status[1] = "Player Realized..."; break; }
                case Player.Prefetching: { status[1] = "Player Prefetching..."; break; }
                case Player.Prefetched:  { status[1] = "Player Prefetched..."; player.close(); break; }
                case Player.Started:     { status[1] = "Player Started..."; player.stop(); player.close(); break; }
                default: { status[1] = "Player Unknown state: " + player.getState() ; }
            }
        }

        if ( videoFrame != null)
        {
            videoFrame.dispose();
        }
        stopListener();
        return status;

//        myVideoSessionManager.closeSession();
//        myVideoSessionManager.dispose();
    }

    /**
     *
     * @param event
     */
    @SuppressWarnings("static-access")
    public void update(ReceiveStreamEvent event)
    {
        status[0] = "0"; status[1] = "";

        if (event instanceof NewReceiveStreamEvent)
        {
            receiveStream = event.getReceiveStream();
            rDS = receiveStream.getDataSource();
            try { player = Manager.createRealizedPlayer(rDS); }
            catch (IOException ex) {  }
            catch (NoPlayerException ex) {  }
            catch (CannotRealizeException ex) {  }
            Component component = player.getVisualComponent();
            Dimension dimension = component.getSize();
            videoFrame = new VideoFrame();
            videoFrame.jPanel1.add(component);
            videoFrame.setSize(dimension);
            videoFrame.pack();
            videoFrame.setVisible(true);
            player.start();
        }
        else
        {
            player.start();
        }
    }
}