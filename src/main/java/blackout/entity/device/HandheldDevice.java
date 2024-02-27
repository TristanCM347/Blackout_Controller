package blackout.entity.device;
import utils.Angle;

public class HandheldDevice extends Device {
    public static final int RANGE = 50000;

    public HandheldDevice(String deviceId, Angle position) {
        super(deviceId, position, RANGE);
    }
}
