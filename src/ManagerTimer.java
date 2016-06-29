import java.util.*;

/**
 *
 * @author ron
 */
public class ManagerTimer extends TimerTask // CLASS
{
    Manager manager;

    ManagerTimer(Manager eCallCenterManagerParam) // CONSTRUCTOR
    {
        manager = eCallCenterManagerParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        manager.serviceTimeline();
    }
}
