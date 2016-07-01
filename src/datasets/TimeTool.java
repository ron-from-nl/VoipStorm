package datasets;

import java.util.Calendar;

/**
 *
 * @author ron
 */
public class TimeTool
{
    private TimeWindow[]        timewindowArray;

    /**
     *
     */
    public TimeTool()
    {
        timewindowArray = new TimeWindow[3];
        timewindowArray[0] = new TimeWindow("Morning", 0, 0, 8, 59);
        timewindowArray[1] = new TimeWindow("Daytime", 9, 0, 17, 59);
        timewindowArray[2] = new TimeWindow("Evening", 18, 0, 23, 59);
    }

    /**
     *
     * @param headStartCalParam
     * @param orderParam
     * @param throughputFactorParam
     * @param targetOffset
     * @param timewindowIndices
     * @return
     */
    public Calendar         getEstimatedEndCalendar(Calendar headStartCalParam, Order orderParam, int throughputFactorParam, int targetOffset, int[] timewindowIndices) // First Cal is Campaign.expectedStartCal
    {
        // Get the TimeWindow
        long headDuration               = 0;
        long bodyDuration               = 0;
        long campaignDuration           = 0;
        long headAndBodyGrosDuration    = 0;
        long bodyGrosDuration           = 0;

        Calendar bodyEndGrosCal         = (Calendar) Calendar.getInstance();

            // 1) Set headStartCalendar
            Calendar headStartCal = (Calendar) headStartCalParam.clone();
            //System.out.println("\n\nheadStartCal:        " + displayCalendar(headStartCal));

            // 2) Calc and Set campaignDuration (Uninterrupted)
            campaignDuration = Math.round(((orderParam.getMessageDuration() * (orderParam.getTargetTransactionQuantity() - targetOffset)) / throughputFactorParam));
            //System.out.println("headAndBodyDuration: " + headAndBodyDuration + " Sec");

            // 3) Set bodyEndCalendar
            Calendar bodyEndCal = Calendar.getInstance();
            bodyEndCal.setTimeInMillis(headStartCal.getTimeInMillis() + (campaignDuration * 1000));
            //System.out.println("bodyEndCal:          " + displayCalendar(bodyEndCal));

            // 4) Set bodyStartCalendar Morning Timewindow
            Calendar headEndCal = (Calendar) headStartCal.clone();
            headEndCal.set(Calendar.HOUR_OF_DAY, 0);headEndCal.set(Calendar.MINUTE, 0); headEndCal.set(Calendar.SECOND, 0); // Calendar set to 00:00:00 that day
            headEndCal.setTimeInMillis(headEndCal.getTimeInMillis() + (timewindowArray[(timewindowIndices.length - 1)].getEndSeconds() * 1000));
            //System.out.println("daytimeEndCal:       " + displayCalendar(daytimeEndCal));

            if (bodyEndCal.after(headEndCal))
            {
                // 5) Set headDuration
                headDuration = ((headEndCal.getTimeInMillis()/1000) - (headStartCalParam.getTimeInMillis()/1000));
                //System.out.println("headDuration:        " + headDuration + " Sec");

                // 6) Set bodyDuration
                bodyDuration = ((bodyEndCal.getTimeInMillis()/1000) - (headEndCal.getTimeInMillis()/1000));
                //System.out.println("bodyDuration:        " + bodyDuration + " Sec");

                int selectedTimewindowsSeconds = 0; // Possibly Plural
                for (int timewindowIndice: timewindowIndices) { selectedTimewindowsSeconds += timewindowArray[timewindowIndice].getSeconds(); }
                // 7) Set bodyGrosDuration
                bodyGrosDuration = (bodyDuration + (TimeWindow.DAY - selectedTimewindowsSeconds) + ((bodyDuration / selectedTimewindowsSeconds) * (TimeWindow.DAY - selectedTimewindowsSeconds))); // Gros = NetBody + all NoGo dayparts
                //System.out.println("bodyGrosDuration:    " + bodyGrosDuration + " Sec");
                //System.out.println("daytimeDuration:     " + MORNINGSECS + " Sec");

                // 8) Set the bodyEndGrosCalendar
                bodyEndGrosCal.setTimeInMillis(headEndCal.getTimeInMillis() + (bodyGrosDuration * 1000));
                //System.out.println("bodyEndGrosCal:      " + displayCalendar(bodyEndGrosCal));
            }
            else
            {
                bodyEndGrosCal = (Calendar) bodyEndCal.clone();
                //System.out.println("bodyEndGrosCal:      " + displayCalendar(bodyEndGrosCal));
            }

        return bodyEndGrosCal;
    }

    /**
     *
     * @param calendarParam
     * @return
     */
    public String displayCalendar(Calendar calendarParam)
    {
        String calendarString = 
                                String.format("%04d", calendarParam.get(Calendar.YEAR)) + "-" +
                                String.format("%02d", calendarParam.get(Calendar.MONTH + 1)) + "-" +
                                String.format("%02d", calendarParam.get(Calendar.DAY_OF_MONTH)) + " " +
                                String.format("%02d", calendarParam.get(Calendar.HOUR_OF_DAY)) + ":" +
                                String.format("%02d", calendarParam.get(Calendar.MINUTE)) + ":" +
                                String.format("%02d", calendarParam.get(Calendar.SECOND));
        return calendarString;
    }

    /**
     *
     * @return
     */
    public int getCurrentTimeWindowIndex()
    {
        int timeWindow = 0;
        Calendar currentTimeCalendar = Calendar.getInstance();
        int currentTimeHour   = currentTimeCalendar.get(Calendar.HOUR_OF_DAY);

//        for (TimeWindow timewindow: timewindowArray) { if ( (currentTimeHour >= timewindow.getStartHour()) && (currentTimeHour <= timewindow.getEndHour()) )   { timeWindow = timewindow.getDescription(); } }
        for (int counter = 0; counter <= (timewindowArray.length - 1); counter++) { if ( (currentTimeHour >= timewindowArray[counter].getStartHour()) && (currentTimeHour <= timewindowArray[counter].getEndHour()) )   { timeWindow = counter; } }
        
        return timeWindow;
    }

    /**
     *
     * @return
     */
    public TimeWindow getCurrentTimeWindow()
    {
        return timewindowArray[getCurrentTimeWindowIndex()];
    }

    /**
     *
     * @param timewindowIndexParam
     * @return
     */
    public TimeWindow getTimeWindow(int timewindowIndexParam)
    {
        return timewindowArray[timewindowIndexParam];
    }
}
