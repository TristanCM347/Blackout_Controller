package blackout.entity.satellite;
import static utils.MathsHelper.CLOCKWISE;

import blackout.entity.Entity;
import blackout.entity.device.DesktopDevice;
import utils.Angle;

public class StandardSatellite extends Satellite {
    public static final int SPEED = 2500;
    public static final int RANGE = 150000;
    public static final int RECIEVE_BANDWIDTH = 1;
    public static final int SEND_BANDWIDTH = 1;
    public static final int MAX_FILE = 3;
    public static final int MAX_BYTES = 80;

    public StandardSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, CLOCKWISE, RANGE);
    }

    @Override
    public void move() {
        Angle positionChange = Angle.fromRadians(SPEED / super.getHeight());
        Angle newPosition = super.getPosition().subtract(positionChange);
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

    @Override
    public boolean isEntityCompatible(Entity entity) {
        return super.isEntityCompatible(entity) && !(entity instanceof DesktopDevice);
    }
}
