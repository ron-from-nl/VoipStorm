import java.util.*;

/**
 *
 * @author ron
 */
public class EndCallTimer extends TimerTask // CLASS
{
    SoftPhone mySoftPhone;

    EndCallTimer(SoftPhone mySoftPhoneParam) // CONSTRUCTOR
    {
        mySoftPhone = mySoftPhoneParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        mySoftPhone.byeRequest();
    }
}
