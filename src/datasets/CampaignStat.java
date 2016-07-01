package datasets;

/**
 *
 * @author ron
 */
public class CampaignStat implements Cloneable
{
    private int campaignId;
    private int onAC;
    private int idleAC;
    private int connectingAC;
    private int connectingTT;
    private int tryingAC;
    private int tryingTT;
    private int callingAC;
    private int callingTT;
    private int ringingAC;
    private int ringingTT;
    private int acceptingAC;
    private int acceptingTT;
    private int talkingAC;
    private int talkingTT;
    private int localCancelTT;
    private int remoteCancelTT;
    private int localBusyTT;
    private int remoteBusyTT;
    private int localByeTT;
    private int remoteByeTT;

    /**
     *
     */
    public CampaignStat()
    {
	campaignId = 0;
	onAC = 0;
	idleAC = 0;
	connectingAC = 0;
	connectingTT = 0;
	tryingAC = 0;
	tryingTT = 0;
	callingAC = 0;
	callingTT = 0;
	ringingAC = 0;
	ringingTT = 0;
	acceptingAC = 0;
	acceptingTT = 0;
	talkingAC = 0;
	talkingTT = 0;
	localCancelTT = 0;
	remoteCancelTT = 0;
	localBusyTT = 0;
	remoteBusyTT = 0;
	localByeTT = 0;
	remoteByeTT = 0;
    }

    /**
     *
     * @param campaignIdParam
     * @param onACParam
     * @param idleACParam
     * @param connectingACParam
     * @param connectingTTParam
     * @param tryingACParam
     * @param tryingTTParam
     * @param callingACParam
     * @param callingTTParam
     * @param ringingACParam
     * @param ringingTTParam
     * @param acceptingACParam
     * @param acceptingTTParam
     * @param talkingACParam
     * @param talkingTTParam
     * @param localCancelTTParam
     * @param remoteCancelTTParam
     * @param localBusyTTParam
     * @param remoteBusyTTParam
     * @param localByeTTParam
     * @param remoteByeTTParam
     */
    public CampaignStat(
			    int campaignIdParam,
			    int onACParam,
			    int idleACParam,
			    int connectingACParam,
			    int connectingTTParam,
			    int tryingACParam,
			    int tryingTTParam,
			    int callingACParam,
			    int callingTTParam,
			    int ringingACParam,
			    int ringingTTParam,
			    int acceptingACParam,
			    int acceptingTTParam,
			    int talkingACParam,
			    int talkingTTParam,
			    int localCancelTTParam,
			    int remoteCancelTTParam,
			    int localBusyTTParam,
			    int remoteBusyTTParam,
			    int localByeTTParam,
			    int remoteByeTTParam
		    )
		    {
			    campaignId =    campaignIdParam;
			    onAC =          onACParam;
			    idleAC =        idleACParam;
			    connectingAC =  connectingACParam;
			    connectingTT =  connectingTTParam;
			    tryingAC =      tryingACParam;
			    tryingTT =      tryingTTParam;
			    callingAC =     callingACParam;
			    callingTT =     callingTTParam;
			    ringingAC =     ringingACParam;
			    ringingTT =     ringingTTParam;
			    acceptingAC =   acceptingACParam;
			    acceptingTT =   acceptingTTParam;
			    talkingAC =     talkingACParam;
			    talkingTT =     talkingTTParam;
			    localCancelTT = localCancelTTParam;
			    remoteCancelTT = remoteCancelTTParam;
			    localBusyTT =   localBusyTTParam;
			    remoteBusyTT =  remoteBusyTTParam;
			    localByeTT =    localByeTTParam;
			    remoteByeTT =   remoteByeTTParam;
		    }

    /**
     *
     * @return
     */
    public int	    getCampaignId()		{ return campaignId; }

    /**
     *
     * @return
     */
    public int	    getOnAC()			{ return onAC; }

    /**
     *
     * @return
     */
    public int	    getIdleAC()			{ return idleAC; }

    /**
     *
     * @return
     */
    public int	    getConnectingAC()		{ return connectingAC; }

    /**
     *
     * @return
     */
    public int	    getConnectingTT()		{ return connectingTT; }

    /**
     *
     * @return
     */
    public int	    getTryingAC()		{ return tryingAC; }

    /**
     *
     * @return
     */
    public int	    getTryingTT()		{ return tryingTT; }

    /**
     *
     * @return
     */
    public int	    getCallingAC()		{ return callingAC; }

    /**
     *
     * @return
     */
    public int	    getCallingTT()		{ return callingTT; }

    /**
     *
     * @return
     */
    public int	    getRingingAC()		{ return ringingAC; }

    /**
     *
     * @return
     */
    public int	    getRingingTT()		{ return ringingTT; }

    /**
     *
     * @return
     */
    public int	    getAcceptingAC()		{ return acceptingAC; }

    /**
     *
     * @return
     */
    public int	    getAcceptingTT()		{ return acceptingTT; }

    /**
     *
     * @return
     */
    public int	    getTalkingAC()		{ return talkingAC; }

    /**
     *
     * @return
     */
    public int	    getTalkingTT()		{ return talkingTT; }

    /**
     *
     * @return
     */
    public int	    getLocalCancelTT()		{ return localCancelTT; }

    /**
     *
     * @return
     */
    public int	    getRemoteCancelTT()		{ return remoteCancelTT; }

    /**
     *
     * @return
     */
    public int	    getLocalBusyTT()		{ return localBusyTT; }

    /**
     *
     * @return
     */
    public int	    getRemoteBusyTT()		{ return remoteBusyTT; }

    /**
     *
     * @return
     */
    public int	    getLocalByeTT()		{ return localByeTT; }

    /**
     *
     * @return
     */
    public int	    getRemoteByeTT()		{ return remoteByeTT; }

    /**
     *
     */
    public void	    addOnAC()			{ onAC++; }

    /**
     *
     */
    public void	    addIdleAC()			{ idleAC++; }

    /**
     *
     */
    public void	    addConnectingAC()		{ connectingAC++; }

    /**
     *
     */
    public void	    addConnectingTT()		{ connectingTT++; }

    /**
     *
     */
    public void	    addTryingAC()		{ tryingAC++; }

    /**
     *
     */
    public void	    addTryingTT()		{ tryingTT++; }

    /**
     *
     */
    public void	    addCallingAC()		{ callingAC++; }

    /**
     *
     */
    public void	    addCallingTT()		{ callingTT++; }

    /**
     *
     */
    public void	    addRingingAC()		{ ringingAC++; }

    /**
     *
     */
    public void	    addRingingTT()		{ ringingTT++; }

    /**
     *
     */
    public void	    addAcceptingAC()		{ acceptingAC++; }

    /**
     *
     */
    public void	    addAcceptingTT()		{ acceptingTT++; }

    /**
     *
     */
    public void	    addTalkingAC()		{ talkingAC++; }

    /**
     *
     */
    public void	    addTalkingTT()		{ talkingTT++; }

    /**
     *
     */
    public void	    addLocalCancelTT()		{ localCancelTT++; }

    /**
     *
     */
    public void	    addRemoteCancelTT()		{ remoteCancelTT++; }

    /**
     *
     */
    public void	    addLocalBusyTT()		{ localBusyTT++; }

    /**
     *
     */
    public void	    addRemoteBusyTT()		{ remoteBusyTT++; }

    /**
     *
     */
    public void	    addLocalByeTT()		{ localByeTT++; }

    /**
     *
     */
    public void	    addRemoteByeTT()		{ remoteByeTT++; }

    /**
     *
     */
    public void	    subOnAC()			{ onAC--; }

    /**
     *
     */
    public void	    subIdleAC()			{ idleAC--; }

    /**
     *
     */
    public void	    subConnectingAC()		{ if (connectingAC > 0) { connectingAC--; } }

    /**
     *
     */
    public void	    subConnectingTT()		{ connectingTT--; }

    /**
     *
     */
    public void	    subTryingAC()		{ tryingAC--; }

    /**
     *
     */
    public void	    subTryingTT()		{ tryingTT--; }

    /**
     *
     */
    public void	    subCallingAC()		{ callingAC--; }

    /**
     *
     */
    public void	    subCallingTT()		{ callingTT--; }

    /**
     *
     */
    public void	    subRingingAC()		{ ringingAC--; }

    /**
     *
     */
    public void	    subRingingTT()		{ ringingTT--; }

    /**
     *
     */
    public void	    subAcceptingAC()		{ acceptingAC--; }

    /**
     *
     */
    public void	    subAcceptingTT()		{ acceptingTT--; }

    /**
     *
     */
    public void	    subTalkingAC()		{ talkingAC--; }

    /**
     *
     */
    public void	    subTalkingTT()		{ talkingTT--; }

    /**
     *
     */
    public void	    subLocalCancelTT()		{ localCancelTT--; }

    /**
     *
     */
    public void	    subRemoteCancelTT()		{ remoteCancelTT--; }

    /**
     *
     */
    public void	    subLocalBusyTT()		{ localBusyTT--; }

    /**
     *
     */
    public void	    subRemoteBusyTT()		{ remoteBusyTT--; }

    /**
     *
     */
    public void	    subLocalByeTT()		{ localByeTT--; }

    /**
     *
     */
    public void	    subRemoteByeTT()		{ remoteByeTT--; }

    /**
     *
     * @param campaignIdParam
     */
    public void	    setCampaignId(int campaignIdParam)		{ campaignId = campaignIdParam; }

    /**
     *
     * @param onACParam
     */
    public void	    setOnAC(int onACParam)			{ onAC = onACParam; }

    /**
     *
     * @param idleACParam
     */
    public void	    setIdleAC(int idleACParam)			{ idleAC = idleACParam; }

    /**
     *
     * @param connectingACParam
     */
    public void	    setConnectingAC(int connectingACParam)	{ connectingAC = connectingACParam; }

    /**
     *
     * @param connectingTTParam
     */
    public void	    setConnectingTT(int connectingTTParam)	{ connectingTT = connectingTTParam; }

    /**
     *
     * @param tryingACParam
     */
    public void	    setTryingAC(int tryingACParam)		{ tryingAC = tryingACParam; }

    /**
     *
     * @param tryingTTParam
     */
    public void	    setTryingTT(int tryingTTParam)		{ tryingTT = tryingTTParam; }

    /**
     *
     * @param callingACParam
     */
    public void	    setCallingAC(int callingACParam)		{ callingAC = callingACParam; }

    /**
     *
     * @param callingTTParam
     */
    public void	    setCallingTT(int callingTTParam)		{ callingTT = callingTTParam; }

    /**
     *
     * @param ringingACParam
     */
    public void	    setRingingAC(int ringingACParam)		{ ringingAC = ringingACParam; }

    /**
     *
     * @param ringingTTParam
     */
    public void	    setRingingTT(int ringingTTParam)		{ ringingTT = ringingTTParam; }

    /**
     *
     * @param acceptingACParam
     */
    public void	    setAcceptingAC(int acceptingACParam)	{ acceptingAC = acceptingACParam; }

    /**
     *
     * @param acceptingTTParam
     */
    public void	    setAcceptingTT(int acceptingTTParam)	{ acceptingTT = acceptingTTParam; }

    /**
     *
     * @param talkingACParam
     */
    public void	    setTalkingAC(int talkingACParam)		{ talkingAC = talkingACParam; }

    /**
     *
     * @param talkingTTParam
     */
    public void	    setTalkingTT(int talkingTTParam)		{ talkingTT = talkingTTParam; }

    /**
     *
     * @param localCancelTTParam
     */
    public void	    setLocalCancelTT(int localCancelTTParam)	{ localCancelTT = localCancelTTParam; }

    /**
     *
     * @param remoteCancelTTParam
     */
    public void	    setRemoteCancelTT(int remoteCancelTTParam)	{ remoteCancelTT = remoteCancelTTParam; }

    /**
     *
     * @param localBusyTTParam
     */
    public void	    setLocalBusyTT(int localBusyTTParam)	{ localBusyTT = localBusyTTParam; }

    /**
     *
     * @param remoteBusyTTParam
     */
    public void	    setRemoteBusyTT(int remoteBusyTTParam)	{ remoteBusyTT = remoteBusyTTParam; }

    /**
     *
     * @param localByeTTParam
     */
    public void	    setLocalByeTT(int localByeTTParam)		{ localByeTT = localByeTTParam; }

    /**
     *
     * @param remoteByeTTParam
     */
    public void	    setRemoteByeTT(int remoteByeTTParam)	{ remoteByeTT = remoteByeTTParam; }

    /**
     *
     */
    public void     resetActiveCounts()
    {
	connectingAC = 0;
	tryingAC = 0;
	callingAC = 0;
	ringingAC = 0;
	acceptingAC = 0;
	talkingAC = 0;
    }

    /**
     *
     */
    public void     resetAll()
    {
	connectingAC = 0;
	connectingTT = 0;
	tryingAC = 0;
	tryingTT = 0;
	callingAC = 0;
	callingTT = 0;
	ringingAC = 0;
	ringingTT = 0;
	acceptingAC = 0;
	acceptingTT = 0;
	talkingAC = 0;
	talkingTT = 0;
	localCancelTT = 0;
	remoteCancelTT = 0;
	localBusyTT = 0;
	remoteBusyTT = 0;
	localByeTT = 0;
	remoteByeTT = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
