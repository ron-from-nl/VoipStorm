package data;

import java.awt.Color;

/**
 *
 * @author ron
 */
public class DisplayData
{

    private String softphoneInfoCell;
    private String proxyInfoCell;
    private String primaryStatusCell;
    private String primaryStatusDetailsCell;
    private String secondayStatusCell;
    private String secondayStatusDetailsCell;
    private boolean onFlag;
    private boolean idleFlag;
    private boolean connectFlag;
    private boolean callingFlag;
    private boolean ringingFlag;
    private boolean acceptFlag;
    private boolean talkingFlag;
    private boolean registeredFlag;
    private boolean answerFlag;
    private boolean cancelFlag;
    private boolean muteFlag;

    /**
     *
     */
    public  final Color INACTIVECOLOR;

    /**
     *
     */
    public  final Color OFFCOLOR;

    /**
     *
     */
    public  final Color ONCOLOR;

    /**
     *
     */
    public  final Color IDLECOLOR;

    /**
     *
     */
    public  final Color CONNECTCOLOR;

    /**
     *
     */
    public  final Color CALLINGCOLOR;

    /**
     *
     */
    public  final Color ACCEPTCOLOR;

    /**
     *
     */
    public  final Color RINGINGCOLOR;

    /**
     *
     */
    public  final Color TALKINGCOLOR;

    /**
     *
     */
    public  final Color REGISTEREDCOLOR;

    /**
     *
     */
    public  final Color ANSWERCOLOR;

    /**
     *
     */
    public  final Color CANCELCOLOR;

    /**
     *
     */
    public  final Color MUTECOLOR;

    /**
     *
     */
    public DisplayData()
    {
        softphoneInfoCell           = "";
        proxyInfoCell               = "";
        primaryStatusCell           = "";
        primaryStatusDetailsCell    = "";
        secondayStatusCell          = "";
        secondayStatusDetailsCell   = "";
        onFlag                      = false;
        idleFlag                    = false;
        connectFlag                 = false;
        callingFlag                 = false;
        ringingFlag                 = false;
        acceptFlag                  = false;
        talkingFlag                 = false;
        registeredFlag              = false;
        answerFlag                  = false;
        cancelFlag                  = false;
        muteFlag                    = false;

        INACTIVECOLOR               = Color.LIGHT_GRAY;
        OFFCOLOR                    = Color.WHITE;
        ONCOLOR                     = Color.LIGHT_GRAY;
        IDLECOLOR                   = Color.DARK_GRAY;
        CONNECTCOLOR                = Color.RED;
        CALLINGCOLOR                = Color.ORANGE;
        ACCEPTCOLOR                 = Color.ORANGE;
        RINGINGCOLOR                = Color.PINK;
        TALKINGCOLOR                = Color.GREEN;
        REGISTEREDCOLOR             = Color.GREEN;
        ANSWERCOLOR                 = Color.ORANGE;
        CANCELCOLOR                 = Color.RED;
        MUTECOLOR                   = Color.RED;        
    }

    /**
     *
     * @return
     */
    public String getSoftphoneInfoCell()            { return softphoneInfoCell;}

    /**
     *
     * @return
     */
    public String getProxyInfoCell()                { return proxyInfoCell;}

    /**
     *
     * @return
     */
    public String getPrimaryStatusCell()            { return primaryStatusCell;}

    /**
     *
     * @return
     */
    public String getPrimaryStatusDetailsCell()     { return primaryStatusDetailsCell;}

    /**
     *
     * @return
     */
    public String getSecondaryStatusCell()          { return secondayStatusCell;}

    /**
     *
     * @return
     */
    public String getSecondaryStatusDetailsCell()   { return secondayStatusDetailsCell;}

    /**
     *
     * @return
     */
    public boolean getOnFlag()          { return onFlag;}

    /**
     *
     * @return
     */
    public boolean getIdleFlag()        { return idleFlag;}

    /**
     *
     * @return
     */
    public boolean getConnectFlag()     { return connectFlag;}

    /**
     *
     * @return
     */
    public boolean getCallingFlag()     { return callingFlag;}

    /**
     *
     * @return
     */
    public boolean getRingingFlag()     { return ringingFlag;}

    /**
     *
     * @return
     */
    public boolean getAcceptFlag()      { return acceptFlag;}

    /**
     *
     * @return
     */
    public boolean getTalkingFlag()     { return talkingFlag;}

    /**
     *
     * @return
     */
    public boolean getRegisteredFlag()  { return registeredFlag;}

    /**
     *
     * @return
     */
    public boolean getAnswerFlag()      { return answerFlag;}

    /**
     *
     * @return
     */
    public boolean getCancelFlag()      { return cancelFlag;}

    /**
     *
     * @return
     */
    public boolean getMuteFlag()        { return muteFlag;}

    /**
     *
     * @param softphoneInfoCellParam
     */
    public void setSoftphoneInfoCell(String softphoneInfoCellParam) { softphoneInfoCell = softphoneInfoCellParam;}

    /**
     *
     * @param proxyInfoCellParam
     */
    public void setProxyInfoCell(String proxyInfoCellParam) { proxyInfoCell = proxyInfoCellParam ;}

    /**
     *
     * @param softphoneStatusCellParam
     */
    public void setPrimaryStatusCell(String softphoneStatusCellParam) { primaryStatusCell = softphoneStatusCellParam ;}

    /**
     *
     * @param softphoneStatusDetailsCellParam
     */
    public void setPrimaryStatusDetailsCell(String softphoneStatusDetailsCellParam) { primaryStatusDetailsCell = softphoneStatusDetailsCellParam ;}

    /**
     *
     * @param peerStatusCellParam
     */
    public void setSecondaryStatusCell(String peerStatusCellParam) { secondayStatusCell = peerStatusCellParam ;}

    /**
     *
     * @param peerStatusDetailsCellParam
     */
    public void setSecondaryStatusDetailsCell(String peerStatusDetailsCellParam) { secondayStatusDetailsCell = peerStatusDetailsCellParam ;}

    /**
     *
     * @param onCellFlagParam
     */
    public void setOnFlag(boolean onCellFlagParam) { onFlag = onCellFlagParam ;}

    /**
     *
     * @param idleCellFlagParam
     */
    public void setIdleFlag(boolean idleCellFlagParam) { idleFlag = idleCellFlagParam ;}

    /**
     *
     * @param connectCellFlagParam
     */
    public void setConnectFlag(boolean connectCellFlagParam) { connectFlag = connectCellFlagParam ;}

    /**
     *
     * @param callingCellFlagParam
     */
    public void setCallingFlag(boolean callingCellFlagParam) { callingFlag = callingCellFlagParam ;}

    /**
     *
     * @param ringingCellFlagParam
     */
    public void setRingingFlag(boolean ringingCellFlagParam) { ringingFlag = ringingCellFlagParam ;}

    /**
     *
     * @param acceptCellFlagParam
     */
    public void setAcceptFlag(boolean acceptCellFlagParam) { acceptFlag = acceptCellFlagParam ;}

    /**
     *
     * @param talkingCellFlagParam
     */
    public void setTalkingFlag(boolean talkingCellFlagParam) { talkingFlag = talkingCellFlagParam ;}

    /**
     *
     * @param registeredCellFlagParam
     */
    public void setRegisteredFlag(boolean registeredCellFlagParam) { registeredFlag = registeredCellFlagParam ;}

    /**
     *
     * @param answerCellFlagParam
     */
    public void setAnswerFlag(boolean answerCellFlagParam) { answerFlag = answerCellFlagParam ;}

    /**
     *
     * @param cancelCellFlagParam
     */
    public void setCancelFlag(boolean cancelCellFlagParam) { cancelFlag = cancelCellFlagParam ;}

    /**
     *
     * @param muteCellFlagParam
     */
    public void setMuteFlag(boolean muteCellFlagParam) { muteFlag = muteCellFlagParam ;}

    /**
     *
     */
    public void resetSip()
    {
        setIdleFlag(false);
        setConnectFlag(false);
        setCallingFlag(false);
        setRingingFlag(false);
        setAcceptFlag(false);
        setTalkingFlag(false);
    }
}
