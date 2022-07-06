package bad.packets.qualitycontrol.processor.test;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.util.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackSend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class FileCheckTest {

    private final QualityControlPlayer data;

    public FileCheckTest(final QualityControlPlayer data) {
        this.data = data;
    }

    private boolean ongoingTest = false;

    private final List<User> alertWhenFinished = new ArrayList<>();
    private User alertOfResult;

    private final ArrayDeque<Boolean> resourceTracker = new ArrayDeque<>();
    private boolean sentResourceStatus = false;

    private String pretendLocation;

    /**
     * Pre-made response messages.
     */

    private final String existingTestResponse = MessageUtil.translate(
            "&7You already have an ongoing test running for this user!");
    private final String ongoingTestResponse = MessageUtil.translate(
            "&7A file check test is already active for this user.\n&7We'll alert you when it's finished.\n");
    private final String incompatibleClientVersionResponse = MessageUtil.translate(
            "&7Unable to start this test due to the user being logged in using a client newer than 1.8.9.");
    private final String testStartedResponse = MessageUtil.translate(
            "&7FileCheckTest has been started, we'll alert you when it concludes.\n");

    /**
     * Check if we're able to start the test and if we are then start the test.
     */

    private int testActiveTicks = 0;

    public void initializeTest(User user, String fileURL, String pretendLocation) {
        if (ongoingTest) {
            if (user.equals(alertOfResult)) {
                user.sendMessage(existingTestResponse);
                return;
            }

            user.sendMessage(ongoingTestResponse);

            if (!alertWhenFinished.contains(user)) {
                alertWhenFinished.add(user);
            }
        }

        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            user.sendMessage(incompatibleClientVersionResponse);
            return;
        }

        this.pretendLocation = pretendLocation;

        sentResourceStatus = false;
        ongoingTest = true;
        testActiveTicks = 0;
        alertOfResult = user;

        data.getPlayer().sendPacket(new WrapperPlayServerResourcePackSend(
                fileURL, "FILECHECKTEST", false, null));

        alertOfResult.sendMessage(testStartedResponse);
    }

    /**
     * Eventually we time out the test just so we don't have to keep observing packets when unnecessary.
     * Client flying packets/transaction are unreliable so we just use the server tick... it doesn't matter anyways.
     */

    public void handleTicking() {
        if (!ongoingTest) return;

        testActiveTicks += 1;

        // 15 seconds.
        if (testActiveTicks == 300) {
            ongoingTest = false;
            resourceTracker.clear();

            String testerResponse = MessageUtil.translate(
                    "&b" + data.getUsername() + "&7's FileCheckTest has timed out (blocking file check packets?).");
            String waitingResponse = MessageUtil.translate(
                    "&b" + data.getUsername() + "&7's FileCheckTest has concluded.\n" +
                            "&7Please note that it appears that they are not responding to file check packets.");

            alertOfResult.sendMessage(testerResponse);

            for (User user : alertWhenFinished) {
                user.sendMessage(waitingResponse);
            }
        }
    }

    public void listenReceiveResourceStatus(WrapperPlayClientResourcePackStatus wrapper) {
        if (!ongoingTest) return;
        if (!resourceTracker.removeFirst()) return;

        ongoingTest = false;
        resourceTracker.clear();

        StringBuilder result = new StringBuilder("&9FileCheckTest Results For ").append(data.getUsername());
        result.append("\n \n").append("&7File Exists At Location &b").append(pretendLocation).append("&7:&b ");
        result.append(wrapper.getResult().equals(WrapperPlayClientResourcePackStatus.Result.ACCEPTED) ? "True" : "False");
        result.append("\n");

        String waitingResponse = MessageUtil.translate("&b" + data.getUsername() + "&7's FileCheckTest has concluded.");

        alertOfResult.sendMessage(MessageUtil.translate(result.toString()));

        for (User user : alertWhenFinished) {
            user.sendMessage(waitingResponse);
        }
    }

    public void listenSendResourceStatus(WrapperPlayServerResourcePackSend wrapper) {
        if (!ongoingTest) return;

        if (wrapper.getHash().equals("FILECHECKTEST") && !sentResourceStatus) {
            resourceTracker.add(true);
            sentResourceStatus = true;
        }
        else resourceTracker.add(false);
    }
}