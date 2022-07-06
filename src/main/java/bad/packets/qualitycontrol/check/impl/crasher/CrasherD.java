package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;

/**
 * Created by am noah
 * CrasherD
 */

@CheckInfo(name = "Crasher", type = "D")
public class CrasherD extends PacketCheck {

    public CrasherD(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.NAME_ITEM);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientNameItem wrapper = new WrapperPlayClientNameItem(event);
        final int length = wrapper.getItemName().length();

        //The max length changes in 1.17+
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17)) {
            //In 1.17+ the max rename length is 50.
            if (length > 50) {
                fail();
            }

        //If below 1.17, the max rename length is 35.
        } else if (length > 35) {
            fail();
        }
    }
}