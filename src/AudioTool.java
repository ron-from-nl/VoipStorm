import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
    import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author ron
 */
public class AudioTool
{
    private boolean end = false;
    private final static String soundsDir = "sounds";
    private URL calltoneURL, ringtoneURL, dialtoneURL, busytoneURL, deadtoneURL, errortoneURL, clickontoneURL, clickofftoneURL, ticktoneURL, successtoneURL, powersuccesstoneURL, failuretoneURL, registerenabledtoneURL, registerdisabledtoneURL, answerenabledtoneURL, answerdisabledtoneURL, cancelenabledURL, canceldisabledURL, muteenabledURL, mutedisabledURL;
    private DataLine.Info calltoneInfo, ringtoneInfo, dialtoneInfo, busytoneInfo, deadtoneInfo, errortoneInfo, clickontoneInfo, clickofftoneInfo, ticktoneInfo, successtoneInfo, powersuccesstoneInfo, failuretoneInfo, registerenabledtoneInfo, registerdisabledtoneInfo, answerenabledtoneInfo, answerdisabledtoneInfo, cancelenabledInfo, canceldisabledInfo, muteenabledInfo, mutedisabledInfo;
    private AudioInputStream calltoneStream, ringtoneStream, dialtoneStream, busytoneStream, deadtoneStream, errortoneStream, clickontoneStream, clickofftoneStream, ticktoneStream, successtoneStream, powersuccesstoneStream, failuretoneStream, registerenabledtoneStream, registerdisabledtoneStream, answerenabledtoneStream, answerdisabledtoneStream, cancelenabledStream, canceldisabledStream, muteenabledStream, mutedisabledStream;
    private Clip calltoneClip, ringtoneClip, dialtoneClip, busytoneClip, deadtoneClip, errortoneClip, clickontoneClip, clickofftoneClip, ticktoneClip, successtoneClip, powersuccesstoneClip, failuretoneClip, registerenabledtoneClip, registerdisabledtoneClip, answerenabledtoneClip, answerdisabledtoneClip, cancelenabledClip, canceldisabledClip, muteenabledClip, mutedisabledClip;

    /**
     *
     */
    public DataLine dataLine;

    /**
     *
     */
    public AudioTool()
    {
        ringtoneURL = getClass().getClassLoader().getResource(soundsDir + "/ringtone.wav");
        try { ringtoneStream = AudioSystem.getAudioInputStream(ringtoneURL); }
        catch (UnsupportedAudioFileException ex)                                                             { System.out.println("Something wrong with line1: " + ex.getMessage()); }
        catch (IOException ex)                                                                               { System.out.println("Something wrong with line2: " + ex.getMessage()); }
        ringtoneInfo = new DataLine.Info(Clip.class, ringtoneStream.getFormat());

        try { dataLine = (DataLine) AudioSystem.getLine(ringtoneInfo); } catch (LineUnavailableException ex) { System.out.println("Something wrong with line3: " + ex.getMessage()); }
        try
        {
            calltoneURL = getClass().getClassLoader().getResource(soundsDir + "/calltone.wav");                     calltoneStream = AudioSystem.getAudioInputStream(calltoneURL);                          calltoneInfo = new DataLine.Info(Clip.class, calltoneStream.getFormat());                           calltoneClip = (Clip) AudioSystem.getLine(calltoneInfo);                            calltoneClip.open(calltoneStream);
            ringtoneURL = getClass().getClassLoader().getResource(soundsDir + "/ringtone.wav");                     ringtoneStream = AudioSystem.getAudioInputStream(ringtoneURL);                          ringtoneInfo = new DataLine.Info(Clip.class, ringtoneStream.getFormat());                           ringtoneClip = (Clip) AudioSystem.getLine(ringtoneInfo);                            ringtoneClip.open(ringtoneStream);
            dialtoneURL = getClass().getClassLoader().getResource(soundsDir + "/dialtone.wav");                     dialtoneStream = AudioSystem.getAudioInputStream(dialtoneURL);                          dialtoneInfo = new DataLine.Info(Clip.class, dialtoneStream.getFormat());                           dialtoneClip = (Clip) AudioSystem.getLine(dialtoneInfo);                            dialtoneClip.open(dialtoneStream);
            busytoneURL = getClass().getClassLoader().getResource(soundsDir + "/busytone.wav");                     busytoneStream = AudioSystem.getAudioInputStream(busytoneURL);                          busytoneInfo = new DataLine.Info(Clip.class, busytoneStream.getFormat());                           busytoneClip = (Clip) AudioSystem.getLine(busytoneInfo);                            busytoneClip.open(busytoneStream);
            deadtoneURL = getClass().getClassLoader().getResource(soundsDir + "/deadtone.wav");                     deadtoneStream = AudioSystem.getAudioInputStream(deadtoneURL);                          deadtoneInfo = new DataLine.Info(Clip.class, deadtoneStream.getFormat());                           deadtoneClip = (Clip) AudioSystem.getLine(deadtoneInfo);                            deadtoneClip.open(deadtoneStream);
            errortoneURL = getClass().getClassLoader().getResource(soundsDir + "/errortone.wav");                   errortoneStream = AudioSystem.getAudioInputStream(errortoneURL);                        errortoneInfo = new DataLine.Info(Clip.class, errortoneStream.getFormat());                         errortoneClip = (Clip) AudioSystem.getLine(errortoneInfo);                          errortoneClip.open(errortoneStream);
            clickontoneURL = getClass().getClassLoader().getResource(soundsDir + "/clickontone.wav");               clickontoneStream = AudioSystem.getAudioInputStream(clickontoneURL);                    clickontoneInfo = new DataLine.Info(Clip.class, clickontoneStream.getFormat());                     clickontoneClip = (Clip) AudioSystem.getLine(clickontoneInfo);                      clickontoneClip.open(clickontoneStream);
            clickofftoneURL = getClass().getClassLoader().getResource(soundsDir + "/clickofftone.wav");             clickofftoneStream = AudioSystem.getAudioInputStream(clickofftoneURL);                  clickofftoneInfo = new DataLine.Info(Clip.class, clickofftoneStream.getFormat());                   clickofftoneClip = (Clip) AudioSystem.getLine(clickofftoneInfo);                    clickofftoneClip.open(clickofftoneStream);
            clickofftoneURL = getClass().getClassLoader().getResource(soundsDir + "/clickofftone.wav");             clickofftoneStream = AudioSystem.getAudioInputStream(clickofftoneURL);                  clickofftoneInfo = new DataLine.Info(Clip.class, clickofftoneStream.getFormat());                   clickofftoneClip = (Clip) AudioSystem.getLine(clickofftoneInfo);                    clickofftoneClip.open(clickofftoneStream);
            ticktoneURL = getClass().getClassLoader().getResource(soundsDir + "/ticktone.wav");                     ticktoneStream = AudioSystem.getAudioInputStream(ticktoneURL);                          ticktoneInfo = new DataLine.Info(Clip.class, ticktoneStream.getFormat());                           ticktoneClip = (Clip) AudioSystem.getLine(ticktoneInfo);                            ticktoneClip.open(ticktoneStream);
            successtoneURL = getClass().getClassLoader().getResource(soundsDir + "/successtone.wav");               successtoneStream = AudioSystem.getAudioInputStream(successtoneURL);                    successtoneInfo = new DataLine.Info(Clip.class, successtoneStream.getFormat());                     successtoneClip = (Clip) AudioSystem.getLine(successtoneInfo);                      successtoneClip.open(successtoneStream);
            powersuccesstoneURL = getClass().getClassLoader().getResource(soundsDir + "/powersuccesstone.wav");     powersuccesstoneStream = AudioSystem.getAudioInputStream(powersuccesstoneURL);          powersuccesstoneInfo = new DataLine.Info(Clip.class, powersuccesstoneStream.getFormat());           powersuccesstoneClip = (Clip) AudioSystem.getLine(powersuccesstoneInfo);            powersuccesstoneClip.open(powersuccesstoneStream);
            failuretoneURL = getClass().getClassLoader().getResource(soundsDir + "/failuretone.wav");               failuretoneStream = AudioSystem.getAudioInputStream(failuretoneURL);                    failuretoneInfo = new DataLine.Info(Clip.class, failuretoneStream.getFormat());                     failuretoneClip = (Clip) AudioSystem.getLine(failuretoneInfo);                      failuretoneClip.open(failuretoneStream);
            registerenabledtoneURL = getClass().getClassLoader().getResource(soundsDir + "/enabledtone.wav");       registerenabledtoneStream = AudioSystem.getAudioInputStream(registerenabledtoneURL);    registerenabledtoneInfo = new DataLine.Info(Clip.class, registerenabledtoneStream.getFormat());     registerenabledtoneClip = (Clip) AudioSystem.getLine(registerenabledtoneInfo);      registerenabledtoneClip.open(registerenabledtoneStream);
            registerdisabledtoneURL = getClass().getClassLoader().getResource(soundsDir + "/disabledtone.wav");     registerdisabledtoneStream = AudioSystem.getAudioInputStream(registerdisabledtoneURL);  registerdisabledtoneInfo = new DataLine.Info(Clip.class, registerdisabledtoneStream.getFormat());   registerdisabledtoneClip = (Clip) AudioSystem.getLine(registerdisabledtoneInfo);    registerdisabledtoneClip.open(registerdisabledtoneStream);
            answerenabledtoneURL = getClass().getClassLoader().getResource(soundsDir + "/enabledtone.wav");         answerenabledtoneStream = AudioSystem.getAudioInputStream(answerenabledtoneURL);        answerenabledtoneInfo = new DataLine.Info(Clip.class, answerenabledtoneStream.getFormat());         answerenabledtoneClip = (Clip) AudioSystem.getLine(answerenabledtoneInfo);          answerenabledtoneClip.open(answerenabledtoneStream);
            answerdisabledtoneURL = getClass().getClassLoader().getResource(soundsDir + "/disabledtone.wav");       answerdisabledtoneStream = AudioSystem.getAudioInputStream(answerdisabledtoneURL);      answerdisabledtoneInfo = new DataLine.Info(Clip.class, answerdisabledtoneStream.getFormat());       answerdisabledtoneClip = (Clip) AudioSystem.getLine(answerdisabledtoneInfo);        answerdisabledtoneClip.open(answerdisabledtoneStream);
            cancelenabledURL = getClass().getClassLoader().getResource(soundsDir + "/enabledtone.wav");             cancelenabledStream = AudioSystem.getAudioInputStream(cancelenabledURL);                cancelenabledInfo = new DataLine.Info(Clip.class, cancelenabledStream.getFormat());                 cancelenabledClip = (Clip) AudioSystem.getLine(cancelenabledInfo);                  cancelenabledClip.open(cancelenabledStream);
            canceldisabledURL = getClass().getClassLoader().getResource(soundsDir + "/disabledtone.wav");           canceldisabledStream = AudioSystem.getAudioInputStream(canceldisabledURL);              canceldisabledInfo = new DataLine.Info(Clip.class, canceldisabledStream.getFormat());               canceldisabledClip = (Clip) AudioSystem.getLine(canceldisabledInfo);                canceldisabledClip.open(canceldisabledStream);
            muteenabledURL = getClass().getClassLoader().getResource(soundsDir + "/enabledtone.wav");               muteenabledStream = AudioSystem.getAudioInputStream(muteenabledURL);                    muteenabledInfo = new DataLine.Info(Clip.class, muteenabledStream.getFormat());                     muteenabledClip = (Clip) AudioSystem.getLine(muteenabledInfo);                      muteenabledClip.open(muteenabledStream);
            mutedisabledURL = getClass().getClassLoader().getResource(soundsDir + "/disabledtone.wav");             mutedisabledStream = AudioSystem.getAudioInputStream(mutedisabledURL);                  mutedisabledInfo = new DataLine.Info(Clip.class, mutedisabledStream.getFormat());                   mutedisabledClip = (Clip) AudioSystem.getLine(mutedisabledInfo);                    mutedisabledClip.open(mutedisabledStream);
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     *
     * @param clipParam
     */
    public void rewind(Clip clipParam)          {  }

    /**
     *
     * @param clipParam
     */
    public void fastBackward(Clip clipParam)    {  }

    /**
     *
     * @param clipParam
     */
    public void play(Clip clipParam)            { clipParam.setFramePosition(0); clipParam.start(); }

    /**
     *
     * @param clipParam
     */
    public void playLoop(Clip clipParam)	{ clipParam.setFramePosition(0); clipParam.loop(Clip.LOOP_CONTINUOUSLY); }

    /**
     *
     * @param clipParam
     */
    public void stop(Clip clipParam)		{ clipParam.stop(); clipParam.setFramePosition(0); }

    /**
     *
     * @param clipParam
     */
    public void fastForward(Clip clipParam)	{  }

    /**
     *
     * @param clipParam
     */
    public void wind(Clip clipParam)		{  }
    
    /**
     *
     * @return
     */
    public Clip getCalltoneClip()               { return calltoneClip; }

    /**
     *
     * @return
     */
    public Clip getRingtoneClip()               { return ringtoneClip; }

    /**
     *
     * @return
     */
    public Clip getDialtoneClip()               { return dialtoneClip; }

    /**
     *
     * @return
     */
    public Clip getBusytoneClip()               { return busytoneClip; }

    /**
     *
     * @return
     */
    public Clip getDeadtoneClip()               { return deadtoneClip; }

    /**
     *
     * @return
     */
    public Clip getErrortoneClip()              { return errortoneClip; }

    /**
     *
     * @return
     */
    public Clip getClickontoneClip()            { return clickontoneClip; }

    /**
     *
     * @return
     */
    public Clip getClickofftoneClip()           { return clickofftoneClip; }

    /**
     *
     * @return
     */
    public Clip getTicktoneClip()               { return ticktoneClip; }

    /**
     *
     * @return
     */
    public Clip getSuccesstoneClip()            { return successtoneClip; }

    /**
     *
     * @return
     */
    public Clip getPowersuccesstoneClip()       { return powersuccesstoneClip; }

    /**
     *
     * @return
     */
    public Clip getFailuretoneClip()            { return failuretoneClip; }

    /**
     *
     * @return
     */
    public Clip getRegisterenabledtoneClip()    { return registerenabledtoneClip; }

    /**
     *
     * @return
     */
    public Clip getRegisterdisabledtoneClip()   { return registerdisabledtoneClip; }

    /**
     *
     * @return
     */
    public Clip getAnswerenabledtoneClip()      { return answerenabledtoneClip; }

    /**
     *
     * @return
     */
    public Clip getAnswerdisabledtoneClip()     { return answerdisabledtoneClip; }

    /**
     *
     * @return
     */
    public Clip getCancelenabledClip()          { return cancelenabledClip; }

    /**
     *
     * @return
     */
    public Clip getCanceldisabledClip()         { return canceldisabledClip; }

    /**
     *
     * @return
     */
    public Clip getMuteenabledClip()            { return muteenabledClip; }

    /**
     *
     * @return
     */
    public Clip getMutedisabledClip()           { return mutedisabledClip; }

    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                AudioTool audioTool = new AudioTool();
            }
        });
    }
}

