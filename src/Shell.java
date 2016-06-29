import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ron
 */
public class Shell {

    private String              platform;
    private String[]            status = new String[2];
    private Process             process;
    private String[]            PID;
    private String[]            KILLPID;
//    private String[]            CPUIDLE;
//    private String[]            MEMFREE;
    private String[]            NETBPSUPCMD;
    private String[]            NETBPSDOWNCMD;
    private String[]            SID;
    private String[]            STARTJCONSOLE;
    private String[]            STARTEPHONEGUI;
    private String[]            STARTVOIPSTORMUPDATER;
    private String[]            STARTCALLCENTERMANAGER;
    private String[]            STARTMANAGEDCALLCENTERLEFT;
    private String[]            STARTMANAGEDCALLCENTERRIGHT;
    private String[]            STARTUNMANAGEDCALLCENTERRIGHT;
    private String[]            STARTUNMANAGEDCALLCENTERLEFT;

    private String[]            STARTMANAGEDCALLCENTERINBOUND;
    private String[]            STARTMANAGEDCALLCENTEROUTBOUND;
    private String              STARTMANAGEDCALLCENTEROUTBOUND2;
    private String              STARTMANAGEDCALLCENTEROUTBOUND3;

    private String[]            STARTUNMANAGEDCALLCENTERINBOUND;
    private String[]            STARTUNMANAGEDCALLCENTEROUTBOUND;
    private String              STARTUNMANAGEDCALLCENTEROUTBOUND2;
    private String              STARTUNMANAGEDCALLCENTEROUTBOUND3;

    private String              commandOutput = new String();
    private boolean             runThreadsAsDaemons = true;
    private int                 heapMemMax = 256;
    private String              javaOptions = "";

    /**
     *
     */
    public Shell()
    {
	platform = System.getProperty("os.name").toLowerCase();
        setENV();
    }

    /**
     *
     * @return
     */
    public String[] getPID()
    {
        status[0] = "0"; status[1] = "";
        try { process = Runtime.getRuntime().exec(PID); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        try { while ((commandOutput = stdInput.readLine()) != null) {status[1] += commandOutput;}}
        catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
        return status;
    }

    /**
     *
     * @param pidParam
     * @return
     */
    public String[] killPID(int pidParam)
    {
	status[0] = "0"; status[1] = ""; KILLPID[2] += Integer.toString(pidParam) + ";";
	try { process = Runtime.getRuntime().exec(KILLPID); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	try { while ((commandOutput = stdInput.readLine()) != null) {status[1] += commandOutput;}}
	catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
//	try { Thread.sleep(1000); } catch (InterruptedException ex) {} process.destroy();
	return status;
    }

    /**
     *
     */
    public void startJConsole()
    {
        try { Runtime.getRuntime().exec(STARTJCONSOLE); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param javaOptionsParam
     */
    public void startEPhone(String javaOptionsParam)
    {
        javaOptions = javaOptionsParam;
        try { Runtime.getRuntime().exec(STARTEPHONEGUI); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     */
    public void startVoipStormUpdater()
    {
        try { Runtime.getRuntime().exec(STARTVOIPSTORMUPDATER); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param javaOptionsParam
     */
    public void startCallCenterManager(String javaOptionsParam)
    {
        javaOptions = javaOptionsParam;
        try { Runtime.getRuntime().exec(STARTCALLCENTERMANAGER); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param heapMemMaxParam
     * @param javaOptionsParam
     */
    public void startManagedCallCenterLeft(int heapMemMaxParam, String javaOptionsParam)
    {
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
        try { Runtime.getRuntime().exec(STARTMANAGEDCALLCENTERLEFT); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param heapMemMaxParam
     * @param javaOptionsParam
     */
    public void startManagedCallCenterRight(int heapMemMaxParam, String javaOptionsParam)
    {
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
        try { Runtime.getRuntime().exec(STARTMANAGEDCALLCENTERRIGHT); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param heapMemMaxParam
     * @param javaOptionsParam
     */
    public void startUnManagedCallCenterLeft(int heapMemMaxParam, String javaOptionsParam)
    {
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
        try { Runtime.getRuntime().exec(STARTUNMANAGEDCALLCENTERLEFT); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param heapMemMaxParam
     * @param javaOptionsParam
     */
    public void startUnManagedCallCenterRight(int heapMemMaxParam, String javaOptionsParam)
    {
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
        try { Runtime.getRuntime().exec(STARTUNMANAGEDCALLCENTERRIGHT); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
    }

    /**
     *
     * @param heapMemMaxParam
     * @param javaOptionsParam
     * @return
     */
    public String[] startManagedCallCenterInbound(int heapMemMaxParam, String javaOptionsParam)
    {
	status[0] = "0"; status[1] = "";
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
	try { Runtime.getRuntime().exec(STARTMANAGEDCALLCENTERINBOUND); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
	return status;
    }

    /**
     *
     * @param heapMemMaxParam
     * @param javaOptionsParam
     * @return
     */
    public String[] startUnManagedCallCenterInbound(int heapMemMaxParam, String javaOptionsParam)
    {
	status[0] = "0"; status[1] = "";
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
	try { Runtime.getRuntime().exec(STARTUNMANAGEDCALLCENTERINBOUND); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
	return status;
    }

    /**
     *
     * @param campaignIdParam
     * @param heapMemMaxParam
     * @param javaOptionsParam
     * @return
     */
    public String[] startManagedCallCenterOutbound(final int campaignIdParam, int heapMemMaxParam, String javaOptionsParam)
    {
	status[0] = "0"; status[1] = "";
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
        STARTMANAGEDCALLCENTEROUTBOUND3 = Integer.toString(campaignIdParam); STARTMANAGEDCALLCENTEROUTBOUND[2] = STARTMANAGEDCALLCENTEROUTBOUND2 + STARTMANAGEDCALLCENTEROUTBOUND3;
	try { Runtime.getRuntime().exec(STARTMANAGEDCALLCENTEROUTBOUND); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
	return status;
    }

    /**
     *
     * @param campaignIdParam
     * @param heapMemMaxParam
     * @param javaOptionsParam
     * @return
     */
    public String[] startUnManagedCallCenterOutbound(final int campaignIdParam, int heapMemMaxParam, String javaOptionsParam)
    {
	status[0] = "0"; status[1] = "";
        heapMemMax = heapMemMaxParam;
        javaOptions = javaOptionsParam;
        setENV();
        STARTUNMANAGEDCALLCENTEROUTBOUND3 = Integer.toString(campaignIdParam); STARTUNMANAGEDCALLCENTEROUTBOUND[2] = STARTUNMANAGEDCALLCENTEROUTBOUND2 + STARTUNMANAGEDCALLCENTEROUTBOUND3;
	try { Runtime.getRuntime().exec(STARTUNMANAGEDCALLCENTEROUTBOUND); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
	return status;
    }

//    public String[] getCPUIDLE()
//    {
//	status[0] = "0"; status[1] = "";
//	try { process = Runtime.getRuntime().exec(CPUIDLE); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
//	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
//	BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//	try { while ((commandOutput = stdInput.readLine()) != null) {status[1] += commandOutput;}}
//	catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
////	try { Thread.sleep(1000); } catch (InterruptedException ex) {} process.destroy();
//	return status;
//    }

//    public String[] getMEMFREE()
//    {
//	status[0] = "0"; status[1] = "";
//	try { process = Runtime.getRuntime().exec(MEMFREE); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
//
//	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
//	BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//	try { while ((commandOutput = stdInput.readLine()) != null) {status[1] += commandOutput;}}
//	catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
////	try { Thread.sleep(1000); } catch (InterruptedException ex) {} process.destroy();
//	return status;
//    }

    /**
     *
     * @return
     */
    
    public String[] getBPSUp()
    {
	status[0] = "0"; status[1] = "";
	try { process = Runtime.getRuntime().exec(NETBPSUPCMD); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }

	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	try { while ((commandOutput = stdInput.readLine()) != null) {status[1] += commandOutput;}}
	catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
//	try { Thread.sleep(1000); } catch (InterruptedException ex) {} process.destroy();
	return status;
    }

    /**
     *
     * @return
     */
    public String[] getBPSDown()
    {
	status[0] = "0"; status[1] = "";
	try { process = Runtime.getRuntime().exec(NETBPSDOWNCMD); } catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }

	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	try { while ((commandOutput = stdInput.readLine()) != null) {status[1] += commandOutput;}}
	catch (IOException ex) { status[0] = "1"; status[1] = "RunTime Error: " + ex.getMessage(); }
//	try { Thread.sleep(1000); } catch (InterruptedException ex) {} process.destroy();
	return status;
    }

    /**
     *
     * @return
     */
    public String getPlatform()
    {
	return platform;
    }

    private void setENV()
    {
        if	    (( platform.indexOf("mac os x") != -1 ) || ( platform.indexOf("bsd") != -1 ))
        {
            PID                                 = new String[]{ "/bin/sh", "-c", "ps -ef | grep java | grep -v 'grep java' | tail -1 | while read a b c; do echo $b; done; exit"};
            KILLPID                             = new String[]{ "/bin/sh", "-c", "kill "};
            STARTJCONSOLE                       = new String[]{ "/bin/sh", "-c", "jconsole"};
            STARTEPHONEGUI                      = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -cp VoipStorm.jar EPhone"};
            STARTVOIPSTORMUPDATER               = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar data/bin/VoipStormUpdater.jar"};
            STARTCALLCENTERMANAGER              = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar VoipStorm.jar"};
            STARTMANAGEDCALLCENTERLEFT          = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed"};
            STARTMANAGEDCALLCENTERRIGHT          = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined Managed"};
            STARTUNMANAGEDCALLCENTERLEFT        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged"};
            STARTUNMANAGEDCALLCENTERRIGHT       = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined UnManaged"};

            STARTMANAGEDCALLCENTERINBOUND       = new String[]{ "/bin/sh", "-c", "\"java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound Managed\""};
            STARTMANAGEDCALLCENTEROUTBOUND      = new String[]{ "/bin/sh", "-c", ""};
            STARTMANAGEDCALLCENTEROUTBOUND2     = new String("java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed ");
            STARTMANAGEDCALLCENTEROUTBOUND3     = new String("CampaignId");

            STARTUNMANAGEDCALLCENTERINBOUND     = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound UnManaged"};
            STARTUNMANAGEDCALLCENTEROUTBOUND    = new String[]{ "/bin/sh", "-c", ""};
            STARTUNMANAGEDCALLCENTEROUTBOUND2   = new String("java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged ");
            STARTUNMANAGEDCALLCENTEROUTBOUND3   = new String("CampaignId");

//            CPUIDLE                             = new String[]{ "/bin/sh", "-c", "sar 1 1 | tail -1 | while read a b c d e; do echo $e; done; exit" };
//            MEMFREE                             = new String[]{ "/bin/sh", "-c", "sysctl hw.usermem | while read a b; do echo $b; done; exit" };
            NETBPSDOWNCMD                       = new String[]{ "/bin/sh", "-c", "sar -n PPP 1 1 | grep en1 | head -1 | while read a b c d e f; do echo $d; done; exit"};
            SID                                 = new String[]{ "/bin/sh", "-c", "system_profiler -detailLevel basic SPHardwareDataType | grep 'Serial Number (system)' | while read a b c d; do echo $d; done; exit"};

        }
        else if ( platform.indexOf("linux") != -1 )
        {
            PID                                 = new String[]{ "/bin/sh", "-c", "ps -ef | grep java | grep -v 'grep java' | tail -1 | while read a b c; do echo $b; done; exit"};
            KILLPID                             = new String[]{ "/bin/sh", "-c", "kill "};
            STARTJCONSOLE                       = new String[]{ "/bin/sh", "-c", "jconsole"};
            STARTEPHONEGUI                      = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -cp VoipStorm.jar EPhone"};
            STARTVOIPSTORMUPDATER               = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar data/bin/VoipStormUpdater.jar"};
            STARTCALLCENTERMANAGER              = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar VoipStorm.jar"};
            STARTMANAGEDCALLCENTERLEFT          = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed"};
            STARTMANAGEDCALLCENTERRIGHT          = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined Managed"};
            STARTUNMANAGEDCALLCENTERLEFT        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged"};
            STARTUNMANAGEDCALLCENTERRIGHT       = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined UnManaged"};

            STARTMANAGEDCALLCENTERINBOUND       = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound Managed"};
            STARTMANAGEDCALLCENTEROUTBOUND      = new String[]{ "/bin/sh", "-c", ""};
            STARTMANAGEDCALLCENTEROUTBOUND2     = new String("java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed ");
            STARTMANAGEDCALLCENTEROUTBOUND3     = new String("CampaignId");

            STARTUNMANAGEDCALLCENTERINBOUND     = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound UnManaged"};
            STARTUNMANAGEDCALLCENTEROUTBOUND    = new String[]{ "/bin/sh", "-c", ""};
            STARTUNMANAGEDCALLCENTEROUTBOUND2   = new String("java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged ");
            STARTUNMANAGEDCALLCENTEROUTBOUND3   = new String("CampaignId");

//            CPUIDLE                           = new String[]{ "/bin/bash", "-c", "mpstat 1 1 | tail -1 | tr ',' '.' | while read a b c d e f g h i j k; do echo $j;done; exit" }; // Requires Package sysstat
//            CPUIDLE                             = new String[]{ "/bin/bash", "-c", "sar 1 1 | tail -1 | tr ',' '.' | while read a b c d e f g h; do echo $h; done; exit" }; // Requires Package sysstat
//            MEMFREE                             = new String[]{ "/bin/bash", "-c", "free | grep buffers | tail -1 | while read a b c d; do echo ${d}000; done; exit" };
            NETBPSDOWNCMD                       = new String[]{ "/bin/bash", "-c", "sar -n DEV 1 1 | grep eth1 | tail -2 | while read a b c d e f g h i; do echo $d; done; exit"};
            SID                                 = new String[]{ "/bin/sh", "-c", "cat /etc/smolt/hw-uuid; exit"};
        }
        else if (( platform.indexOf("sunos") != -1 ) || ( platform.indexOf("hpux") != -1 ) || ( platform.indexOf("aix") != -1 ))
        {
            PID                                 = new String[]{ "/bin/sh", "-c", "ps -ef | grep java " + javaOptions + " | grep -v 'grep java' | tail -1 | while read a b c; do echo $b; done; exit"};
            KILLPID                             = new String[]{ "/bin/sh", "-c", "kill "};
            STARTJCONSOLE                       = new String[]{ "/bin/sh", "-c", "jconsole"};
            STARTEPHONEGUI                      = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -cp VoipStorm.jar EPhone"};
            STARTVOIPSTORMUPDATER               = new String[]{ "/bin/sh", "-c", "java -jar data/bin/VoipStormUpdater.jar"};
            STARTCALLCENTERMANAGER              = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar VoipStorm.jar"};
            STARTMANAGEDCALLCENTERLEFT          = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed"};
            STARTMANAGEDCALLCENTERRIGHT          = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined Managed"};
            STARTUNMANAGEDCALLCENTERLEFT        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged"};
            STARTUNMANAGEDCALLCENTERRIGHT       = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined UnManaged"};

            STARTMANAGEDCALLCENTERINBOUND       = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound Managed"};
            STARTMANAGEDCALLCENTEROUTBOUND      = new String[]{ "/bin/sh", "-c", ""};
            STARTMANAGEDCALLCENTEROUTBOUND2     = new String("java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed ");
            STARTMANAGEDCALLCENTEROUTBOUND3     = new String("CampaignId");

            STARTUNMANAGEDCALLCENTERINBOUND     = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound UnManaged"};
            STARTUNMANAGEDCALLCENTEROUTBOUND    = new String[]{ "/bin/sh", "-c", ""};
            STARTUNMANAGEDCALLCENTEROUTBOUND2   = new String("java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged ");
            STARTUNMANAGEDCALLCENTEROUTBOUND3   = new String("CampaignId");

//            CPUIDLE                           = new String[]{ "/bin/bash", "-c", "mpstat 1 1 | tail -1 | tr ',' '.' | while read a b c d e f g h i j k; do echo $j;done; exit" }; // Requires Package sysstat
//            CPUIDLE                             = new String[]{ "/bin/bash", "-c", "sar 1 1 | tail -1 | tr ',' '.' | while read a b c d e f g h; do echo $h; done; exit" }; // Requires Package sysstat
//            MEMFREE                             = new String[]{ "/bin/bash", "-c", "free | grep buffers | tail -1 | while read a b c d; do echo ${d}000; done; exit" };
            NETBPSDOWNCMD                       = new String[]{ "/bin/bash", "-c", "sar -n DEV 1 1 | grep eth1 | tail -2 | while read a b c d e f g h i; do echo $d; done; exit"};
            SID                                 = new String[]{ "/bin/sh", "-c", "cat /etc/smolt/hw-uuid; exit"};
        }
        else if ( platform.indexOf("windows") != -1 )
        {
            PID                                 = new String[]{ "cmd", "/C", "ps -ef | grep java | grep -v 'grep java' | tail -1 | while read a b c; do echo $b; done; exit"};
            KILLPID                             = new String[]{ "cmd", "/C", "kill "};
            STARTJCONSOLE                       = new String[]{ "cmd", "/C", "jconsole"};
            STARTEPHONEGUI                      = new String[]{ "cmd", "/C", "java " + javaOptions + " -cp VoipStorm.jar EPhone"};
            STARTVOIPSTORMUPDATER               = new String[]{ "cmd", "/C", "java " + javaOptions + " -jar data\\bin\\VoipStormUpdater.jar"};
            STARTCALLCENTERMANAGER              = new String[]{ "cmd", "/C", "java " + javaOptions + " -jar VoipStorm.jar"};
            STARTMANAGEDCALLCENTERLEFT          = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed"};
            STARTMANAGEDCALLCENTERRIGHT          = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined Managed"};
            STARTUNMANAGEDCALLCENTERLEFT        = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged"};
            STARTUNMANAGEDCALLCENTERRIGHT       = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Undefined UnManaged"};

            STARTMANAGEDCALLCENTERINBOUND       = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound Managed"};
            STARTMANAGEDCALLCENTEROUTBOUND      = new String[]{ "cmd", "/C", ""};
            STARTMANAGEDCALLCENTEROUTBOUND2     = "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound Managed ";
            STARTMANAGEDCALLCENTEROUTBOUND3     = "CampaignId";

            STARTUNMANAGEDCALLCENTERINBOUND     = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Inbound UnManaged"};
            STARTUNMANAGEDCALLCENTEROUTBOUND    = new String[]{ "cmd", "/C", ""};
            STARTUNMANAGEDCALLCENTEROUTBOUND2   = "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp VoipStorm.jar ECallCenter21 Outbound UnManaged ";
            STARTUNMANAGEDCALLCENTEROUTBOUND3   = "CampaignId";

//            CPUIDLE                           = new String[]{ "/bin/bash", "-c", "mpstat 1 1 | tail -1 | tr ',' '.' | while read a b c d e f g h i j k; do echo $j;done; exit" }; // Requires Package sysstat
//            CPUIDLE                             = new String[]{ "cmd", "/C", "data\\bin\\Process.exe -t | find \"Idle\" | data\\bin\\gawk.exe \"{ print $5 }\"" }; // Requires Package sysstat
//            CPUIDLE                             = new String[]{ "cmd", "/C", "echo 50" }; // Requires Package sysstat
//            MEMFREE                             = new String[]{ "cmd", "/C", "echo 1024000000" };
            NETBPSDOWNCMD                       = new String[]{ "cmd", "/C", "sar -n DEV 1 1 | grep eth1 | tail -2 | while read a b c d e f g h i; do echo $d; done"};
            SID                                 = new String[]{ "cmd", "/C", "ipconfig; exit"};
        }
        else                                            { System.out.println("Shell commands not implemented for " + platform); }
    }
}
