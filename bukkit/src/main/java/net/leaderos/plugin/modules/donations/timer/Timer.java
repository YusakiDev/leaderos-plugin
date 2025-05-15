package net.leaderos.plugin.modules.donations.timer;

import com.tcoded.folialib.wrapper.task.WrappedTask;

import net.leaderos.plugin.Bukkit;
import net.leaderos.plugin.modules.donations.managers.DonationManager;

/**
 * Time checker for update scheduler
 *
 * @author poyrazinan
 * @since 1.0
 */
public class Timer {
    
    public static void run() {
        
        Bukkit.getFoliaLib().getScheduler().runTimerAsync(
            (task) -> DonationManager.updateAllData(),
            1L,
            20L * Bukkit.getInstance().getModulesFile().getDonations().getUpdateSecond()
        );
    }

    public static void cancel(WrappedTask task){
        Bukkit.getFoliaLib().getScheduler().cancelTask(task);
    }

    public static void cancelAllTask(){
        Bukkit.getFoliaLib().getScheduler().cancelAllTasks();
    }
}
