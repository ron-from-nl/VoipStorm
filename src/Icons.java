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

import javax.swing.ImageIcon;

/**
 *
 * @author ron
 */
public class Icons
{
//    private String headerIconFile;
//    private String emptyIconFile;
//    private String offIconFile;
//    private String onIconFile;
//    private String idleIconFile;
//    private String registeredIconFile;
//    private String idleRegisteredIconFile;
//    private String connectIconFile;
//    private String callIconFile;
//    private String ringIconFile;
//    private String acceptIconFile;
//    private String talkIconFile;
//    private String localcancelIconFile;
//    private String remotecancelIconFile;
//    private String localbusyIconFile;
//    private String remotebusyIconFile;
//    private String localbyeIconFile;
//    private String remotebyeIconFile;
//    private String errorIconFile;
//    private String processIconFile;

    private ImageIcon headerIcon;
    private ImageIcon emptyIcon;
    private ImageIcon offIcon;
    private ImageIcon onIcon;
    private ImageIcon idleIcon;
    private ImageIcon registeredIcon;
    private ImageIcon idleRegisteredIcon;
    private ImageIcon connectingIcon;
    private ImageIcon tryingIcon;
    private ImageIcon callIcon;
    private ImageIcon ringIcon;
    private ImageIcon acceptIcon;
    private ImageIcon talkIcon;
    private ImageIcon localCancelIcon;
    private ImageIcon remoteCancelIcon;
    private ImageIcon localBusyIcon;
    private ImageIcon remoteBusyIcon;
    private ImageIcon localByeIcon;
    private ImageIcon remoteByeIcon;
    private ImageIcon errorIcon;
    private ImageIcon processIcon;
    private int defaultWidth;
    private int defaultHeight;

	// ⃘ ⚭ ⃠ ∅ ⊗ ⊝ ⌽ ⚮ ⧬ ⧭ ℗ ✹ ✱ ❋ ✺ ⹿
	// ░  ☑ ☒ ⨱ ⍰ ⍐ ⍗  ▢ ◇ ☍ ⬚
	// ⤫ ✗ ✕ 〷 ⌘ ⎌ ⨯
	// ✁ ✃ ✂ ✄ ⌛ ☕ ♨ ⚐ ⚑ ⧥ ☠  ⎗ ⎘ ✌ ⧌ ⟁ ¹ ² ☂ ☁ ☙ ⸛ ✌ ⸟ ⧞  ˟ ῁ ⤦ ⸕   ⎗ ⎘ ☮ ✉ ࿃ Ꙭ ꙭ Ꝏ ꝏ
	// ☏ ☎ ✆  ⌚ ⨶ ⍾ ⎋ ☄ ◎ ⚒ ⏏ 
	// Ⓐ Ⓑ Ⓒ Ⓔ Ⓓ Ⓔ Ⓕ Ⓖ Ⓗ Ⓘ Ⓙ Ⓚ Ⓛ Ⓜ Ⓝ Ⓞ Ⓟ Ⓠ Ⓡ Ⓢ Ⓣ Ⓤ Ⓥ Ⓦ Ⓧ Ⓨ Ⓩ ␛ ␦ ® ␇ ␅ ␖ ␘ ☡ ﹟ ？ ⌗ ␛ ♯ ⸮ ❢ ℕ № ‼ ➊ ➁ ➀ ➋ ₠ ¼ ½ ¾  ₠
	// ☊ ↻ ⃔ ↧ ↥ ↗ ≺ ≪ ⏎ ◁ ← ↖ ↙ ↰ ↲ ↵ ⇖ ⇙ ⇐ ⇠ ⇣ ⇦ ➘ ⟀ ⥂ ⥃ ⥳ ⥴ ⬉ ⬋ ⬑ ⬐ ￩ ⇇ ⇜ ⇤ ⤽ ⤼ ♐ ⥇  ↑ ↓ ↟ ↡ ↥ ↧ ⇑ ⇓ ⇗ ⇘ ⇞ ⇟ ⇡ ⇣ ⇈ ⇊ ⇪ ⇶ ⍐ ⍇ ⍈ ⍗  
        // ↆ ⇪ ⊻ ⊼ ⏏ ▲ △ ▵ ▼ ▽ ⬆ ⬇ ⇡
	// ♫ ♪ ♩
	// ℡ ℡ ⫶ ⋰ ⋱ ⋯ ⁚ ♒ ⧣ ⧤ ⩶ ⎓ ⑊ ⍼ ⍭ ⍲  ̷
        // ■ ▣ █  ⅓ ¼ ½ ¾ ‰ ‱
        // ‣ • ‖ ⌨ ⎆ ⎗ ⎘ ⎙ ► ▸ ✈  Ⓜ Ⓢ ⎮ ❙❙ ⚭ ⚮ ❒

    private final String offChar = "";
    private final String onChar= "⎋";
    private final String idleChar= "☏";
    private final String registeredChar= "⌨";
    private String idleRegisteredChar= "☏";
    private final String connectingChar= "⚡";
    private final String tryingChar= "☍";
    private final String callChar= "";
    private final String ringChar= "♩";
    private final String acceptChar= "☑";
    private final String talkChar= "☎";
    private final String localCancelChar= "␛";
    private final String remoteCancelChar= "␛";
    private final String localBusyChar= "✂";
    private final String remoteBusyChar= "✂";
    private final String localByeChar= "➊";
    private final String remoteByeChar= "➁";
    private final String errorChar= "⚠";
    private final String actionChar= "✆";
    private final String upChar= "⇡";
    private final String downChar= "⇣";
    private final String resizeUpChar= "⊼";
    private final String resizeDownChar= "⊻";
    private final String concurrentChar= "=";
    private final String sumChar= "+";
    private final String processChar= "№";

    private final String headerIconFile = "/icons/header/header.jpg";
    private final String emptyIconFile = "/icons/empty/emptyIcon.jpg";
    private final String offIconFile = "/icons/off/offIcon1.jpg";
    private final String onIconFile = "/icons/on/onIcon1.jpg";
    private final String idleIconFile = "/icons/idle/idleIcon4.jpg";
    private final String registeredIconFile = "/icons/registered/registeredIcon4.jpg";
    private final String idleRegisteredIconFile = "/icons/idle/idleIcon5.jpg";
    private final String connectingIconFile = "/icons/connecting/connectingIcon1.jpg";
    private final String tryingIconFile = "/icons/trying/tryingIcon1.jpg";
    private final String callIconFile = "/icons/call/callIcon4Black.jpg";
    private final String ringIconFile = "/icons/ring/ringIcon5Black.jpg";
    private final String acceptIconFile = "/icons/accept/acceptIcon1.jpg";
    private final String talkIconFile = "/icons/talk/talkIcon2Green.jpg";
    private final String localCancelIconFile = "/icons/cancel/cancelIcon1Red.jpg";
    private final String remoteCancelIconFile = "/icons/cancel/cancelIcon1Grey.jpg";
    private final String localBusyIconFile = "/icons/busy/busyIcon2Red.jpg";
    private final String remoteBusyIconFile = "/icons/busy/busyIcon2Red.jpg";
    private final String localByeIconFile = "/icons/bye/byeIcon2Green.jpg";
    private final String remoteByeIconFile = "/icons/bye/byeIcon2Grey.jpg";
    private final String errorIconFile = "/icons/error/errorIcon1.jpg";
    private final String processIconFile = "/icons/process/processIcon1.jpg";

    private boolean iconsWanted;

//    private int PHONESPOOLTABLECOLUMNWIDTH = 26;
//    private int PHONESPOOLTABLECOLUMNHEIGHT = 16;

    Icons(int widthParam, int heightParam, boolean iconsParam)
    {
        iconsWanted = iconsParam;
//        Image scaledImaged;
        defaultWidth = widthParam;
        defaultHeight = heightParam;
        
//        headerIcon = new ImageIcon(getClass().getResource(offIconFile)); scaledImaged = headerIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); headerIcon.setImage(scaledImaged);
//        emptyIcon = new ImageIcon(getClass().getResource(emptyIconFile)); scaledImaged = emptyIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); headerIcon.setImage(scaledImaged);
//        offIcon = new ImageIcon(getClass().getResource(offIconFile)); scaledImaged = offIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); offIcon.setImage(scaledImaged);
//        onIcon = new ImageIcon(getClass().getResource(onIconFile)); scaledImaged = onIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); onIcon.setImage(scaledImaged);
//        idleIcon = new ImageIcon(getClass().getResource(idleIconFile)); scaledImaged = idleIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); idleIcon.setImage(scaledImaged);
//        registeredIcon = new ImageIcon(getClass().getResource(registeredIconFile)); scaledImaged = registeredIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); registeredIcon.setImage(scaledImaged);
//        idleRegisteredIcon = new ImageIcon(getClass().getResource(idleIconFile)); scaledImaged = idleRegisteredIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); idleRegisteredIcon.setImage(scaledImaged);
//        connectIcon = new ImageIcon(getClass().getResource(connectIconFile)); scaledImaged = connectIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); connectIcon.setImage(scaledImaged);
//        callIcon = new ImageIcon(getClass().getResource(callIconFile)); scaledImaged = callIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); callIcon.setImage(scaledImaged);
//        ringIcon = new ImageIcon(getClass().getResource(ringIconFile)); scaledImaged = ringIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); ringIcon.setImage(scaledImaged);
//        acceptIcon = new ImageIcon(getClass().getResource(acceptIconFile)); scaledImaged = acceptIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); acceptIcon.setImage(scaledImaged);
//        talkIcon = new ImageIcon(getClass().getResource(talkIconFile)); scaledImaged = talkIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); talkIcon.setImage(scaledImaged);
//        localCancelIcon = new ImageIcon(getClass().getResource(localCancelIconFile)); scaledImaged = localCancelIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); localCancelIcon.setImage(scaledImaged);
//        remoteCancelIcon = new ImageIcon(getClass().getResource(remoteCancelIconFile)); scaledImaged = remoteCancelIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); remoteCancelIcon.setImage(scaledImaged);
//        localBusyIcon = new ImageIcon(getClass().getResource(localBusyIconFile)); scaledImaged = localBusyIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); localBusyIcon.setImage(scaledImaged);
//        remoteBusyIcon = new ImageIcon(getClass().getResource(remoteBusyIconFile)); scaledImaged = remoteBusyIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); remoteBusyIcon.setImage(scaledImaged);
//        localByeIcon = new ImageIcon(getClass().getResource(localByeIconFile)); scaledImaged = localByeIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); localByeIcon.setImage(scaledImaged);
//        remoteByeIcon = new ImageIcon(getClass().getResource(remoteByeIconFile)); scaledImaged = remoteByeIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); remoteByeIcon.setImage(scaledImaged);
//        errorIcon = new ImageIcon(getClass().getResource(errorIconFile)); scaledImaged = errorIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); errorIcon.setImage(scaledImaged);
//        processIcon = new ImageIcon(getClass().getResource(processIconFile)); scaledImaged = processIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); processIcon.setImage(scaledImaged);

        headerIcon = new ImageIcon(getClass().getResource(offIconFile));
        emptyIcon = new ImageIcon(getClass().getResource(emptyIconFile));
        offIcon = new ImageIcon(getClass().getResource(offIconFile));
        onIcon = new ImageIcon(getClass().getResource(onIconFile));
        idleIcon = new ImageIcon(getClass().getResource(idleIconFile));
        registeredIcon = new ImageIcon(getClass().getResource(registeredIconFile));
        idleRegisteredIcon = new ImageIcon(getClass().getResource(idleIconFile));
        connectingIcon = new ImageIcon(getClass().getResource(connectingIconFile));
        tryingIcon = new ImageIcon(getClass().getResource(tryingIconFile));
        callIcon = new ImageIcon(getClass().getResource(callIconFile));
        ringIcon = new ImageIcon(getClass().getResource(ringIconFile));
        acceptIcon = new ImageIcon(getClass().getResource(acceptIconFile));
        talkIcon = new ImageIcon(getClass().getResource(talkIconFile));
        localCancelIcon = new ImageIcon(getClass().getResource(localCancelIconFile));
        remoteCancelIcon = new ImageIcon(getClass().getResource(remoteCancelIconFile));
        localBusyIcon = new ImageIcon(getClass().getResource(localBusyIconFile));
        remoteBusyIcon = new ImageIcon(getClass().getResource(remoteBusyIconFile));
        localByeIcon = new ImageIcon(getClass().getResource(localByeIconFile));
        remoteByeIcon = new ImageIcon(getClass().getResource(remoteByeIconFile));
        errorIcon = new ImageIcon(getClass().getResource(errorIconFile));
        processIcon = new ImageIcon(getClass().getResource(processIconFile));
    }

    /**
     *
     * @return
     */
    public String   getIdleIconFile()           { return idleIconFile; }

    /**
     *
     * @return
     */
    public String   getRegisteredIconFile()     { return registeredIconFile; }

    /**
     *
     * @return
     */
    public String   getOffChar()                { return offChar; }

    /**
     *
     * @return
     */
    public String   getOnChar()                 { return onChar; }

    /**
     *
     * @return
     */
    public String   getIdleChar()               { return idleChar; }

    /**
     *
     * @return
     */
    public String   getRegisteredChar()         { return registeredChar; }

    /**
     *
     * @return
     */
    public String   getIdleRegisteredChar()     { return idleRegisteredChar; }

    /**
     *
     * @return
     */
    public String   getConnectChar()            { return connectingChar; }

    /**
     *
     * @return
     */
    public String   getTryChar()                { return tryingChar; }

    /**
     *
     * @return
     */
    public String   getCallChar()               { return callChar; }

    /**
     *
     * @return
     */
    public String   getRingChar()               { return ringChar; }

    /**
     *
     * @return
     */
    public String   getAcceptChar()             { return acceptChar; }

    /**
     *
     * @return
     */
    public String   getTalkChar()               { return talkChar; }

    /**
     *
     * @return
     */
    public String   getLocalCancelChar()        { return localCancelChar; }

    /**
     *
     * @return
     */
    public String   getRemoteCancelChar()       { return remoteCancelChar; }

    /**
     *
     * @return
     */
    public String   getLocalBusyChar()          { return localBusyChar; }

    /**
     *
     * @return
     */
    public String   getRemoteBusyChar()         { return remoteBusyChar; }

    /**
     *
     * @return
     */
    public String   getLocalByeChar()           { return localByeChar; }

    /**
     *
     * @return
     */
    public String   getRemoteByeChar()          { return remoteByeChar; }

    /**
     *
     * @return
     */
    public String   getErrorChar()              { return errorChar; }

    /**
     *
     * @return
     */
    public String   getProcessChar()            { return processChar; }

    /**
     *
     * @return
     */
    public String   getConcurrentChar()         { return concurrentChar; }

    /**
     *
     * @return
     */
    public String   getSumChar()                { return sumChar; }

    /**
     *
     * @return
     */
    public String   getUpChar()                 { return upChar; }

    /**
     *
     * @return
     */
    public String   getDownChar()               { return downChar; }

    /**
     *
     * @return
     */
    public String   getResizeUpChar()           { return resizeUpChar; }

    /**
     *
     * @return
     */
    public String   getResizeDownChar()         { return resizeDownChar; }

    /**
     *
     * @return
     */
    public ImageIcon getHeaderIcon()            { return headerIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getEmptyIcon()             { return emptyIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getOffIcon()               { return offIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getOnIcon()                { return onIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getIdleIcon()              { return idleIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getRegisteredIcon()        { return registeredIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getIdleRegisteredIcon()    { return idleRegisteredIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getConnectIcon()           { return connectingIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getTryIcon()               { return tryingIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getCallIcon()              { return callIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getRingIcon()              { return ringIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getAcceptIcon()            { return acceptIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getTalkIcon()              { return talkIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getLocalCancelIcon()       { return localCancelIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getRemoteCancelIcon()      { return remoteCancelIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getLocalBusyIcon()         { return localBusyIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getRemoteBusyIcon()        { return remoteBusyIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getLocalByeIcon()          { return localByeIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getRemoteByeIcon()         { return remoteByeIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getErrorIcon()             { return errorIcon; }

    /**
     *
     * @return
     */
    public ImageIcon getProcessIcon()           { return processIcon; }

    /**
     *
     * @return
     */
    public Object getHeaderSymbol()             { if ( iconsWanted ) { return headerIcon; }           else { return ""; } }

    /**
     *
     * @return
     */
    public Object getEmptySymbol()              { if ( iconsWanted ) { return emptyIcon; }            else { return ""; } }

    /**
     *
     * @return
     */
    public Object getOffSymbol()                { if ( iconsWanted ) { return offIcon; }              else { return offChar; } }

    /**
     *
     * @return
     */
    public Object getOnSymbol()                 { if ( iconsWanted ) { return onIcon; }               else { return onChar; } }

    /**
     *
     * @return
     */
    public Object getIdleSymbol()               { if ( iconsWanted ) { return idleIcon; }             else { return idleChar; } }

    /**
     *
     * @return
     */
    public Object getRegisteredSymbol()         { if ( iconsWanted ) { return registeredIcon; }       else { return registeredChar; } }

    /**
     *
     * @return
     */
    public Object getIdleRegisteredSymbol()     { if ( iconsWanted ) { return idleRegisteredIcon; }   else { return idleRegisteredChar; } }

    /**
     *
     * @return
     */
    public Object getConnectSymbol()            { if ( iconsWanted ) { return connectingIcon; }       else { return connectingChar; } }

    /**
     *
     * @return
     */
    public Object getTrySymbol()                { if ( iconsWanted ) { return tryingIcon; }           else { return tryingChar; } }

    /**
     *
     * @return
     */
    public Object getCallSymbol()               { if ( iconsWanted ) { return callIcon; }             else { return callChar; } }

    /**
     *
     * @return
     */
    public Object getRingSymbol()               { if ( iconsWanted ) { return ringIcon; }             else { return ringChar; } }

    /**
     *
     * @return
     */
    public Object getAcceptSymbol()             { if ( iconsWanted ) { return acceptIcon; }           else { return acceptChar; } }

    /**
     *
     * @return
     */
    public Object getTalkSymbol()               { if ( iconsWanted ) { return talkIcon; }             else { return talkChar; } }

    /**
     *
     * @return
     */
    public Object getLocalCancelSymbol()        { if ( iconsWanted ) { return localCancelIcon; }      else { return localCancelChar; } }

    /**
     *
     * @return
     */
    public Object getRemoteCancelSymbol()       { if ( iconsWanted ) { return remoteCancelIcon; }     else { return remoteCancelChar; } }

    /**
     *
     * @return
     */
    public Object getLocalBusySymbol()          { if ( iconsWanted ) { return localBusyIcon; }        else { return localBusyChar; } }

    /**
     *
     * @return
     */
    public Object getRemoteBusySymbol()         { if ( iconsWanted ) { return remoteBusyIcon; }       else { return remoteBusyChar; } }

    /**
     *
     * @return
     */
    public Object getLocalByeSymbol()           { if ( iconsWanted ) { return localByeIcon; }         else { return localByeChar; } }

    /**
     *
     * @return
     */
    public Object getRemoteByeSymbol()          { if ( iconsWanted ) { return remoteByeIcon; }        else { return remoteByeChar; } }

    /**
     *
     * @return
     */
    public Object getErrorSymbol()              { if ( iconsWanted ) { return errorIcon; }            else { return errorChar; } }

    /**
     *
     * @return
     */
    public Object getProcessSymbol()            { if ( iconsWanted ) { return processIcon; }          else { return processChar; } }

    /**
     *
     * @param registeredParam
     */
    public void      setIdleIsRegistered(boolean registeredParam)
    {
        if (registeredParam)
        {
//            idleRegisteredIcon = new ImageIcon(getClass().getResource(getRegisteredIconFile())); Image scaledImaged = idleRegisteredIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); idleRegisteredIcon.setImage(scaledImaged);
            idleRegisteredIcon = new ImageIcon(getClass().getResource(getRegisteredIconFile()));
            idleRegisteredChar = getRegisteredChar();
        }
        else
        {
//            idleRegisteredIcon = new ImageIcon(getClass().getResource(getIdleIconFile())); Image scaledImaged = idleRegisteredIcon.getImage().getScaledInstance(defaultWidth, defaultHeight, Image.SCALE_SMOOTH); idleRegisteredIcon.setImage(scaledImaged);
            idleRegisteredIcon = new ImageIcon(getClass().getResource(getIdleIconFile()));
            idleRegisteredChar = getIdleChar();
        }
    }

}
