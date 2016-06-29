
/**
 *
 * @author ron
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
