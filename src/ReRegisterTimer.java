import java.util.*;

/**
 *
 * @author ron
 */
public class ReRegisterTimer extends TimerTask // CLASS
{
    ECallCenter21 eCallCenter;

    ReRegisterTimer(ECallCenter21 eCallCenterParam) // CONSTRUCTOR
    {
        eCallCenter = eCallCenterParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        eCallCenter.reRegister();
    }
}
