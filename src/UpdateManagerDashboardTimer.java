import java.util.*;

/**
 *
 * @author ron
 */
public class UpdateManagerDashboardTimer extends TimerTask // CLASS
{
    Manager manager;

    UpdateManagerDashboardTimer(Manager managerParam) // CONSTRUCTOR
    {
        manager = managerParam;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        manager.timedDashboardManagerUpdate();
    }
}
