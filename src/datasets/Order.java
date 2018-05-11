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
public class Order
{
    private int		    orderId;
    private long	    orderTimestamp;
    private int		    customerId;
    private String          recipientsCategory;
//    private String          timeWindowCategory;
    private int             timeWindow0;
    private int             timeWindow1;
    private int             timeWindow2;
    private int             targetTransactionQuantity;
    private String          messageFilename;
    private int             messageDuration;
    private float           messageRatePerSecond;
    private float           messageRate;
    private float           subTotal;

    /**
     *
     */
    public Order()
    {
	orderId                                 = 0;
	orderTimestamp                          = 0;
	customerId                              = 0;
        recipientsCategory                      = "";
//        timeWindowCategory                      = "";
        timeWindow0                             = -1;
        timeWindow1                             = -1;
        timeWindow2                             = -1;
        targetTransactionQuantity               = 0;
        messageFilename                         = "";
        messageDuration                         = 0;
        messageRatePerSecond                    = 0;
        messageRate                             = 0;
        subTotal                                = 0;
    }

    /**
     *
     * @param orderIdParam
     * @param orderTimestampParam
     * @param customerIdParam
     * @param recipientsCategoryParam
     * @param timeWindow0Param
     * @param timeWindow1Param
     * @param timeWindow2Param
     * @param targetTransactionQuantityParam
     * @param messageFilenameParam
     * @param messageDurationParam
     * @param messageRatePerSecondParam
     * @param messageRateParam
     * @param subTotalParam
     */
    public Order(
		    int orderIdParam,
		    long orderTimestampParam,
		    int customerIdParam,
		    String recipientsCategoryParam,
//		    String timeWindowCategoryParam,
		    int timeWindow0Param,
		    int timeWindow1Param,
		    int timeWindow2Param,
		    int targetTransactionQuantityParam,
		    String messageFilenameParam,
		    int messageDurationParam,
		    float messageRatePerSecondParam,
		    float messageRateParam,
		    float subTotalParam
		)
		{
		    orderId                     = orderIdParam;
		    orderTimestamp              = orderTimestampParam;
		    customerId                  = customerIdParam;
		    recipientsCategory          = recipientsCategoryParam;
//		    timeWindowCategory          = timeWindowCategoryParam;
		    timeWindow0                 = timeWindow0Param;
		    timeWindow1                 = timeWindow1Param;
		    timeWindow2                 = timeWindow2Param;
		    targetTransactionQuantity   = targetTransactionQuantityParam;
		    messageFilename             = messageFilenameParam;
		    messageDuration             = messageDurationParam;
		    messageRatePerSecond        = messageRatePerSecondParam;
		    messageRate                 = messageRateParam;
		    subTotal                    = subTotalParam;
		}

    /**
     *
     * @return
     */
    public int	    getOrderId()                                                        { return orderId; }

    /**
     *
     * @return
     */
    public long     getOrderTimestamp()                                                 { return orderTimestamp; }

    /**
     *
     * @return
     */
    public int	    getCustomerId()                                                     { return customerId; }

    /**
     *
     * @return
     */
    public String   getRecipientsCategory()                                             { return recipientsCategory; }
//    public String   getTimeWindowCategory()                                             { return timeWindowCategory; }

    /**
     *
     * @return
     */
        public int      getTimeWindow0()                                                    { return timeWindow0; }

    /**
     *
     * @return
     */
    public int      getTimeWindow1()                                                    { return timeWindow1; }

    /**
     *
     * @return
     */
    public int      getTimeWindow2()                                                    { return timeWindow2; }

    /**
     *
     * @return
     */
    public int      getTargetTransactionQuantity()                                      { return targetTransactionQuantity; }

    /**
     *
     * @return
     */
    public String   getMessageFilename()                                                { return messageFilename; }

    /**
     *
     * @return
     */
    public int      getMessageDuration()                                                { return messageDuration; }

    /**
     *
     * @return
     */
    public float    getMessageRatePerSecond()                                           { return messageRatePerSecond; }

    /**
     *
     * @return
     */
    public float    getMessageRate()                                                    { return messageRate; }

    /**
     *
     * @return
     */
    public float    getSubTotal()                                                       { return subTotal; }

    /**
     *
     * @return
     */
    public int[]    getTimewindowIndexArray()
    {
        // Order has 3 static timewindow integers, but the timewindowList only wants the set (above -1) value(s) in an array, so it's a conversion from 3 static integers to a possibly smaller int[] array like indicesArray
        int counter = 0; if (getTimeWindow0() >= 0) { counter++;} if (getTimeWindow1() >= 0) { counter++;} if (getTimeWindow2() >= 0) { counter++;}
        int [] indicesArray = new int[counter];

        counter = 0;
        if (getTimeWindow0() >= 0) { indicesArray[counter++] = getTimeWindow0();}
        if (getTimeWindow1() >= 0) { indicesArray[counter++] = getTimeWindow1();}
        if (getTimeWindow2() >= 0) { indicesArray[counter++] = getTimeWindow2();}
        return indicesArray;
    }

    /**
     *
     * @param indicesParam
     * @return
     */
    public int[]    getIndices2Static(int[] indicesParam) // int[<=3] > static int[3]
    {
        int [] timewindowIndexArray = new int[3]; timewindowIndexArray[0] = -1; timewindowIndexArray[1] = -1; timewindowIndexArray[2] = -1;
        int counter = 0;
        for (int index:indicesParam) { timewindowIndexArray[counter++] = index; }

        return timewindowIndexArray;
    }

    /**
     *
     * @param orderIdParam
     */
    public void	    setOrderId(int orderIdParam)                                        { orderId = orderIdParam; }

    /**
     *
     * @param orderTimestampParam
     */
    public void     setOrderTimestamp(long orderTimestampParam)                         { orderTimestamp = orderTimestampParam; }

    /**
     *
     * @param customerIdParam
     */
    public void	    setCustomerId(int customerIdParam)                                  { customerId = customerIdParam; }

    /**
     *
     * @param recipientsCategoryParam
     */
    public void     setRecipientsCategory(String recipientsCategoryParam)               { recipientsCategory = recipientsCategoryParam ;}
//    public void     setTimeWindowCategory(String timeWindowCategoryParam)               { timeWindowCategory = timeWindowCategoryParam ;}

    /**
     *
     * @param timeWindow0Param
     */
        public void     setTimeWindow0(int timeWindow0Param)                                { timeWindow0 = timeWindow0Param ;}

    /**
     *
     * @param timeWindow1Param
     */
    public void     setTimeWindow1(int timeWindow1Param)                                { timeWindow1 = timeWindow1Param ;}

    /**
     *
     * @param timeWindow2Param
     */
    public void     setTimeWindow2(int timeWindow2Param)                                { timeWindow2 = timeWindow2Param ;}

    /**
     *
     * @param targetTransactionQuantityParam
     */
    public void     setTargetTransactionQuantity(int targetTransactionQuantityParam)	{ targetTransactionQuantity = targetTransactionQuantityParam ;}

    /**
     *
     * @param messageFilenameParam
     */
    public void     setMessageFilename(String messageFilenameParam)			{ messageFilename = messageFilenameParam ;}

    /**
     *
     * @param messageDurationParam
     */
    public void     setMessageDuration(int messageDurationParam)                        { messageDuration = messageDurationParam ;}

    /**
     *
     * @param messageRatePerSecondParam
     */
    public void     setMessageRatePerSecond(float messageRatePerSecondParam)            { messageRatePerSecond = messageRatePerSecondParam ;}

    /**
     *
     * @param messageRateParam
     */
    public void     setMessageRate(float messageRateParam)                              { messageRate = messageRateParam ;}

    /**
     *
     * @param subTotalParam
     */
    public void     setSubTotal(float subTotalParam)                                    { subTotal = subTotalParam ;}
}
