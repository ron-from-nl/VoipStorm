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

    /**
     *
     * @return
     */
    public int       getId()                                { return id;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarCampaignCreated()           { return calendarCampaignCreated;}

    /**
     *
     * @return
     */
    public int       getOrderId()                           { return orderId;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarScheduledStart()            { return calendarScheduledStart;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarScheduledEnd()              { return calendarScheduledEnd;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarExpectedStart()             { return calendarExpectedStart;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarExpectedEnd()               { return calendarExpectedEnd;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarRegisteredStart()           { return calendarRegisteredStart;}

    /**
     *
     * @return
     */
    public Calendar  getCalendarRegisteredEnd()             { return calendarRegisteredEnd;}

    /**
     *
     * @return
     */
    public boolean   getTestCampaign()                      { return testCampaign;}

    /**
     *
     * @return
     */
    public int       getTestCampaignNumber()                { if (testCampaign) {return (int)1;} else {return (int)0;}}

    /**
     *
     * @param idParam
     */
    public void   setId(int idParam)                                                { id                        = idParam; }

    /**
     *
     * @param calendarCampaignCreatedParam
     */
    public void   setCalendarCampaignCreated(Calendar calendarCampaignCreatedParam) { calendarCampaignCreated   = calendarCampaignCreatedParam; }

    /**
     *
     * @param orderIdParam
     */
    public void   setOrderId(int orderIdParam)                                      { orderId                   = orderIdParam; }

    /**
     *
     * @param calendarScheduledStartParam
     */
    public void   setCalendarScheduledStart(Calendar calendarScheduledStartParam)   { calendarScheduledStart    = calendarScheduledStartParam; }

    /**
     *
     * @param calendarScheduledEndParam
     */
    public void   setCalendarScheduledEnd(Calendar calendarScheduledEndParam)       { calendarScheduledEnd      = calendarScheduledEndParam; }

    /**
     *
     * @param calendarExpectedStartParam
     */
    public void   setCalendarExpectedStart(Calendar calendarExpectedStartParam)     { calendarExpectedStart     = calendarExpectedStartParam; }

    /**
     *
     * @param calendarExpectedEndParam
     */
    public void   setCalendarExpectedEnd(Calendar calendarExpectedEndParam)         { calendarExpectedEnd       = calendarExpectedEndParam; }

    /**
     *
     * @param calendarRegisteredStartParam
     */
    public void   setCalendarRegisteredStart(Calendar calendarRegisteredStartParam) { calendarRegisteredStart   = calendarRegisteredStartParam; }

    /**
     *
     * @param calendarRegisteredEndParam
     */
    public void   setCalendarRegisteredEnd(Calendar calendarRegisteredEndParam)     { calendarRegisteredEnd     = calendarRegisteredEndParam; }

    /**
     *
     * @param testCampaignParam
     */
    public void   setTestCampaign(boolean testCampaignParam)                        { testCampaign = testCampaignParam; }

    /**
     *
     * @param testCampaignParam
     */
    public void   setTestCampaignNumber(int testCampaignParam)                      { if (testCampaignParam == 0) {testCampaign = false;} else {testCampaign = true;} }

    /**
     *
     * @param calendarCampaignCreatedEpochParam
     */
    public void   setCalendarCampaignCreatedEpoch(Long calendarCampaignCreatedEpochParam) { calendarCampaignCreated.setTimeInMillis(calendarCampaignCreatedEpochParam);}

    /**
     *
     * @param calendarScheduledStartEpochParam
     */
    public void   setCalendarScheduledStartEpoch(Long calendarScheduledStartEpochParam)   { calendarScheduledStart.setTimeInMillis(calendarScheduledStartEpochParam);}

    /**
     *
     * @param calendarScheduledEndEpochParam
     */
    public void   setCalendarScheduledEndEpoch(Long calendarScheduledEndEpochParam)       { calendarScheduledEnd.setTimeInMillis(calendarScheduledEndEpochParam);}

    /**
     *
     * @param calendarExpectedStartEpochParam
     */
    public void   setCalendarExpectedStartEpoch(Long calendarExpectedStartEpochParam)     { calendarExpectedStart.setTimeInMillis(calendarExpectedStartEpochParam);}

    /**
     *
     * @param calendarExpectedEndEpochParam
     */
    public void   setCalendarExpectedEndEpoch(Long calendarExpectedEndEpochParam)         { calendarExpectedEnd.setTimeInMillis(calendarExpectedEndEpochParam);}

    /**
     *
     * @param calendarRegisteredStartEpochParam
     */
    public void   setCalendarRegisteredStartEpoch(Long calendarRegisteredStartEpochParam) { calendarRegisteredStart.setTimeInMillis(calendarRegisteredStartEpochParam);}

    /**
     *
     * @param calendarRegisteredEndEpochParam
     */
    public void   setCalendarRegisteredEndEpoch(Long calendarRegisteredEndEpochParam)     { calendarRegisteredEnd.setTimeInMillis(calendarRegisteredEndEpochParam);}

    /**
     *
     */
    public void   resetCalendarCampaignCreated() { calendarCampaignCreated.setTimeInMillis(0);}

    /**
     *
     */
    public void   resetCalendarScheduledStart()  { calendarScheduledStart.setTimeInMillis(0);}

    /**
     *
     */
    public void   resetCalendarScheduledEnd()    { calendarScheduledEnd.setTimeInMillis(0);}

    /**
     *
     */
    public void   resetCalendarExpectedStart()   { calendarExpectedStart.setTimeInMillis(0);}

    /**
     *
     */
    public void   resetCalendarExpectedEnd()     { calendarExpectedEnd.setTimeInMillis(0);}

    /**
     *
     */
    public void   resetCalendarRegisteredStart() { calendarRegisteredStart.setTimeInMillis(0);}

    /**
     *
     */
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
