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

import datasets.SpeakerData;
import datasets.DisplayData;
import datasets.Destination;

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
