package bad.packets.qualitycontrol.util.type;

import com.github.retrooper.packetevents.util.Vector3d;

public class QCPositionOut {
    /*
     * Position is our X, Y, Z coordinates of the teleport. The look values shouldn't really matter.
     *
     * Add is pretty nondescript, I don't know a better name. If true then when this is completed we add this position
     * to the pending teleports, if false then we remove the position.
     */
    Vector3d position;
    boolean add;

    public QCPositionOut(Vector3d position, boolean add) {
        this.position = position;
        this.add = add;
    }

    public QCPositionOut(double x, double y, double z, boolean add) {
        this.position = new Vector3d(x, y, z);
        this.add = add;
    }

    public Vector3d getPosition() {
        return position;
    }

    public boolean isAdd() {
        return add;
    }

    public boolean equals(QCPositionOut input) {
        return input.getPosition().equals(this.position);
    }
}
