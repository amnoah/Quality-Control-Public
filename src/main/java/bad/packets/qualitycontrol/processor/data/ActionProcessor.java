package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

public final class ActionProcessor {

    private final QualityControlPlayer data;

    public ActionProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    /**
     * Our objects.
     */

    private boolean sprinting, sneaking;

    private boolean sprintingQuestionable = true, lastSprintingQuestionable, sneakingQuestionable = true, lastSneakingQuestionable;

    private boolean placing = false, digging = false, usingItem = false, windowClick = false;

    private boolean inventory;

    /**
     * Our Getters.
     */

    public boolean isSprinting() {
        return sprinting;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public boolean isSprintingQuestionable() {
        return sprintingQuestionable;
    }

    public boolean isSneakingQuestionable() {
        return sneakingQuestionable;
    }

    public boolean isLastSprintingQuestionable() {
        return lastSprintingQuestionable;
    }

    public boolean isLastSneakingQuestionable() {
        return lastSneakingQuestionable;
    }

    public boolean hasPlaced() {
        return placing;
    }

    public boolean hasDug() {
        return digging;
    }

    public boolean hasUsedItem() {
        return usingItem;
    }

    public boolean isClickingWindow() {
        return windowClick;
    }

    public boolean isInInventory() {
        return inventory;
    }

    /**
     * Update tracked action information.
     * @param wrapper Entity Action In wrapper.
     */
    public void handleEntityAction(final WrapperPlayClientEntityAction wrapper) {
        switch (wrapper.getAction()) {
            case START_SPRINTING:
                sprinting = true;
                lastSprintingQuestionable = sprintingQuestionable;
                sprintingQuestionable = false;
                break;
            case STOP_SPRINTING:
                lastSprintingQuestionable = sprintingQuestionable;
                sprinting = sprintingQuestionable = false;
                break;
            case START_SNEAKING:
                sneaking = true;
                lastSneakingQuestionable = sneakingQuestionable;
                sneakingQuestionable = false;
                break;
            case STOP_SNEAKING:
                lastSneakingQuestionable = sneakingQuestionable;
                sneaking = sneakingQuestionable = false;
                break;
            default:
                break;
        }
    }

    /**
     * Sets questionable values. World change and misc. teleports can cause sprint/sneak de-sync, important to have.
     */
    public void handleTeleport() {
        lastSneakingQuestionable = sneakingQuestionable;
        lastSprintingQuestionable = sprintingQuestionable;
        sprintingQuestionable = sneakingQuestionable = true;
    }

    /**\
     * Update our inventory status whenever the player says they've opened their inventory.
     * Technically only accurate for not in inventory.
     */
    public void handleClientCommand(final WrapperPlayClientClientStatus wrapper) {
        if (wrapper.getAction().equals(WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT)) {
            inventory = true;
        }
    }

    public void handleCloseWindow() {
        inventory = false;
    }

    public void handleBlockPlace(){
        placing = true;
    }

    public void handleBlockDig(){
        digging = true;
    }

    public void handleWindowClick() {
        windowClick = true;
    }

    public void handleUseItem() {
        usingItem = true;
    }

    /**
     * Reset all action booleans on a flying packet.
     * When ticking actively this accurately tells if the player is performing actions.
     */
    public void handleFlying(){
        placing = digging = windowClick = usingItem = false;
    }
}