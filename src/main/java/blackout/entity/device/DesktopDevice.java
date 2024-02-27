package blackout.entity.device;
import blackout.entity.Entity;
import blackout.entity.satellite.StandardSatellite;
import utils.Angle;

public class DesktopDevice extends Device {
    public static final int RANGE = 200000;

    public DesktopDevice(String deviceId, Angle position) {
        super(deviceId, position, RANGE);
    }

    @Override
    public boolean isEntityCompatible(Entity entity) {
        return super.isEntityCompatible(entity) && !(entity instanceof StandardSatellite);
    }
}
