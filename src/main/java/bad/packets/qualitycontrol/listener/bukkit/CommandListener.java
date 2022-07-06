package bad.packets.qualitycontrol.listener.bukkit;

import bad.packets.qualitycontrol.commands.QualityControlCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

    QualityControlCommand commandProcessor = new QualityControlCommand();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            System.out.println("QC > Cannot run command sent by console.");
        } else {
            commandProcessor.handleCommand((Player) commandSender, command, s, strings);
        }

        return false;
    }
}
