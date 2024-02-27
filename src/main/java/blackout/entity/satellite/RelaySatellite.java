package blackout.entity.satellite;
import static utils.MathsHelper.ANTI_CLOCKWISE;
import static utils.MathsHelper.CLOCKWISE;

import utils.Angle;

public class RelaySatellite extends Satellite {
    private static final int SPEED = 1500;
    private static final int RANGE = 300000;
    private static final int SEND_BANDWIDTH = Integer.MAX_VALUE;
    private static final int RECIEVE_BANDWIDTH = Integer.MAX_VALUE;
    public static final int MAX_FILE = 0;
    public static final int MAX_BYTES = 0;

    public RelaySatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, CLOCKWISE, RANGE);
    }

    @Override
    public void move() {
        Angle positionChange = Angle.fromRadians(SPEED / super.getHeight());
        Angle newPosition;

        // if position is in range keep going in direction
        if (super.getPosition().compareTo(Angle.fromDegrees(140)) > 0
        && super.getPosition().compareTo(Angle.fromDegrees(190)) < 0) {
            if (super.getDirection() == CLOCKWISE) {
                newPosition = super.getPosition().subtract(positionChange);
            } else {
                newPosition = super.getPosition().add(positionChange);
            }
        } else if (super.getPosition().compareTo(Angle.fromDegrees(190)) >= 0
        && super.getPosition().compareTo(Angle.fromDegrees(345)) < 0) {
            // if position is outside of range and clockwise
            super.setDirection(CLOCKWISE);
            newPosition = super.getPosition().subtract(positionChange);
        } else {
            // if position is outside of range and anticlockwise
            super.setDirection(ANTI_CLOCKWISE);
            newPosition = super.getPosition().add(positionChange);
        }
        super.setPosition(newPosition);
    }

    @Override
    public int getSendingBandwidth() {
        return SEND_BANDWIDTH;
    }

    @Override
    public int getRecievingBandwidth() {
        return RECIEVE_BANDWIDTH;
    }

    @Override
    public int getMaxFileStorage() {
        return MAX_FILE;
    }

    @Override
    public int getMaxByteStorage() {
        return MAX_BYTES;
    }
}
