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
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Time;
import javax.media.protocol.*;

/**
 *
 * @author ron
 */
public class SoundTool implements ControllerListener
{
    private URL url;
    private MediaLocator mediaLocator;
    private DataSource dsource;
    private Player player;
    private boolean end = false;
    private float mediaRate = 1;
    private final static String dataDir			= "data/";
    private final static String soundsDir               = dataDir + "sounds/";

    /**
     *
     */
    public  final static String RINGTONE                = soundsDir + "/ringtone.wav";

    /**
     *
     */
    public  final static String DIALTONE                = soundsDir + "/dialtone.wav";

    /**
     *
     */
    public  final static String CALLTONE                = soundsDir + "/calltone.wav";

    /**
     *
     */
    public  final static String BUSYTONE                = soundsDir + "/busytone.wav";

    /**
     *
     */
    public  final static String DEADTONE                = soundsDir + "/deadtone.wav";

    /**
     *
     */
    public  final static String ERRORTONE               = soundsDir + "/errortone.wav";

    /**
     *
     */
    public  final static String CLICKONTONE             = soundsDir + "/clickontone.wav";

    /**
     *
     */
    public  final static String CLICKOFFTONE            = soundsDir + "/clickofftone.wav";

    /**
     *
     */
    public  final static String TICKTONE                = soundsDir + "/ticktone.wav";

    /**
     *
     */
    public  final static String SUCCESSTONE             = soundsDir + "/successtone.wav";

    /**
     *
     */
    public  final static String POWERSUCCESSTONE        = soundsDir + "/powersuccesstone.wav";

    /**
     *
     */
    public  final static String FAILURETONE             = soundsDir + "/failuretone.wav";

    /**
     *
     */
    public  final static String REGISTERENABLEDTONE     = soundsDir + "/enabledtone.wav";

    /**
     *
     */
    public  final static String REGISTERDISABLEDTONE    = soundsDir + "/disabledtone.wav";

    /**
     *
     */
    public  final static String ANSWERENABLEDTONE       = soundsDir + "/enabledtone.wav";

    /**
     *
     */
    public  final static String ANSWERDISABLEDTONE      = soundsDir + "/disabledtone.wav";

    /**
     *
     */
    public  final static String CANCELENABLEDTONE       = soundsDir + "/enabledtone.wav";

    /**
     *
     */
    public  final static String CANCELDISABLEDTONE      = soundsDir + "/disabledtone.wav";

    /**
     *
     */
    public  final static String MUTEENABLEDTONE         = soundsDir + "/enabledtone.wav";

    /**
     *
     */
    public  final static String MUTEDISABLEDTONE        = soundsDir + "/disabledtone.wav";

    /**
     *
     * @param urlParam
     */
    public SoundTool(String urlParam)
    {
        try
        {
            mediaLocator = new MediaLocator(urlParam);
            dsource = Manager.createDataSource(mediaLocator);
            player = Manager.createRealizedPlayer(dsource);
            player.addControllerListener(this);
        }
        catch(Exception ex) { ex.printStackTrace(); System.out.println(ex.getMessage()); }
    }

    /**
     *
     */
    public void rewind()	    { player.setMediaTime(Time.TIME_UNKNOWN); }

    /**
     *
     */
    public void fastBackward()	    { mediaRate -= 0.5; player.setRate(mediaRate); }

    /**
     *
     */
    public void play()		    { end = true; mediaRate = 1; player.setRate(mediaRate); player.start(); }

    /**
     *
     */
    public void playLoop()	    { end = false; mediaRate = 1; player.setRate(mediaRate); player.start(); }

    /**
     *
     */
    public void stop()		    { end = true; player.stop(); player.setMediaTime(Time.TIME_UNKNOWN); }

    /**
     *
     */
    public void fastForward()	    { mediaRate += 0.5; player.setRate(mediaRate);}

    /**
     *
     */
    public void wind()		    { player.setMediaTime(player.getDuration()); }

    /**
     *
     * @return
     */
    public int getMediaDuration()
    {
        int duration = 0;
        if (player.getDuration() != null ) { duration = (int)player.getDuration().getSeconds(); }
        return duration;
    }

    /**
     *
     * @return
     */
    public String getSoundsDir()    { return soundsDir; }
    
    /**
     *
     * @param cEvent
     */
    @Override
    public void controllerUpdate(ControllerEvent cEvent) { if (cEvent instanceof EndOfMediaEvent) { if (!end) { rewind(); player.start(); } else      { player.stop(); rewind();  } } }

    /**
     *
     */
    public void dispose()	    { player.removeControllerListener(this); player.close();}
}
