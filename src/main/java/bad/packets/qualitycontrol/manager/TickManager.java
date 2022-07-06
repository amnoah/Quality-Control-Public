package bad.packets.qualitycontrol.manager;

import bad.packets.qualitycontrol.QualityControl;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * This class is copy pasted from Medusa Tick Manager as I really didn't feel like putting in effort.
 * https://github.com/GladUrBad/Medusa/blob/master/Impl/src/main/java/com/gladurbad/medusa/manager/TickManager.java
 */

public class TickManager implements Runnable {

    private static BukkitTask task;

    public void start() {
        assert task == null : "Transaction Processor started twice!";

        task = Bukkit.getScheduler().runTaskTimer(QualityControl.INSTANCE.plugin, this, 0L, 1L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {

        QualityControl.INSTANCE.playerManager.getPlayerDataMap().forEach(
                (uuid, playerData) -> playerData.handleTickTask());
    }
}