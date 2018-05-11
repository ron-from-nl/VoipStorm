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

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Calendar;

/**
 *
 * @author ron
 */
public class SysMonitor
{
    private OperatingSystemMXBean bean;

    private Calendar    currCPUCalendar;
    private Calendar    lastCPUCalendar;
    private long        currCPUMilliesUsed = 0;
    private long        lastCPUMilliesUsed = 0;

    private long        currMemValue = 0;

    /**
     *
     */
    public SysMonitor()
    {
        bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean( );

        currCPUCalendar = Calendar.getInstance();
        lastCPUCalendar = Calendar.getInstance();
    }

    /**
     *
     * @return
     */
    public int getProcessTime() // Returns a cpuUsage percentage of the VM this code is running regardless of call interval 
    {
        int cpuPercent = 0;

        if ( (bean instanceof com.sun.management.OperatingSystemMXBean) )
        {
            currCPUMilliesUsed = (bean.getProcessCpuTime()/1000000); // Returns millies-secs used by VM since last call

            currCPUCalendar = Calendar.getInstance(); // Taking a snapshot of time for the (millisecs gone by versus millisecs used)
            long milliesPassed = (currCPUCalendar.getTimeInMillis() - lastCPUCalendar.getTimeInMillis()); // How much time went by since last clocking
            long cpuMilliesDiff = (currCPUMilliesUsed - lastCPUMilliesUsed); // How many millisecs were consumed since last clocking
            cpuPercent = (int)Math.round((cpuMilliesDiff / (milliesPassed * 0.001))/10); // (CPU lillisecs used / times (passed / 100)) = cpuPercent(used)
            if (cpuPercent > 100) {cpuPercent = 100;} // Shouldn't be happening, but rounding from nanosec to millisec could lift slightly over top
            lastCPUMilliesUsed = currCPUMilliesUsed; // Keep last usage in mem for next clocking
            lastCPUCalendar.setTimeInMillis(currCPUCalendar.getTimeInMillis()); // Set last timestamp to current timstamp for next clicking
        }
        else
        {
            cpuPercent = 50; // If the bean isn't a member then set the usage to 50% (in case some dashboard expects and uses this number)
        }
        return cpuPercent;
    }

    /**
     *
     * @return
     */
    public int getPhysMem()
    {
        if ( (bean instanceof com.sun.management.OperatingSystemMXBean) )
        {
            currMemValue = (bean.getFreePhysicalMemorySize() / ( 1024 * 1024 ));
        }
        else
        {
            currMemValue = 100;
        }
        return (int)currMemValue;
    }
}
