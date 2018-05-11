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

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author ron
 */
public class VersionChecker
{
    private String              UPDATEURL                   = "http://www.voipstorm.nl/VoipStorm.jar"; // Impossible!!!!!!!
    private String              newJarFileString;
    private String              curJarFileString;
    private String              oldJarFileString;
    private boolean             versionCheckerFailed        = false;
    private boolean             detectedANewVersion         = false;
    private VersionUpdateDialog        updateDialog;
    private boolean             newVersionInstalled         = false;
    private Manager  eCallCenterManager;
    private boolean             runThreadsAsDaemons         = true;
    private Shell               shell;
    private String              responseCodeDescription;
    private int                 httpConTimeout              = 1000; // mS
    private String              dataDir;
    private String              binDir;
    private String              fileSeparator;
    private String              platform;

    /**
     *
     * @param eCallCenterManagerParam
     * @throws MalformedURLException
     * @throws IOException
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public VersionChecker(Manager eCallCenterManagerParam) throws java.net.MalformedURLException, java.io.IOException
    {
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; } else { fileSeparator = "/"; }
        dataDir = "data" + fileSeparator; binDir = dataDir + "bin" + fileSeparator;

//        remoteJarURLString  = new String("http://www.voipstorm.nl/VoipStorm.jr");
        newJarFileString    = new String(binDir + "VoipStorm-New.jar");
        curJarFileString    = new String("VoipStorm.jar");
        oldJarFileString    = new String(binDir + "VoipStorm-Old.jar");

        eCallCenterManager = eCallCenterManagerParam;
        int rescode = downloadRemoteJarFile();

        if ( rescode == 200 ) { compareFiles(); }
        else
        {
            eCallCenterManager.showStatus("Software Update Check Failed!" + responseCodeDescription,true, true);
            eCallCenterManager.managerVersionLabel.setForeground(Color.yellow);
            eCallCenterManager.callcenterVersionLabel.setForeground(Color.yellow);
            eCallCenterManager.ephoneVersionLabel.setForeground(Color.yellow);
        } // true = logtoapplic, true = logtofile
    }

    /**
     *
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public final int downloadRemoteJarFile() throws java.net.MalformedURLException, java.io.IOException
    {
        eCallCenterManager.showStatus("Download and Check Update...", true, true); // true = logtoapplic, true = logtofile
        final URL remoteURL = new URL( UPDATEURL );
        HttpURLConnection urlConnection = (HttpURLConnection)remoteURL.openConnection();
//        URLConnection urlConnection = remoteURL.openConnection();
        urlConnection.setConnectTimeout(httpConTimeout);
        int responseCode = urlConnection.getResponseCode();
        responseCodeDescription = urlConnection.getResponseMessage();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(newJarFileString);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        int byteNumber; while ((byteNumber = bufferedInputStream.read()) != -1)
        {
//            eCallCenterManager.showStatus("Downloading and checking Version..." + Integer.toString(bufferedInputStream.read()), true);
            bufferedOutputStream.write(byteNumber);
        }
        bufferedOutputStream.flush();
        return responseCode;
    }

    /**
     *
     * @throws FileNotFoundException
     * @throws MalformedURLException
     * @throws IOException
     */
    public void compareFiles() throws FileNotFoundException, MalformedURLException, IOException
    {
        File        newFile         = new File(newJarFileString);
        File        curFile         = new File(curJarFileString);

        long        newFileLength   = newFile.length();
        long        curFileLength   = curFile.length();

        InputStream newInputStream  = new FileInputStream(newJarFileString);
        InputStream curInputStream  = new FileInputStream(curJarFileString);

        byte[]      newByteArray    = new byte[(int)newFileLength];
        byte[]      curByteArray    = new byte[(int)curFileLength];

        //Read bytes
        int offset; int numRead;
        offset = 0; numRead = 0; try { while (offset < newByteArray.length && (numRead = newInputStream.read(newByteArray, offset, newByteArray.length - offset)) >= 0) { offset += numRead; } } catch (IOException ex) { }
        offset = 0; numRead = 0; try { while (offset < curByteArray.length && (numRead = curInputStream.read(curByteArray, offset, curByteArray.length - offset)) >= 0) { offset += numRead; } } catch (IOException ex) { }

        String remoteMD5String = MD5Converter.getMD5SumFromByteArray(newByteArray);
        String localMD5String  = MD5Converter.getMD5SumFromByteArray(curByteArray);

        if (remoteMD5String.equals(localMD5String))
        {
            detectedANewVersion = false;
            eCallCenterManager.showStatus("Software is up to Date!",true, true); // true = logtoapplic, true = logtofile
            eCallCenterManager.managerVersionLabel.setForeground(Color.green);
            eCallCenterManager.callcenterVersionLabel.setForeground(Color.green);
            eCallCenterManager.ephoneVersionLabel.setForeground(Color.green);
        }
        else
        {
            detectedANewVersion = true;
            eCallCenterManager.showStatus("New Software Update Found",true, true); // true = logtoapplic, true = logtofile
            eCallCenterManager.managerVersionLabel.setForeground(Color.red);
            eCallCenterManager.callcenterVersionLabel.setForeground(Color.red);
            eCallCenterManager.ephoneVersionLabel.setForeground(Color.red);
            
            updateDialog = new VersionUpdateDialog(new javax.swing.JFrame(),true); updateDialog.setVisible(true);

            int retcode = updateDialog.getReturnStatus();

            if (retcode == VersionUpdateDialog.RET_OK)
            {
                installNewVersion();
                eCallCenterManager.requestStop(); // This is stopping the CallCenterManager mother process
            }
            else if (retcode == VersionUpdateDialog.RET_CANCEL)
            {
                eCallCenterManager.showStatus("Software Update User Canceled!",true, true); // true = logtoapplic, true = logtofile
            }
        }
    }

    /**
     *
     */
    public void installNewVersion()
    {
        shell = new Shell();

        Thread restartThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                shell.startVoipStormUpdater();
            }
        });
        restartThread.setName("restartThread");
        restartThread.setDaemon(runThreadsAsDaemons);
        restartThread.start();

        // try { Thread.sleep(1000); } catch (InterruptedException error) {  } // Give the VoipStormUpdater Thread some time to get started
    }

    /**
     *
     * @return
     */
    public boolean hasDetectedANewVersion() { return detectedANewVersion; }

    /**
     *
     * @return
     */
    public boolean hasInstalledNewVersion() { return newVersionInstalled; }
}
