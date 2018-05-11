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

package datasets;

/**
 *
 * @author ron
 */
public class TimeWindow
{
    private String              description;

    /**
     *
     */
    public static final int     SECOND                  = 1;

    /**
     *
     */
    public static final int     MINUTE                  = SECOND * 60;

    /**
     *
     */
    public static final int     HOUR                    = MINUTE * 60;

    /**
     *
     */
    public static final int     DAY                     = HOUR   * 24;

    private final int[]         DAYSTARTDAYSTART        = { 0, 0 };  // HR,MIN

    private int[]               start; //                 { hour, minute }
    private int[]               end; //                   { hour, minute }

    private int                 startSeconds;
    private int                 endSeconds;
    private int                 seconds;
    private float               factor;

    /**
     *
     * @param descriptionParam
     * @param startHourParam
     * @param startMinuteParam
     * @param endHourParam
     * @param endMinuteParam
     */
    public TimeWindow(String descriptionParam, int startHourParam, int startMinuteParam, int endHourParam, int endMinuteParam )
    {
        description             = descriptionParam;

        start = new int[2];

        start[0]                = startHourParam;
        start[1]                = startMinuteParam;

        end   = new int[2];

        end[0]                  = endHourParam;
        end[1]                  = endMinuteParam;

        startSeconds            = (HOUR * start[0]) + (MINUTE *  start[1]);
        endSeconds              = (HOUR * end[0]) + (MINUTE * end[1]);
        seconds                 = (endSeconds - startSeconds);
        factor                  = (DAY / seconds);
    }

    /**
     *
     */
    public TimeWindow() {
        
    }

    /**
     *
     * @return
     */
    public int              getSECOND()             { return SECOND; }

    /**
     *
     * @return
     */
    public int              getMINUTE()             { return MINUTE; }

    /**
     *
     * @return
     */
    public int              getHOUR()               { return HOUR; }

    /**
     *
     * @return
     */
    public int              getDAY()                { return DAY; }

    /**
     *
     * @return
     */
    public String           getDescription()        { return description; }

    /**
     *
     * @return
     */
    public int              getStartHour()          { return start[0]; }

    /**
     *
     * @return
     */
    public int              getStartMinute()        { return start[1]; }

    /**
     *
     * @return
     */
    public int              getEndHour()            { return end[0]; }

    /**
     *
     * @return
     */
    public int              getEndMinute()          { return end[1]; }

    /**
     *
     * @return
     */
    public int              getStartSeconds()       { return startSeconds; }

    /**
     *
     * @return
     */
    public int              getEndSeconds()         { return endSeconds; }

    /**
     *
     * @return
     */
    public int              getSeconds()            { return seconds; }

    /**
     *
     * @return
     */
    public float            getTimewindowFACTOR()   { return factor; }
}
