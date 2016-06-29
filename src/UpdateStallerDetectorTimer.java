import java.util.*;

/**
 *
 * @author ron
 */
public class UpdateStallerDetectorTimer extends TimerTask // CLASS
{
    ECallCenter21 eCallCenter21;

    UpdateStallerDetectorTimer(ECallCenter21 eCallCenter21Param) // CONSTRUCTOR
    {
        eCallCenter21 = eCallCenter21Param;
    }

    @Override
    public void run() // METHOD (THREADED)
    {
        eCallCenter21.timedStallingDetectionUpdate();
    }
}
