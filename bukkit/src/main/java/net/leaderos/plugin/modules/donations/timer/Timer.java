package net.leaderos.plugin.modules.donations.timer;

import com.tcoded.folialib.wrapper.task.WrappedTask;

import net.leaderos.plugin.Bukkit;
import net.leaderos.plugin.modules.donations.managers.DonationManager;

/**
 * Time checker for update scheduler
 *
 * @author poyrazinan
 * @since 1.0
 * 
 */



public class Timer {
    
    /**
     * Stores the reference to our scheduled task
     */
    private static WrappedTask scheduledTask;
    
    public static void run() {
        // Cancel previous task if it exists
        if (scheduledTask != null) {
            Bukkit.getFoliaLib().getScheduler().cancelTask(scheduledTask);
            scheduledTask = null;
        }
        
        // Get update interval from config
        long updateIntervalSeconds = Bukkit.getInstance().getModulesFile().getDonations().getUpdateSecond();
        
        // Schedule task using FoliaLib's timer
        scheduledTask = Bukkit.getFoliaLib().getScheduler().runTimerAsync(
                () -> DonationManager.updateAllData(), 
                1,  // Initial delay in seconds
                updateIntervalSeconds);  // Repeat interval in seconds
    }
    
    /**
     * Cancels the scheduled task
     */
    public static void cancel() {
        if (scheduledTask != null) {
            Bukkit.getFoliaLib().getScheduler().cancelTask(scheduledTask);
            scheduledTask = null;
        }
    }
}
