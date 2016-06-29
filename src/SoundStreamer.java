import java.net.*;
import java.io.IOException;
//import java.util.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.control.*;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import com.sun.media.rtp.*;

/**
 *
 * @author ron
 */
public class SoundStreamer implements ReceiveStreamListener
{
    private boolean muteAudio;
    private RTPSessionMgr rtpSessionManager;
    private InetAddress sourceIP;
    private InetAddress destinationIP;
    private Integer     sourcePort;
    private Integer     destinationPort;
    private SessionAddress localAddress;
    private SessionAddress remoteAddress;
    private SessionAddress destAddress;
    private Processor processor;
    private SendStream sendStream;
    private ReceiveStream receiveStream;
    private Player player;
    private AudioFormat audioFormat;
    private DataSource iDS;
    private DataSource oDS;
    private DataSource rDS;
    private DataSource tDS;
//    String[] status = new String[2];
    private boolean isListening;
    private static int AUTO_SOURCE_PORT_RANGE_START = 10000; // Default 10000
    private static int AUTO_SOURCE_PORT_RANGE_END   = 49998; // Default 49998

    /**
     *
     */
    public long mediaDuration                       = 1000;
    
    private final double RTCP_BW_FRACTION           = 0.05; // RTCP Report Bandwidth Fraction usage (Default: 0.05)
    private final double RTCP_SENDER_BW_FRACTION    = 0.25; // Fraction of Above Fraction for Sender reports (Default: 0.25)
//    private BufferControl bufferControl;

    SessionAddress localSessionAddress;
    UserInterface userInterface;
//    int superId;

    @SuppressWarnings("static-access")
    SoundStreamer(UserInterface userInterfaceParam, int superIdParam)
    {
        userInterface = userInterfaceParam;
//        superId = superIdParam;
    } // Dodgy Constructor

    /**
     *
     * @param sourceIPParam
     * @param sourcePortParam
     * @param destinationIPParam
     * @param destinationPortParam
     * @return
     */
    synchronized public String[] startListener( String sourceIPParam, int sourcePortParam, String destinationIPParam, int destinationPortParam)
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "0";
        try { sourceIP = InetAddress.getByName(sourceIPParam); } catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: UnknownHostException: sourceIP = InetAddress.getByName( " + sourceIPParam + "): UnknownHostException: " + error.getMessage(); return status;}

//        //Create SessionManager object and invoke initSession & startSession & ReceiveStreamEvents
        rtpSessionManager = new RTPSessionMgr();
        rtpSessionManager.addReceiveStreamListener(this);
        localSessionAddress = new SessionAddress();
        //serInterface.log("localSessionAddress:\n" + localSessionAddress.toString());

        try { rtpSessionManager.initSession(localSessionAddress, null, RTCP_BW_FRACTION, RTCP_SENDER_BW_FRACTION); } // localSessionAddress is local!!! At first localaddress is set to auto because of null
        catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: InvalidSessionAddressException: voiceSessionManager.initSession(localSessionAddress, null, RTCP_BW_FRACTION, RTCP_SENDER_BW_FRACTION): InvalidSessionAddressException: " + error.getMessage(); return status; }

        try { destinationIP = InetAddress.getByName(destinationIPParam); }   catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: UnknownHostException: destinationIP = InetAddress.getByName(" + destinationIPParam + "): UnknownHostException: " + error.getMessage(); return status; }

        boolean startSessionUnsuccessfull = false;
        int retryCounter = 0; int retryLimit = 5; int retryInterval = 100;
        do
        {
            startSessionUnsuccessfull = false;
            if (sourcePortParam == 0)
            {
                sourcePort = findSoundPorts();
                status[0] = "0"; status[1] = sourcePort.toString();
            }
            else
            {
                sourcePort = sourcePortParam; status[0] = "0"; status[1] = sourcePort.toString();
            }
            destinationPort = destinationPortParam;

            localAddress = new SessionAddress(sourceIP, sourcePort, sourceIP, sourcePort + 1); // possibly incomming and outgoing local ports
            remoteAddress = new SessionAddress(destinationIP, destinationPort, destinationIP, destinationPort + 1); // possibly incomming and outgoing remote ports

            try { rtpSessionManager.startSession(localAddress, localAddress, remoteAddress, null); } // null is encryption info
            catch (IOException error)
            {
                startSessionUnsuccessfull = true; // userInterface.logToApplication("Error: startListener IOException: voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again");
                if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: startListener IOException: voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                retryCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }
            catch (InvalidSessionAddressException error)
            {
                startSessionUnsuccessfull = true; // userInterface.logToApplication("Error: startListener InvalidSessionAddressException: voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again");
                if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: startListener InvalidSessionAddressException: voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                retryCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }

        } while(startSessionUnsuccessfull);

        isListening = true;
        return status;
    }

    /**
     *
     * @return
     */
    public String[] stopListener()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
	if ( isListening );
	{
	    if (rtpSessionManager != null)
            {
                rtpSessionManager.closeSession();
                rtpSessionManager.removeReceiveStreamListener(this);
                rtpSessionManager.dispose();
                rtpSessionManager = null;

                localSessionAddress = null;
                destinationIP = null;
                sourceIP = null;
                sourcePort = null;
                destinationIP = null;
                destinationPort = null;
                localAddress = null;
                remoteAddress = null;
                userInterface = null;
                iDS = null;
                oDS = null;
                rDS = null;
                tDS = null;
                userInterface = null;
                processor = null;
                player = null;
                sendStream = null;
                receiveStream = null;
                audioFormat = null;
            }
	}
        isListening = false;
        return status;
    }

    /**
     *
     * @return
     */
    synchronized public String[] muteAudio()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        if (rtpSessionManager != null)
        {
            rtpSessionManager.closeSession();
            rtpSessionManager.dispose();
        }
        return status;
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("empty-statement")
    synchronized public String[] unMuteAudio()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        if (rtpSessionManager != null)
        {
            rtpSessionManager = new RTPSessionMgr();
            rtpSessionManager.addReceiveStreamListener(this);
            try { rtpSessionManager.initSession(localSessionAddress, null, RTCP_BW_FRACTION, RTCP_SENDER_BW_FRACTION); } // localSessionAddress is local!!!
            catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: voiceSessionManager.initSession(localSessionAddress, null, RTCP_BW_FRACTION, RTCP_SENDER_BW_FRACTION): InvalidSessionAddressException: " + error.getMessage(); return status; }

            boolean startSessionUnsuccessfull = false;
            int retryCounter = 0; int retryLimit = 5; int retryInterval = 100;
            do
            {
                startSessionUnsuccessfull = false;
                try { rtpSessionManager.startSession(localAddress, localAddress, remoteAddress, null); } // null is encryption info
                catch (IOException error)
                {
                    startSessionUnsuccessfull = true; // userInterface.logToApplication("Error: IOException voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again");
                    if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: IOException voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                    retryCounter++;
                    try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                }
                catch (InvalidSessionAddressException error)
                {
                    startSessionUnsuccessfull = true; // userInterface.logToApplication("Error: InvalidSessionAddressException voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again"); try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                    if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: InvalidSessionAddressException voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                    retryCounter++;
                    try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                }

            } while (startSessionUnsuccessfull);

//            try { voiceSessionManager.startSession(localAddress, localAddress, remoteAddress, null); } // null is encryption info
//            catch (IOException error)                       { status[0] = "1"; status[1] = "Error: disableMuteAudio(): voiceSessionManager.startSession(" + localAddress + ", " + localAddress+ ", " + remoteAddress + ", null): IOException: " + error.getMessage(); return status; }
//            catch (InvalidSessionAddressException error)    { status[0] = "1"; status[1] = "Error: disableMuteAudio(): voiceSessionManager.startSession(" + localAddress + ", " + localAddress+ ", " + remoteAddress + ", null): InvalidSessionAddressException: " + error.getMessage(); return status; }
        }
        return status;
    }

    /**
     *
     * @param destinationIPParam
     * @param destinationPortParam
     * @return
     */
    @SuppressWarnings({"static-access", "empty-statement"})
    synchronized public String[] updateDestination(String destinationIPParam, String destinationPortParam)
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        try { destinationIP   = InetAddress.getByName(destinationIPParam); }   catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: InetAddress.getByName(destIPField): UnknownHostException: " + error.getMessage(); return status; }
        destinationPort = Integer.parseInt(destinationPortParam);
        remoteAddress = new SessionAddress(destinationIP, destinationPort, destinationIP, destinationPort + 1); // posibly incomming and outgoing remote ports

// Kill the whole thing

        rtpSessionManager.closeSession();
        rtpSessionManager.removeReceiveStreamListener(this);
        rtpSessionManager.dispose();

// Rebuild the whole thing
// Rebuild the whole thing

        rtpSessionManager = new RTPSessionMgr();
        rtpSessionManager.addReceiveStreamListener(this);
        try { rtpSessionManager.initSession(localSessionAddress, null, RTCP_BW_FRACTION, RTCP_SENDER_BW_FRACTION); } // localSessionAddress is local!!!
        catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: voiceSessionManager.initSession(localSessionAddress, null, RTCP_BW_FRACTION, RTCP_SENDER_BW_FRACTION): InvalidSessionAddressException: " + error.getMessage(); return status; }

        boolean startSessionUnsuccessfull = false;
        int retryCounter = 0; int retryLimit = 5; int retryInterval = 100;
        do
        {
            startSessionUnsuccessfull = false;
            try { rtpSessionManager.startSession(localAddress, localAddress, remoteAddress, null); } // null is encryption info
            catch (IOException error)
            {
                startSessionUnsuccessfull = true;// if (retryCounter == 3) { userInterface.logToApplication("Error: [" + retryCounter + "] updateDestination: voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again"); } try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: [" + retryCounter + "] updateDestination IOException: voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                retryCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }
            catch (InvalidSessionAddressException error)
            {
                startSessionUnsuccessfull = true;// if (retryCounter == 3) { userInterface.logToApplication("Error: [" + retryCounter + "] updateDestination: voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again"); } try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: [" + retryCounter + "] updateDestination InvalidSessionAddressException: voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                retryCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }
            retryCounter++;
        }
        while (startSessionUnsuccessfull);

//        try { voiceSessionManager.startSession(localAddress, localAddress, remoteAddress, null); } // null is encryption info
//        catch (IOException error)                       { status[0] = "1"; status[1] = "Error: updateDestination(): voiceSessionManager.startSession(" + localAddress + ", " + localAddress+ ", " + remoteAddress + ", null): IOException: " + error.getMessage(); return status; }
//        catch (InvalidSessionAddressException error)    { status[0] = "1"; status[1] = "Error: updateDestination(): voiceSessionManager.startSession(" + localAddress + ", " + localAddress+ ", " + remoteAddress + ", null): InvalidSessionAddressException: " + error.getMessage(); return status; }
        
        return status;
    }

    /**
     *
     * @param destIPField
     * @param destPortField
     * @param fileName
     * @param audioFormatParam
     * @return
     * @throws NoDataSourceException
     * @throws NoProcessorException
     * @throws UnsupportedFormatException
     */
    synchronized public String[] startStreamer(String destIPField, String destPortField, String fileName, int audioFormatParam) throws NoDataSourceException, NoProcessorException, UnsupportedFormatException
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        InetAddress destIP = null; try { destIP = InetAddress.getByName(destIPField); }
        catch (UnknownHostException error) { status[0] = "1"; status[1] = "Error: destIP = InetAddress.getByName(destIPField): UnknownHostException: " + error.getMessage(); return status; }
        Integer destPort = Integer.parseInt(destPortField);

        // Define medialocator
        MediaLocator mediaLocator = new MediaLocator(fileName);
        URL url = null; try { url = new URL(fileName); } catch (MalformedURLException ex) { status[0] = "1"; status[1] = "Error: url = new URL(fileName): MalformedURLException: " + ex.getMessage(); return status; }

// The next expression definitely needs debugging as it hangs the dependent processResponseThread

        try { iDS = javax.media.Manager.createDataSource(mediaLocator); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: iDS = Manager.createDataSource(mediaLocator): IOException: " + error.getMessage(); return status; }
        catch (NoDataSourceException error) { status[0] = "1"; status[1] = "Error: iDS = Manager.createDataSource(mediaLocator): NoDataSourceException: " + error.getMessage(); return status; }

        //mediaDuration = (long) ((int)(iDS.getDuration().getSeconds() / 1000000)); // getSeconds returns nanoseconds strange enough while we want milliseconds in a long primitive, so now you know why the funny code...
        //mediaDuration = (long) ((int)(iDS.DURATION_UNBOUNDED.getSeconds() / 1000000)); // getSeconds returns nanoseconds strange enough while we want milliseconds in a long primitive, so now you know why the funny code...
        //userInterface.log("VoiceTool: startStreamer: iDS.getDuration().getSeconds: mediaDuration set to: " + mediaDuration + "CopntentType: " + iDS.getContentType());

        //Create processor and setup processing rules
        try { processor = javax.media.Manager.createProcessor(iDS); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: processor = Manager.createProcessor(iDS): IOException: " + error.getMessage(); return status; }
        catch (NoProcessorException error) { status[0] = "1"; status[1] = "Error: processor = Manager.createProcessor(iDS): NoProcessorException: " + error.getMessage(); return status; }

        processor.configure(); // Fast usually 0 mSec
        int retryCounter = 0; int retryLimit = 3; int retryInterval = 10;
        try { Thread.sleep(retryInterval); } catch (InterruptedException error) {  }

        while ((processor != null) && ( processor.getState() != Processor.Configured ) && (retryCounter != retryLimit))
        {
//            processor.configure();
            retryCounter++;
            if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: SoundStreamer: processor.configure() Giving Up!"; return status; }
            try { Thread.sleep(retryInterval); } catch (InterruptedException error) {  }
        }
//        System.out.println("configure: mSec: " + (retryCounter * retryInterval));

        processor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));

        TrackControl track[] = processor.getTrackControls();
        switch (audioFormatParam)
        {
            case 0:  { audioFormat = new AudioFormat(audioFormat.ULAW_RTP,8000,8,1); break; }
            case 3:  { audioFormat = new AudioFormat(audioFormat.GSM_RTP,8000,8,1); break; }
            case 4:  { audioFormat = new AudioFormat(audioFormat.G723_RTP,8000,8,1); break; }
            case 8:  { audioFormat = new AudioFormat(audioFormat.ALAW,8000,8,1); break; }
        }
        track[0].setFormat(audioFormat);

        processor.realize(); // Slow, usually between 500 1000 mSec

        retryCounter = 0; retryLimit = 20; retryInterval = 100;
        try { Thread.sleep(retryInterval); } catch (InterruptedException error) {  }
        while ((processor == null) || ( processor.getState() != Processor.Realized ) && (retryCounter <= retryLimit))
        {
            if (processor == null) {processor.realize();}
            retryCounter++;
            if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: SoundStreamer: processor.realize() Giving Up!"; return status; }
            try { Thread.sleep(retryInterval); } catch (InterruptedException error) {  }
        }
//        System.out.println("realize: mSec: " + (retryCounter * retryInterval));

        mediaDuration = (long)(processor.getDuration().getSeconds()*1000); // getSeconds returns nanoseconds strange enough while we want milliseconds in a long primitive, so now you know why the funny code...
        //userInterface.log("VoiceTool: startStreamer: processor.getDuration().getSeconds: mediaDuration set to: " + mediaDuration + "CopntentType: " + iDS.getContentType());

        //Next obtain the output DataSource
        tDS = processor.getDataOutput();
        
        destAddress = new SessionAddress(destIP, destPort, destIP, destPort + 1);

        boolean startSessionUnsuccessfull = false;
        retryCounter = 0; retryLimit = 5; retryInterval = 10; // Fast, usually 0mSec
        do
        {
            startSessionUnsuccessfull = false;
            try { rtpSessionManager.startSession(localAddress, localAddress, remoteAddress, null); } // null is encryption info
            catch (IOException error)
            {
                startSessionUnsuccessfull = true;// userInterface.logToApplication("Error: startStreamer IOException voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again"); try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: startStreamer IOException voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                retryCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }
            catch (InvalidSessionAddressException error)
            {
                startSessionUnsuccessfull = true;// userInterface.logToApplication("Error: startStreamer: voiceSessionManager.startSession(..) " + error.getMessage() + " Trying again"); try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                if (retryCounter == retryLimit) { status[0] = "1"; status[1] = "Error: startStreamer InvalidSessionAddressException voiceSessionManager.startSession(..) " + error.getMessage() + " Giving Up!!!"; return status; }
                retryCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }

        } while (startSessionUnsuccessfull);
//        System.out.println("startSession: mSec: " + (retryCounter * retryInterval));
//        try { voiceSessionManager.startSession(localAddress, localAddress, destAddress, null); }
//        catch (IOException error) { status[0] = "1"; status[1] = "Error: voiceSessionManager.startSession(localAddress, localAddress, destAddress, null): IOException: " + error.getMessage(); return status; }
//        catch (InvalidSessionAddressException error) { status[0] = "1"; status[1] = "Error: voiceSessionManager.startSession(localAddress, localAddress, destAddress, null): InvalidSessionAddressException: " + error.getMessage(); return status; }

        //Next we obtain a sendstream from the DataSources obtained as output of the processor
        try { sendStream = rtpSessionManager.createSendStream(tDS, 0); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: sendStream = voiceSessionManager.createSendStream(tDS, 0): " + error.getMessage(); return status; }
        catch (UnsupportedFormatException error) { status[0] = "1"; status[1] = "Error: sendStream = voiceSessionManager.createSendStream(tDS, 0): UnsupportedFormatException: " + error.getMessage(); return status; }

        //Start capture and transmission
        try { sendStream.start(); processor.start(); }
        catch (IOException error) { status[0] = "1"; status[1] = "Error: sendStream.start(); processor.start();: IOException: " + error.getMessage(); return status; } // Starts send the stream

        return status;
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("static-access")
    public String[] stopMedia()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        //Check whether we are the sending side
        //processor.getState();
        //First stop the player
        if ( processor != null)
        {
            switch (processor.getState())
            {
                case Processor.Unrealized:  { status[1] = "Processor Unrealized..."; processor.close(); break; }
                case Processor.Realizing:   { status[1] = "Processor Realizing..."; processor.close(); break; }
                case Processor.Realized:    { status[1] = "Processor Realized..."; processor.close(); break; }
                case Processor.Configuring: { status[1] = "Processor Configuring..."; processor.close(); break; }
                case Processor.Configured:  { status[1] = "Processor Configured..."; processor.close(); break; }
                case Processor.Prefetching: { status[1] = "Processor Prefetching..."; processor.close(); break; }
                case Processor.Prefetched:  { status[1] = "Processor Prefetched..."; processor.close(); break; }
                case Processor.Started:
                {
                    status[1] = "Processor Started...";
                    processor.stop(); processor.close();
                    try  { sendStream.stop(); sendStream.close(); }
                    catch (IOException error) { status[0] = "1"; status[1] = "Error: sendStream.stop(); sendStream.close();: IOException: " + error.getMessage(); return status; }
                    break;
                }
                default: { status[1] = "Processor Unknown state: " + processor.getState(); }
            }
        }
        
        if ( player != null)
        {
            switch (player.getState())
            {
                case Player.Unrealized:  { status[1] = "Player Unrealized..."; player.close(); break; }
                case Player.Realizing:   { status[1] = "Player Realizing..."; player.close(); break; }
                case Player.Realized:    { status[1] = "Player Realized..."; player.close(); break; }
                case Player.Prefetching: { status[1] = "Player Prefetching..."; player.close(); break; }
                case Player.Prefetched:  { status[1] = "Player Prefetched..."; player.close(); break; }
                case Player.Started:     { player.stop(); player.close(); break; }
                default: { status[1] = "Player Unknown state: " + player.getState() ; }
            }
        }
        return status;
    }

    /**
     *
     * @param event
     */
    @SuppressWarnings("static-access")
    @Override
    synchronized public void update(ReceiveStreamEvent event) // Inherrited
    {
        if (event instanceof NewReceiveStreamEvent)
        {
            receiveStream = event.getReceiveStream();
            rDS = receiveStream.getDataSource();
            try { player = javax.media.Manager.createPlayer(rDS); }
            catch (IOException ex) { userInterface.showStatus("Error: IOException: SoundStreamer.update(event): player = javax.media.Manager.createPlayer(rDS) " + ex.getMessage(), true, true); }
            catch (NoPlayerException ex) { userInterface.showStatus("Error: NoPlayerException: SoundStreamer.update(event): player = javax.media.Manager.createPlayer(rDS) " + ex.getMessage(), true, true); }

            player.start();
        }
        else
        {
//            if (player.getState()!= Player.Started) {player.start();}
            if (player != null) {player.start();}
        }
    }

    /**
     *
     * @return
     */
    public boolean isListening()
    {
        return isListening;
    }

    /**
     *
     * @return
     */
    @SuppressWarnings({"static-access", "empty-statement"})
    synchronized public int findSoundPorts() // Find an unused even (non odd) port
    {
//        System.out.println("method findFreeEvenPort Invoked by SoftPhone: " + superId);
        Integer randomEvenPort = 0, oddPort = 0;
        DatagramSocket evenUDPSocket = null, oddUDPSocket = null;
        boolean evenUDPSocketNotInstantiated = false;
        boolean oddUDPSocketNotInstantiated = false;

        int retryInterval = 100;
        int oddLoopCounter = 0; int outerRetryLimit = 10;
        do
        {
//            System.out.println("do level: 1");
            evenUDPSocketNotInstantiated = false;
            oddUDPSocketNotInstantiated = false;
            int evenLoopCounter = 0;
            int evenRetryLimit = 10;
            do
            {
                randomEvenPort = (int)(Math.random()* (AUTO_SOURCE_PORT_RANGE_END - AUTO_SOURCE_PORT_RANGE_START) + AUTO_SOURCE_PORT_RANGE_START );
                evenUDPSocketNotInstantiated = false;
                
                if ( Integer.lowestOneBit(randomEvenPort) == 1 ) { randomEvenPort++; }
                try { evenUDPSocket = new DatagramSocket(randomEvenPort); }
                catch (IOException error)
                {
                    evenUDPSocketNotInstantiated = true;
                    if (evenLoopCounter == evenRetryLimit) { userInterface.logToApplication("Error: [" + evenLoopCounter + "] SoundStreamer.findSoundPorts(): evenUDPSocket = new DatagramSocket(" + randomEvenPort + "): " + error.getMessage()); }
                    if ((evenUDPSocket != null) && (evenUDPSocket.isBound())) { evenUDPSocket.close(); }
                    try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
                    evenLoopCounter++;
                }
//                System.out.println("do level: 2 on (Even) port: " + randomEvenPort + " evenUDPSocketNotInstantiated = " + evenUDPSocketNotInstantiated);

            } while((evenLoopCounter <= evenRetryLimit) && ((evenUDPSocketNotInstantiated)||(evenUDPSocket == null)));

            int oddRetryLimit = 10;
            try { oddUDPSocket = new DatagramSocket(randomEvenPort + 1); }
            catch (IOException error)
            {
                oddUDPSocketNotInstantiated = true;
                if (oddLoopCounter == oddRetryLimit) { userInterface.logToApplication("Error: [" + oddLoopCounter + "] SoundStreamer.findSoundPorts(): oddUDPSocket = new DatagramSocket(" + randomEvenPort + 1 + "): " + error.getMessage()); }
                if ((oddUDPSocket != null) && (oddUDPSocket.isBound())) { oddUDPSocket.close(); }
                oddLoopCounter++;
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) {};
            }
//            System.out.println("do level: 2 on (Odd) port: " + (randomEvenPort + 1) + " oddUDPSocketNotInstantiated = " + oddUDPSocketNotInstantiated);

        } while((oddLoopCounter <= outerRetryLimit) && (evenUDPSocketNotInstantiated) && (oddUDPSocketNotInstantiated) || (oddUDPSocket == null));

        evenUDPSocket.close(); evenUDPSocket = null;
        oddUDPSocket.close(); oddUDPSocket = null;
        return randomEvenPort;
    }
//
//    @Override
//    public void controllerUpdate(ControllerEvent ce) {
//        System.out.println("ControllerUpdate: state: " + ce.getSourceController().getState());
//    }
}