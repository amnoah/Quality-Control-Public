package bad.packets.qualitycontrol.check.impl.brandspoof;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackSend;

import java.util.ArrayDeque;

/**
 * Created by am noah
 * BrandSpoofC
 */

@CheckInfo(name = "BrandSpoof", type = "C")
public class BrandSpoofC extends PacketCheck {

    public BrandSpoofC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.RESOURCE_PACK_STATUS);
        listenedPacketsIncoming.add(PacketType.Play.Client.WINDOW_CONFIRMATION);

        listenedPacketsOutgoing.add(PacketType.Play.Server.RESOURCE_PACK_SEND);
    }

    final WrapperPlayServerResourcePackSend logInResourcePack = new WrapperPlayServerResourcePackSend(
            "level://../options.txt", "LoginTest", false, null);


    /*
     * While it may seem completely unnecessary to do this due to how early we're sending, I promise it's not.
     * You never know what the server may be running, whether built into other plugins or into the server jar itself,
     * so it's safest just to track whats going on and making sure we're confident in whats going on.
     *
     * Theoretically this shouldn't false. There should always be a response of some kind and packet order is always
     * maintained.
     *
     * THIS DOES FLAG BADLION, however this is because Badlion breaks vanilla behavior... so who's really in the wrong?
     */

    private final ArrayDeque<Boolean> pendingResourceStatuses = new ArrayDeque<>();

    private int transactionID = data.getTransactionProcessor().getNextID();
    private boolean firstTransaction = true;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.RESOURCE_PACK_STATUS)) {
            final boolean isQCRes = pendingResourceStatuses.getFirst();

            if (isQCRes) {
                WrapperPlayClientResourcePackStatus wrapper = new WrapperPlayClientResourcePackStatus(event);

                // 1.8- users should accept while 1.9+ should return failed download.
                if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
                    if (!wrapper.getResult().equals(WrapperPlayClientResourcePackStatus.Result.ACCEPTED)) fail();
                } else {
                    if (!wrapper.getResult().equals(WrapperPlayClientResourcePackStatus.Result.FAILED_DOWNLOAD)) fail();
                }

                data.getChecks().remove(this);
                pendingResourceStatuses.clear();
                return;
            }

            pendingResourceStatuses.removeFirst();
        } else {
            if (data.getTransactionProcessor().getAnsweredID() != transactionID) return;

            if (firstTransaction) {
                data.getPlayer().sendPacket(logInResourcePack);
                firstTransaction = false;
                transactionID = data.getTransactionProcessor().getNextID();
                return;
            }

            if (data.getClientBrand().equals("vanilla")) fail();
            data.getChecks().remove(this);
        }
    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        WrapperPlayServerResourcePackSend wrapper = new WrapperPlayServerResourcePackSend(event);

        pendingResourceStatuses.add(wrapper.getHash().equals("LoginTest"));
    }
}