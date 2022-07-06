package bad.packets.qualitycontrol.manager;

import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.check.impl.aimassist.*;
import bad.packets.qualitycontrol.check.impl.autoblock.*;
import bad.packets.qualitycontrol.check.impl.brandspoof.BrandSpoofA;
import bad.packets.qualitycontrol.check.impl.brandspoof.BrandSpoofB;
import bad.packets.qualitycontrol.check.impl.brandspoof.BrandSpoofC;
import bad.packets.qualitycontrol.check.impl.brandspoof.BrandSpoofD;
import bad.packets.qualitycontrol.check.impl.chat.ChatA;
import bad.packets.qualitycontrol.check.impl.chat.ChatB;
import bad.packets.qualitycontrol.check.impl.crasher.*;
import bad.packets.qualitycontrol.check.impl.disabler.DisablerA;
import bad.packets.qualitycontrol.check.impl.disabler.DisablerB;
import bad.packets.qualitycontrol.check.impl.elytra.ElytraA;
import bad.packets.qualitycontrol.check.impl.fastbreak.FastBreakA;
import bad.packets.qualitycontrol.check.impl.fastbreak.FastBreakB;
import bad.packets.qualitycontrol.check.impl.invalidcombat.InvalidCombatA;
import bad.packets.qualitycontrol.check.impl.invalidcombat.InvalidCombatB;
import bad.packets.qualitycontrol.check.impl.invalidcombat.InvalidCombatC;
import bad.packets.qualitycontrol.check.impl.invalidposition.*;
import bad.packets.qualitycontrol.check.impl.invalidrotation.InvalidRotationA;
import bad.packets.qualitycontrol.check.impl.invalidrotation.InvalidRotationB;
import bad.packets.qualitycontrol.check.impl.invalidrotation.InvalidRotationC;
import bad.packets.qualitycontrol.check.impl.inventory.InventoryA;
import bad.packets.qualitycontrol.check.impl.inventory.InventoryB;
import bad.packets.qualitycontrol.check.impl.inventory.InventoryC;
import bad.packets.qualitycontrol.check.impl.macro.MacroA;
import bad.packets.qualitycontrol.check.impl.macro.MacroB;
import bad.packets.qualitycontrol.check.impl.noslow.NoSlowA;
import bad.packets.qualitycontrol.check.impl.noswing.NoSwingA;
import bad.packets.qualitycontrol.check.impl.noswing.NoSwingB;
import bad.packets.qualitycontrol.check.impl.packetorder.PacketOrderA;
import bad.packets.qualitycontrol.check.impl.packetorder.PacketOrderB;
import bad.packets.qualitycontrol.check.impl.pingspoof.PingSpoofA;
import bad.packets.qualitycontrol.check.impl.returnorder.ReturnOrderA;
import bad.packets.qualitycontrol.check.impl.returnorder.ReturnOrderB;
import bad.packets.qualitycontrol.check.impl.returnorder.ReturnOrderC;
import bad.packets.qualitycontrol.check.impl.returnorder.ReturnOrderD;
import bad.packets.qualitycontrol.check.impl.scaffold.*;
import bad.packets.qualitycontrol.check.impl.skinblinker.SkinBlinkerA;
import bad.packets.qualitycontrol.check.impl.vehiclemove.VehicleMoveA;
import bad.packets.qualitycontrol.check.impl.vehiclemove.VehicleMoveB;
import bad.packets.qualitycontrol.check.impl.wtap.WTapA;
import bad.packets.qualitycontrol.check.impl.wtap.WTapB;
import bad.packets.qualitycontrol.check.impl.wtap.WTapC;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class CheckManager {

    public final Class<?>[] CHECKS = new Class[]{
            AimAssistA.class,
            AimAssistC.class,
            AimAssistD.class,
            AimAssistE.class,
            AimAssistF.class,
            AutoBlockA.class,
            AutoBlockB.class,
            AutoBlockC.class,
            AutoBlockD.class,
            AutoBlockE.class,
            AutoBlockF.class,
            BrandSpoofA.class,
            BrandSpoofB.class,
            BrandSpoofC.class,
            BrandSpoofD.class,
            ChatA.class,
            ChatB.class,
            CrasherA.class,
            CrasherB.class,
            CrasherC.class,
            CrasherD.class,
            CrasherE.class,
            CrasherF.class,
            CrasherG.class,
            DisablerA.class,
            DisablerB.class,
            ElytraA.class,
            FastBreakA.class,
            FastBreakB.class,
            InvalidCombatA.class,
            InvalidCombatB.class,
            InvalidCombatC.class,
            InvalidPositionA.class,
            InvalidPositionB.class,
            InvalidPositionC.class,
            InvalidPositionD.class,
            InvalidPositionE.class,
            InvalidRotationA.class,
            InvalidRotationB.class,
            InvalidRotationC.class,
            InventoryA.class,
            InventoryB.class,
            InventoryC.class,
            MacroA.class,
            MacroB.class,
            NoSlowA.class,
            NoSwingA.class,
            NoSwingB.class,
            PacketOrderA.class,
            PacketOrderB.class,
            PingSpoofA.class,
            ReturnOrderA.class,
            ReturnOrderB.class,
            ReturnOrderC.class,
            ReturnOrderD.class,
            ScaffoldA.class,
            ScaffoldB.class,
            ScaffoldC.class,
            ScaffoldD.class,
            ScaffoldE.class,
            SkinBlinkerA.class,
            VehicleMoveA.class,
            VehicleMoveB.class,
            WTapA.class,
            WTapB.class,
            WTapC.class,
    };

    private final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public List<Check> loadChecks(final QualityControlPlayer data) {
        final List<Check> checkList = new ArrayList<>();

        if (ConfigManager.CONFIG_REQUIRES_UPDATE) return checkList;

        for (final Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer());
                exception.printStackTrace();
            }
        }
        return checkList;
    }

    public void setup() {
        for (final Class<?> clazz : CHECKS) {
            try {
                CONSTRUCTORS.add(clazz.getConstructor(QualityControlPlayer.class));
            } catch (final NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }
    }
}
