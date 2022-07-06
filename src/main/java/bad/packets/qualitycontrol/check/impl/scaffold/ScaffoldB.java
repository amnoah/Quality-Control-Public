package bad.packets.qualitycontrol.check.impl.scaffold;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;

/**
 * Created by am noah
 * ScaffoldB
 */

@CheckInfo(name = "Scaffold", type = "B")
public class ScaffoldB extends PacketCheck {

    public ScaffoldB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.HELD_ITEM_CHANGE);
    }

    private int lastSlot = 404;

    /**
     * Note: I think this can false?
     *
     * Due to the fact that the server can change their current slot and the fact that we're not accurately tracking
     * their slot the lastSlot portion may false.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);
        final int slot = wrapper.getSlot();

        // A player cannot change to their previous slot. Their slot value also has to be within 0 and 8.
        if (slot == lastSlot || slot < 0 || slot > 8) fail();

        // Save the slot sent this run as our previous slot to use later.
        lastSlot = slot;
    }
}