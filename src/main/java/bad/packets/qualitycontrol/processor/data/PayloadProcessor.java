package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.manager.ConfigManager;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.util.MessageUtil;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

public final class PayloadProcessor {

    private final QualityControlPlayer data;

    public PayloadProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    /**
     * Our objects.
     */

    private boolean invalidPayload = false;

    private boolean sentNonMinecraft = false;

    /**
     * Our Getters.
     */

    public boolean isInvalidPayload() {
        return invalidPayload;
    }

    public boolean hasSentNonVanilla() {
        return sentNonMinecraft;
    }

    /**
     * Basic check for vanilla/non-vanilla payloads.
     * @param wrapper Plugin Message In wrapper.
     */
    public void handlePayload(WrapperPlayClientPluginMessage wrapper) {
        final String channel = wrapper.getChannelName();
        final byte[] wrapperData = wrapper.getData();

        //1.12- and 1.13+ channel names. If neither was sent, it isn't a Vanilla client.
        if (channel.contains("MC|") || channel.contains("minecraft:")) {

            switch (channel) {
                case "MC|Brand":
                case "minecraft:brand":
                    handleBrand(wrapperData);
                    break;
            }
        } else {
            //The channel name didn't contain either.
            sentNonMinecraft = true;
        }
    }

    /**
     * Identify and set the player's brand in their QualityControlPlayer data manager.
     * @param wrapperData The data held within the Plugin Message packet.
     */
    private void handleBrand(byte[] wrapperData) {
        if (data.getClientBrand().equals("null")) {

            //Copy pasted from Grim.
            // https://github.com/MWHunter/Grim/blob/9b9cf40392bfdea9fe06eba8639cb8cb269f2e6d/src/main/java/ac/grim/grimac/checks/impl/misc/ClientBrand.java
            if (wrapperData.length == 0) {
                data.setClientBrand("null");
            } else {
                byte[] minusLength = new byte[wrapperData.length - 1];
                System.arraycopy(wrapperData, 1, minusLength, 0, minusLength.length);

                data.setClientBrand(new String(minusLength));
            }

            if (ConfigManager.CONFIG_REQUIRES_UPDATE) return;

            if (!ConfigManager.PLAYER_JOIN_MESSAGE.isEmpty()) {
                MessageUtil.sendMessage(MessageUtil.translate(ConfigManager.PLAYER_JOIN_MESSAGE
                        .replaceAll("%player%", data.getUsername())
                        .replaceAll("%clientbrand%", data.getClientBrand())));
            }
        } else {
            invalidPayload = true;
        }
    }
}