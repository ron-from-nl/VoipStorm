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

import java.util.Calendar;

/**
 *
 * @author ron
 */
public class Campaign implements Cloneable
{
    private int id;
    private Calendar calendarCampaignCreated;
    private int orderId;
    private Calendar calendarScheduledStart;
    private Calendar calendarScheduledEnd;
    private Calendar calendarExpectedStart;
    private Calendar calendarExpectedEnd;
    private Calendar calendarRegisteredStart;
    private Calendar calendarRegisteredEnd;
    private boolean  testCampaign;

    /**
     *
     */
    public Campaign()
    {
        id                       = 0;
        calendarCampaignCreated  = Calendar.getInstance(); calendarCampaignCreated.setTimeInMillis(0);
        orderId                  = 0;
        calendarScheduledStart   = Calendar.getInstance(); calendarScheduledStart.setTimeInMillis(0);
        calendarScheduledEnd     = Calendar.getInstance(); calendarScheduledEnd.setTimeInMillis(0);
        calendarExpectedStart    = Calendar.getInstance(); calendarExpectedStart.setTimeInMillis(0);
        calendarExpectedEnd      = Calendar.getInstance(); calendarExpectedEnd.setTimeInMillis(0);
        calendarRegisteredStart  = Calendar.getInstance(); calendarRegisteredStart.setTimeInMillis(0);
        calendarRegisteredEnd    = Calendar.getInstance(); calendarRegisteredEnd.setTimeInMillis(0);
        testCampaign             = false;

    }

    /**
     *
     * @param idParam
     * @param calendarCampaignCreatedParam
     * @param orderIdParam
     * @param calendarScheduledStartParam
     * @param calendarScheduledEndParam
     * @param calendarExpectedStartParam
     * @param calendarExpectedEndParam
     * @param calendarRegisteredStartParam
     * @param calendarRegisteredEndParam
     * @param testCampaignParam
     */
    public Campaign(
                     int idParam,
                     Calendar calendarCampaignCreatedParam,
                     int orderIdParam,
                     Calendar calendarScheduledStartParam,
                     Calendar calendarScheduledEndParam,
                     Calendar calendarExpectedStartParam,
                     Calendar calendarExpectedEndParam,
                     Calendar calendarRegisteredStartParam,
                     Calendar calendarRegisteredEndParam,
                     boolean  testCampaignParam
                   )
    {
        id                       = idParam;
        calendarCampaignCreated  = calendarCampaignCreatedParam;
        orderId                  = orderIdParam;
        calendarScheduledStart   = calendarScheduledStartParam;
        calendarScheduledEnd     = calendarScheduledEndParam;
        calendarExpectedStart    = calendarExpectedStartParam;
        calendarExpectedEnd      = calendarExpectedEndParam;
        calendarRegisteredStart  = calendarRegisteredStartParam;
        calendarRegisteredEnd    = calendarRegisteredEndParam;
        testCampaign             = testCampaignParam;
    }

    public int       getId()                                { return id;}
    public Calendar  getCalendarCampaignCreated()           { return calendarCampaignCreated;}
    public int       getOrderId()                           { return orderId;}
    public Calendar  getCalendarScheduledStart()            { return calendarScheduledStart;}
    public Calendar  getCalendarScheduledEnd()              { return calendarScheduledEnd;}
    public Calendar  getCalendarExpectedStart()             { return calendarExpectedStart;}
    public Calendar  getCalendarExpectedEnd()               { return calendarExpectedEnd;}
    public Calendar  getCalendarRegisteredStart()           { return calendarRegisteredStart;}
    public Calendar  getCalendarRegisteredEnd()             { return calendarRegisteredEnd;}
    public boolean   getTestCampaign()                      { return testCampaign;}
    public int       getTestCampaignNumber()                { if (testCampaign) {return (int)1;} else {return (int)0;}}
    public void   setId(int idParam)                                                { id                        = idParam; }
    public void   setCalendarCampaignCreated(Calendar calendarCampaignCreatedParam) { calendarCampaignCreated   = calendarCampaignCreatedParam; }
    public void   setOrderId(int orderIdParam)                                      { orderId                   = orderIdParam; }
    public void   setCalendarScheduledStart(Calendar calendarScheduledStartParam)   { calendarScheduledStart    = calendarScheduledStartParam; }
    public void   setCalendarScheduledEnd(Calendar calendarScheduledEndParam)       { calendarScheduledEnd      = calendarScheduledEndParam; }
    public void   setCalendarExpectedStart(Calendar calendarExpectedStartParam)     { calendarExpectedStart     = calendarExpectedStartParam; }
    public void   setCalendarExpectedEnd(Calendar calendarExpectedEndParam)         { calendarExpectedEnd       = calendarExpectedEndParam; }
    public void   setCalendarRegisteredStart(Calendar calendarRegisteredStartParam) { calendarRegisteredStart   = calendarRegisteredStartParam; }
    public void   setCalendarRegisteredEnd(Calendar calendarRegisteredEndParam)     { calendarRegisteredEnd     = calendarRegisteredEndParam; }
    public void   setTestCampaign(boolean testCampaignParam)                        { testCampaign = testCampaignParam; }
    public void   setTestCampaignNumber(int testCampaignParam)                      { if (testCampaignParam == 0) {testCampaign = false;} else {testCampaign = true;} }
    public void   setCalendarCampaignCreatedEpoch(Long calendarCampaignCreatedEpochParam) { calendarCampaignCreated.setTimeInMillis(calendarCampaignCreatedEpochParam);}
    public void   setCalendarScheduledStartEpoch(Long calendarScheduledStartEpochParam)   { calendarScheduledStart.setTimeInMillis(calendarScheduledStartEpochParam);}
    public void   setCalendarScheduledEndEpoch(Long calendarScheduledEndEpochParam)       { calendarScheduledEnd.setTimeInMillis(calendarScheduledEndEpochParam);}
    public void   setCalendarExpectedStartEpoch(Long calendarExpectedStartEpochParam)     { calendarExpectedStart.setTimeInMillis(calendarExpectedStartEpochParam);}
    public void   setCalendarExpectedEndEpoch(Long calendarExpectedEndEpochParam)         { calendarExpectedEnd.setTimeInMillis(calendarExpectedEndEpochParam);}
    public void   setCalendarRegisteredStartEpoch(Long calendarRegisteredStartEpochParam) { calendarRegisteredStart.setTimeInMillis(calendarRegisteredStartEpochParam);}
    public void   setCalendarRegisteredEndEpoch(Long calendarRegisteredEndEpochParam)     { calendarRegisteredEnd.setTimeInMillis(calendarRegisteredEndEpochParam);}
    public void   resetCalendarCampaignCreated() { calendarCampaignCreated.setTimeInMillis(0);}
    public void   resetCalendarScheduledStart()  { calendarScheduledStart.setTimeInMillis(0);}
    public void   resetCalendarScheduledEnd()    { calendarScheduledEnd.setTimeInMillis(0);}
    public void   resetCalendarExpectedStart()   { calendarExpectedStart.setTimeInMillis(0);}
    public void   resetCalendarExpectedEnd()     { calendarExpectedEnd.setTimeInMillis(0);}
    public void   resetCalendarRegisteredStart() { calendarRegisteredStart.setTimeInMillis(0);}
    public void   resetCalendarRegisteredEnd()   { calendarRegisteredEnd.setTimeInMillis(0);}

    @Override
    public String   toString()
    {
        String output = new String("");
        output += "Id: "                + Integer.toString(id) + "\n";
        output += "Timestamp: "         + calendarCampaignCreated.getTimeInMillis() + "\n";
        output += "OrderId: "           + Integer.toString(orderId) + "\n";
        output += "ExpectStart: "       + calendarScheduledStart.getTimeInMillis() + "\n";
        output += "EExpectEnd: "        + calendarScheduledEnd.getTimeInMillis() + "\n";
        output += "ExpectStart: "       + calendarExpectedStart.getTimeInMillis() + "\n";
        output += "EExpectEnd: "        + calendarExpectedEnd.getTimeInMillis() + "\n";
        output += "RegisteredStart: "   + calendarRegisteredStart.getTimeInMillis() + "\n";
        output += "RegisteredEnd: "     + calendarRegisteredEnd.getTimeInMillis() + "\n";
        output += "TestCampaign: "      + testCampaign + "\n";

        return output;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
