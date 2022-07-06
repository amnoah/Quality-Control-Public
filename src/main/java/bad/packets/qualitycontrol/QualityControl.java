package bad.packets.qualitycontrol;

import bad.packets.qualitycontrol.listener.bukkit.CommandListener;
import bad.packets.qualitycontrol.listener.bukkit.JoinLeaveListener;
import bad.packets.qualitycontrol.listener.packet.PacketListener;
import bad.packets.qualitycontrol.manager.CheckManager;
import bad.packets.qualitycontrol.manager.ConfigManager;
import bad.packets.qualitycontrol.manager.PlayerManager;
import bad.packets.qualitycontrol.manager.TickManager;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;

public enum QualityControl {
    /**
     * This is our enum instance for our QualityControl class. You can use this to access non-static stuff in this class.
     * You may ask why enum instance and that is a great question, the answer is its cooler and cleaner than the old fashion way.
     */
    INSTANCE;

    public final double version = 0.03;

    public final String alertsPermission = "qualitycontrol.alerts";

    public QualityControlPlugin plugin;

    public PlayerManager playerManager;

    public TickManager tickManager;

    public CommandListener commandListener;

    /**
     * These are our Getters.
     */

    public double getVersion() {
        return version;
    }

    public String getAlertsPermission() {
        return alertsPermission;
    }

    public QualityControlPlugin getPlugin() {
        return plugin;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    /**
     * This method gets called on load.
     */
    public void load(final QualityControlPlugin plugin) {
        this.plugin = plugin;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings().checkForUpdates(true);
        PacketEvents.getAPI().load();
    }

    /**
     * This method gets called on plugin enable.
     */
    public void enable(final QualityControlPlugin plugin) {
        //Metrics metrics = new Metrics(plugin, Your bStats ID);

        enableBukkit();
        enableQualityControl();
    }

    /**
     * Handle Bukkit related tasks on plugin enable.
     */
    private void enableBukkit() {
        plugin.getServer().getPluginManager().registerEvents(new JoinLeaveListener(), plugin);
    }

    /**
     * Handle Quality Control related tasks on plugin enable.
     */
    private void enableQualityControl() {
        tickManager = new TickManager();
        tickManager.start();

        playerManager = new PlayerManager();

        commandListener = new CommandListener();
        plugin.getCommand("qc").setExecutor(commandListener);

        ConfigManager.reloadConfiguration();

        CheckManager.setup();

        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
    }

    /**
     * This method gets called on plugin stop.
     * We can disable our plugin and cancel all tasks here.
     */
    public void stop(final QualityControlPlugin plugin) {
        PacketEvents.getAPI().terminate();
        Bukkit.getScheduler().cancelTasks(plugin);

        tickManager.stop();

        tickManager = null;
        commandListener = null;
        playerManager = null;
    }
}
