package blackout.entity.device;
import static utils.MathsHelper.RADIUS_OF_JUPITER;

import blackout.entity.Entity;
import utils.Angle;

public abstract class Device extends Entity {
    public static final int MAX_FILE = Integer.MAX_VALUE;
    public static final int MAX_BYTES = Integer.MAX_VALUE;
    public static final int RECIEVE_BANDWIDTH = Integer.MAX_VALUE;
    public static final int SEND_BANDWIDTH = Integer.MAX_VALUE;

    public Device(String id, Angle position, int range) {
        super(id, RADIUS_OF_JUPITER, position, range);
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
        return super.isEntityCompatible(entity) && !(entity instanceof Device);
    }

}
