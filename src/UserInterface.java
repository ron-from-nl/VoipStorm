import data.SpeakerData;
import data.DisplayData;
import data.Destination;

/**
 *
 * @author ron
 */
public interface UserInterface {

    /**
     *
     * @param message
     */
    public void logToApplication(String message);

    /**
     *
     * @param message
     */
    public void logToFile(String message);

    /**
     *
     */
    public void resetLog();

    /**
     *
     * @param displayParam
     */
    public void phoneDisplay(DisplayData displayParam);

    /**
     *
     * @param speakerParam
     */
    public void speaker(SpeakerData speakerParam);

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    public void showStatus(String messageParam, boolean logToApplicationParam, boolean logToFileParam);

    /**
     *
     * @param sipstateParam
     * @param lastsipstateParam
     * @param loginstateParam
     * @param softphoneActivityParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    public void sipstateUpdate(final int sipstateParam, final int lastsipstateParam, final int loginstateParam, final int softphoneActivityParam, final int softPhoneInstanceIdParam, final Destination destinationParam); // Mainly for statistics

    /**
     *
     * @param responseCodeParam
     * @param responseReasonPhraseParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    public void responseUpdate(int responseCodeParam, String responseReasonPhraseParam, int softPhoneInstanceIdParam, Destination destinationParam); // Mainly for statistics

    /**
     *
     * @param messageParam
     * @param valueParam
     */
    public void feedback(String messageParam, int valueParam);
}
