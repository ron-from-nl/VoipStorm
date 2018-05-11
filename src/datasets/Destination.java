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
public class Destination implements Cloneable
{
    private long id;
    private int campaignId;
    private int destinationCount;
    private String destination;
    private long connectingTimestamp;
    private long tryingTimestamp;
    private long callingTimestamp;
    private int  callingAttempts;
    private long ringingTimestamp;
    private long localCancelingTimestamp;
    private long remoteCancelingTimestamp;
    private long localBusyTimestamp;
    private long remoteBusyTimestamp;
    private long acceptingTimestamp;
    private long talkingTimestamp;
    private long localByeTimestamp;
    private long remoteByeTimestamp;
    private long responseStatusCode;
    private String responseStatusDesc;

    /**
     *
     */
    public Destination()
    {
	id			    = 0;
	campaignId		    = 0;
	destinationCount	    = 0;
	destination		    = new String("Undefined");
	connectingTimestamp	    = 0;
	tryingTimestamp	            = 0;
	callingTimestamp	    = 0;
	callingAttempts             = 0;
	ringingTimestamp	    = 0;
	localCancelingTimestamp	    = 0;
	remoteCancelingTimestamp    = 0;
	localBusyTimestamp	    = 0;
	remoteBusyTimestamp	    = 0;
	acceptingTimestamp	    = 0;
	talkingTimestamp	    = 0;
	localByeTimestamp	    = 0;
	remoteByeTimestamp	    = 0;
	responseStatusCode	    = 0;
	responseStatusDesc	    = "";
    }

    /**
     *
     * @param idParam
     * @param campaignIdParam
     * @param destinationCountParam
     * @param destinationParam
     * @param connectingTimestampParam
     * @param tryingTimestampParam
     * @param callingTimestampParam
     * @param callingAttemptsParam
     * @param ringingTimestampParam
     * @param localCancelingTimestampParam
     * @param remoteCancelingTimestampParam
     * @param localBusyTimestampParam
     * @param remoteBusyTimestampParam
     * @param acceptingTimestampParam
     * @param talkingTimestampParam
     * @param localByeTimestampParam
     * @param remoteByeTimestampParam
     * @param responseStatusCodeParam
     * @param responseStatusDescParam
     */
    public Destination(
			long idParam,
			int campaignIdParam,
			int destinationCountParam,
			String destinationParam,
			long connectingTimestampParam,
			long tryingTimestampParam,
			long callingTimestampParam,
			int  callingAttemptsParam,
			long ringingTimestampParam,
			long localCancelingTimestampParam,
			long remoteCancelingTimestampParam,
			long localBusyTimestampParam,
			long remoteBusyTimestampParam,
			long acceptingTimestampParam,
			long talkingTimestampParam,
			long localByeTimestampParam,
			long remoteByeTimestampParam,
			long responseStatusCodeParam,
			String responseStatusDescParam
		    )
		    {
			id                          = idParam;
			campaignId                  = campaignIdParam;
			destinationCount            = destinationCountParam;
			destination                 = destinationParam;
			connectingTimestamp         = connectingTimestampParam;
			tryingTimestamp             = tryingTimestampParam;
			callingTimestamp            = callingTimestampParam;
			callingAttempts             = callingAttemptsParam;
			ringingTimestamp            = ringingTimestampParam;
			localCancelingTimestamp     = localCancelingTimestampParam;
			remoteCancelingTimestamp    = remoteCancelingTimestampParam;
			localBusyTimestamp          = localBusyTimestampParam;
			remoteBusyTimestamp         = remoteBusyTimestampParam;
			acceptingTimestamp          = acceptingTimestampParam;
			talkingTimestamp            = talkingTimestampParam;
			localByeTimestamp           = localByeTimestampParam;
			remoteByeTimestamp          = remoteByeTimestampParam;
			responseStatusCode          = responseStatusCodeParam;
			responseStatusDesc          = responseStatusDescParam;
		    }

    /**
     *
     * @return
     */
    public long	    getId()					    { return id;}

    /**
     *
     * @return
     */
    public int	    getCampaignId()				    { return campaignId;}

    /**
     *
     * @return
     */
    public int	    getDestinationCount()			    { return destinationCount;}

    /**
     *
     * @return
     */
    public String   getDestination()				    { return destination;}

    /**
     *
     * @return
     */
    public long	    getConnectingTimestamp()			    { return connectingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getTryingTimestamp()			    { return tryingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getCallingTimestamp()			    { return callingTimestamp;}

    /**
     *
     * @return
     */
    public int	    getCallingAttempts()			    { return callingAttempts;}

    /**
     *
     * @return
     */
    public long	    getRingingTimestamp()			    { return ringingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getLocalCancelingTimestamp()		    { return localCancelingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getRemoteCancelingTimestamp()		    { return remoteCancelingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getLocalBusyTimestamp()			    { return localBusyTimestamp;}

    /**
     *
     * @return
     */
    public long	    getRemoteBusyTimestamp()			    { return remoteBusyTimestamp;}

    /**
     *
     * @return
     */
    public long	    getAcceptingTimestamp()			    { return acceptingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getTalkingTimestamp()			    { return talkingTimestamp;}

    /**
     *
     * @return
     */
    public long	    getLocalByeTimestamp()			    { return localByeTimestamp;}

    /**
     *
     * @return
     */
    public long	    getRemoteByeTimestamp()			    { return remoteByeTimestamp;}

    /**
     *
     * @return
     */
    public long	    getResponseStatusCode()			    { return responseStatusCode;}

    /**
     *
     * @return
     */
    public String   getResponseStatusDesc()			    { return responseStatusDesc;}

    /**
     *
     * @param idParam
     */
    public void	    setId(long idParam)						    { id = idParam; }

    /**
     *
     * @param campaignIdParam
     */
    public void	    setCampaignId(int campaignIdParam)				    { campaignId = campaignIdParam; }

    /**
     *
     * @param destinationCountParam
     */
    public void	    setDestinationCount(int destinationCountParam)		    { destinationCount = destinationCountParam; }

    /**
     *
     * @param destinationParam
     */
    public void     setDestination(String destinationParam)			    { destination = destinationParam; }

    /**
     *
     * @param connectingTimestampParam
     */
    public void	    setConnectingTimestamp(long connectingTimestampParam)	    { connectingTimestamp = connectingTimestampParam; }

    /**
     *
     * @param tryingTimestampParam
     */
    public void	    setTryingTimestamp(long tryingTimestampParam)	            { tryingTimestamp = tryingTimestampParam; }

    /**
     *
     * @param callingTimestampParam
     */
    public void	    setCallingTimestamp(long callingTimestampParam)		    { callingTimestamp = callingTimestampParam; }

    /**
     *
     * @param callingAttemptsParam
     */
    public void	    setCallingAttempts(int callingAttemptsParam)		    { callingAttempts = callingAttemptsParam; }

    /**
     *
     */
    public void	    addCallingAttempts()                                            { callingAttempts++; }

    /**
     *
     * @param ringingTimestampParam
     */
    public void	    setRingingTimestamp(long ringingTimestampParam)		    { ringingTimestamp = ringingTimestampParam; }

    /**
     *
     * @param localCancelingTimestampParam
     */
    public void	    setLocalCancelingTimestamp(long localCancelingTimestampParam)   { localCancelingTimestamp = localCancelingTimestampParam; }

    /**
     *
     * @param remoteCancelingTimestampParam
     */
    public void	    setRemoteCancelingTimestamp(long remoteCancelingTimestampParam) { remoteCancelingTimestamp = remoteCancelingTimestampParam; }

    /**
     *
     * @param localBusyTimestampParam
     */
    public void	    setLocalBusyTimestamp(long localBusyTimestampParam)		    { localBusyTimestamp = localBusyTimestampParam; }

    /**
     *
     * @param remoteBusyTimestampParam
     */
    public void	    setRemoteBusyTimestamp(long remoteBusyTimestampParam)	    { remoteBusyTimestamp = remoteBusyTimestampParam; }

    /**
     *
     * @param acceptingTimestampParam
     */
    public void	    setAcceptingTimestamp(long acceptingTimestampParam)		    { acceptingTimestamp = acceptingTimestampParam; }

    /**
     *
     * @param talkingTimestampParam
     */
    public void	    setTalkingTimestamp(long talkingTimestampParam)		    { talkingTimestamp = talkingTimestampParam; }

    /**
     *
     * @param localByeTimestampParam
     */
    public void	    setLocalByeTimestamp(long localByeTimestampParam)		    { localByeTimestamp = localByeTimestampParam; }

    /**
     *
     * @param remoteByeTimestampParam
     */
    public void	    setRemoteByeTimestamp(long remoteByeTimestampParam)		    { remoteByeTimestamp = remoteByeTimestampParam; }

    /**
     *
     * @param badResponseStatusCodeParam
     */
    public void	    setResponseStatusCode(long badResponseStatusCodeParam)	    { responseStatusCode = badResponseStatusCodeParam; }

    /**
     *
     * @param badResponseStatusDescParam
     */
    public void	    setResponseStatusDesc(String badResponseStatusDescParam)	    { responseStatusDesc = badResponseStatusDescParam; }

    /**
     *
     */
    public void     resetTimestamps()
    {
	connectingTimestamp	    = 0;
	tryingTimestamp	            = 0;
	callingTimestamp	    = 0;
//	callingAttempts             = 0;
	ringingTimestamp	    = 0;
	localCancelingTimestamp	    = 0;
	remoteCancelingTimestamp    = 0;
	localBusyTimestamp	    = 0;
	remoteBusyTimestamp	    = 0;
	acceptingTimestamp	    = 0;
	talkingTimestamp	    = 0;
	localByeTimestamp	    = 0;
	remoteByeTimestamp	    = 0;
    }

    @Override
    public String   toString()
    {
        String info = new String("");
	info += "id: " + id + "\n";
	info += "campaignId: " + campaignId + "\n";
	info += "destinationCount: " + destinationCount + "\n";
	info += "destination: " + destination+ "\n";
	info += "connectingTimestamp: " + connectingTimestamp + "\n";
	info += "tryingTimestamp: " + tryingTimestamp + "\n";
	info += "callingTimestamp: " + callingTimestamp + "\n";
	info += "callingAttempts: " + callingAttempts + "\n";
	info += "ringingTimestamp: " + ringingTimestamp + "\n";
	info += "localCancelingTimestamp: " + localCancelingTimestamp + "\n";
	info += "remoteCancelingTimestamp: " + remoteCancelingTimestamp + "\n";
	info += "localBusyTimestamp: " + localBusyTimestamp + "\n";
	info += "remoteBusyTimestamp: " + remoteBusyTimestamp + "\n";
	info += "acceptingTimestamp: " + acceptingTimestamp + "\n";
	info += "talkingTimestamp: " + talkingTimestamp + "\n";
	info += "localByeTimestamp: " + localByeTimestamp + "\n";
	info += "remoteByeTimestamp: " + remoteByeTimestamp + "\n";
	info += "responseStatusCode: " + responseStatusCode + "\n";
	info += "responseStatusDesc: " + responseStatusDesc + "\n";
        return info;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
