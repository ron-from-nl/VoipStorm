import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

// This Object is integrated as sort of a telnet server receiving and executing commands

/**
 *
 * @author ron
 */

public class NetManagerServer {

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket serverSocket;
    private Socket socket;
    private int serverPort;
    private boolean serverStopRequested = false;
    private int connections = 1; // 0 = unlimited
    private boolean runThreadsAsDaemons = true;
    private ECallCenter21 eCallCenter21;

    /**
     *
     * @param netManagerViewParam
     * @param portParam
     */
    public NetManagerServer(ECallCenter21 netManagerViewParam, int portParam)
    {
        eCallCenter21 = netManagerViewParam;
        serverPort = portParam;

        Thread netManagerServerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                eCallCenter21.showStatus("Management Server Starting",true, true);
                // Instantiate Server(Socket)
                boolean serverStartUnsuccessful = false;
                int retryCounter = 0; int retryLimit = 100; int retryInterval = 100;

                try { serverSocket = new ServerSocket(serverPort, connections); }
                catch (IOException ex)
                {
                    eCallCenter21.showStatus("Error: IOException: NetManagerServer: new ServerSocket(serverPort, connections)" + ex.getMessage(),true, true);
                    serverStartUnsuccessful = true;
                    try { Thread.sleep(retryInterval); } catch (InterruptedException ex2) { }
                }

//                try { serverSocket.setReuseAddress(true); } catch (SocketException ex) { eCallCenter21.showStatus("Error: SocketException: NetManagerServer: serverSocket.setReuseAddress(true)" + ex.getMessage(),true, true); }
                serverStopRequested = false;

                // Run ServerListener
                while (! serverStopRequested)
                {
                    // Wait until connection is made
//                    eCallCenter21.showStatus("Server Connection Accepting...",false);
                    try { socket = serverSocket.accept(); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer: serverSocket.accept() " + ex.getMessage(),true, true); }

                    // Block the serverSocket after established connection
//                    blockServer(true);

                    // Create outputstream and attach to socket
                    try { outputStream = new ObjectOutputStream(socket.getOutputStream()); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer: new ObjectOutputStream(socket.getOutputStream())" + ex.getMessage(), true, true); closeConnection(); stopServer(); return;}

                    // Empty buffer in stream
                    try { outputStream.flush(); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer: outputStream.flush()" + ex.getMessage(), true, true); }

                    // Create inputstream and attach to socket
                    try { inputStream = new ObjectInputStream(socket.getInputStream()); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer: new ObjectInputStream(socket.getInputStream()" + ex.getMessage(), true, true); closeConnection(); stopServer(); return;}

                    // Detach peer connection voor processing client connections
                    String message = new String("");
                    try { message = (String) inputStream.readObject(); }
                    catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer: inputStream.readObject()" + ex.getMessage(), true, true); closeConnection(); }
                    catch (ClassNotFoundException ex) { eCallCenter21.showStatus("Error: ClassNotFoundException: NetManagerServer: inputStream.readObject()" + ex.getMessage(), true, true); closeConnection(); }

//                    if      (message.equalsIgnoreCase("getStatus1"))    { respond("getStatus1 requested"); closeConnection();}
//                    else if (message.equalsIgnoreCase("getStatus2"))    { respond("getStatus2 requested"); closeConnection();}
//                    else                                                { respond("Command Invalid"); closeConnection();}

                    if      (message.equalsIgnoreCase("getCallCenterStatus"))               { respond(Integer.toString(eCallCenter21.getCallCenterStatus())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getCallCenterStatusDescription"))    { respond(eCallCenter21.getCallCenterStatusDescription()); closeConnection(); }
                    else if (message.equalsIgnoreCase("getBoundMode"))                      { respond(eCallCenter21.getBoundMode()); closeConnection(); }
                    else if (message.equalsIgnoreCase("getCampaignReRunStage"))             { respond(Integer.toString(eCallCenter21.getCampaignReRunStage())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getCampaignProgressPercentage"))     { respond(Integer.toString(eCallCenter21.getCampaignProgressPercentage())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getSoftphonesQuantity"))             { respond(Integer.toString(eCallCenter21.getSoftphonesQuantity())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getIdleAC"))                         { respond(Integer.toString(eCallCenter21.getIdleAC())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getConnectingAC"))                   { respond(Integer.toString(eCallCenter21.getConnectingAC())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getConnectingTT"))                   { respond(Integer.toString(eCallCenter21.getConnectingTT())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getCallingAC"))                      { respond(Integer.toString(eCallCenter21.getCallingAC())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getCallingTT"))                      { respond(Integer.toString(eCallCenter21.getCallingTT())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getTalkingAC"))                      { respond(Integer.toString(eCallCenter21.getTalkingAC())); closeConnection(); }
                    else if (message.equalsIgnoreCase("getTalkingTT"))                      { respond(Integer.toString(eCallCenter21.getTalkingTT())); closeConnection(); }
                    else if (message.equalsIgnoreCase("runCampaign"))
                    {
                        if (eCallCenter21.campaign != null)
                        {
                            eCallCenter21.runCampaignToggleButton.setSelected(true);
                            eCallCenter21.runCampaign(eCallCenter21.campaign.getId()); respond(new String("OK"));
                        }
                        closeConnection();
                    }
                    else if (message.equalsIgnoreCase("pauseCampaign"))
                    {
                        if (eCallCenter21.campaign != null)
                        {
                            eCallCenter21.runCampaignToggleButton.setSelected(false);
                            respond(new String("OK"));
                        }
                        closeConnection();
                    }
                    else if (message.equalsIgnoreCase("continueCampaign"))
                    {
                        if (eCallCenter21.campaign != null)
                        {
                            eCallCenter21.runCampaignToggleButton.setSelected(true);
                            respond(new String("OK"));
                        }
                        closeConnection();
                    }
                    else if (message.equalsIgnoreCase("stopCampaign"))
                    {
                        if (eCallCenter21.campaign != null)
                        {
                            eCallCenter21.stopCampaign();
                            if (!eCallCenter21.runCampaignToggleButton.isSelected()) {eCallCenter21.runCampaignToggleButton.setSelected(true);}
                            respond(new String("OK"));
                        }
                        closeConnection();
                    }
                    else if (message.equalsIgnoreCase("closeCallCenter"))                   { eCallCenter21.closeCallCenter(); closeConnection(); }
                    else if (message.equalsIgnoreCase("setPowerOn"))                        { eCallCenter21.setPowerOn(true); closeConnection(); }
                    else if (message.equalsIgnoreCase("getPID"))                            { respond(eCallCenter21.getPID()); closeConnection(); }
                    else if (message.equalsIgnoreCase("setPowerOff"))                       { eCallCenter21.setPowerOn(false); closeConnection(); }
                    else if (message.equalsIgnoreCase("register"))                          { eCallCenter21.register(); closeConnection(); }
                    else if (message.equalsIgnoreCase("unregister"))                        { eCallCenter21.unRegister(); closeConnection(); }
                    else if (message.equalsIgnoreCase("version"))                           { respond(eCallCenter21.getVersion()); closeConnection(); }
                    else if (message.equalsIgnoreCase("isStalling"))                        { respond(eCallCenter21.isStalling()); closeConnection(); }
                    else                                                                    { respond("Command Invalid"); closeConnection(); }
//                    blockServer(false);
                }
                stopServer();
            }
        });
        netManagerServerThread.setName("netManagerServerThread");
        netManagerServerThread.setDaemon(false);
        netManagerServerThread.setPriority(7);
        netManagerServerThread.start();

    }

    /**
     *
     * @return
     */
    public boolean serverIsListening() { return serverSocket.isBound(); }

    /**
     *
     * @param messageParam
     */
    public void respond(String messageParam)
    {
        try { outputStream.writeObject(messageParam); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.respond(): writeObject(messageParam)" + ex.getMessage(), true, true); }
        try { outputStream.flush(); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: outputStream.flush()" + ex.getMessage(), true, true); }
        eCallCenter21.selfDestructCounter = eCallCenter21.getSelfDestructCounterLimit();
        eCallCenter21.blinkNetManagerToggleButton();
        //eCallCenter21.showStatus("Server Sent: " + messageParam, false);
    }

    /**
     *
     */
    public void closeConnection()
    {
//        if (outputStream != null) { try { outputStream.close(); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.closeConnection: outputStream.close()" + ex.getMessage(), true, true); } }
//        if (inputStream != null) { try { inputStream.close();  } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.closeConnection: inputStream.close()" + ex.getMessage(), true, true); } }
        if (socket != null)
        {
            try { socket.shutdownInput(); }     catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.closeConnection: socket.shutdownInput()" + ex.getMessage(), true, true); }
            try { socket.shutdownOutput(); }    catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.closeConnection: socket.shutdownOutput()" + ex.getMessage(), true, true); }
            try { socket.close(); }             catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.closeConnection: socket.close()" + ex.getMessage(), true, true); }
        }
        //eCallCenter21.showStatus("Management Server Connection Closed", true, true);
    }

    /**
     *
     * @param enableParam
     */
    public void blockServer(boolean enableParam)
    {
        if (enableParam) // block
        {
            try { serverSocket.setSoTimeout(0); } catch (SocketException ex) { eCallCenter21.showStatus("Error: SocketException: NetManagerServer: serverSocket.setSoTimeout(0) " + ex.getMessage(),true, true); }        
        }
        else // unblock
        {            
            try { serverSocket.setSoTimeout(1); } catch (SocketException ex) { eCallCenter21.showStatus("Error: SocketException: NetManagerServer: serverSocket.setSoTimeout(0) " + ex.getMessage(),true, true); }        
        }
    }

    /**
     *
     */
    public void stopServer()
    {
        serverStopRequested = true;
        closeConnection();
        if (serverSocket != null) { try { serverSocket.close(); } catch (IOException ex) { eCallCenter21.showStatus("Error: IOException: NetManagerServer.stopServer: serverSocket.close()" + ex.getMessage(), true, true); } }
        eCallCenter21.showStatus("Management Server Closed", false, false);
    }
}
