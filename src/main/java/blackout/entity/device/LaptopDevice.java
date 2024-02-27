package blackout.entity.device;
import utils.Angle;

public class LaptopDevice extends Device {
    public static final int RANGE = 100000;

    public LaptopDevice(String deviceId, Angle position) {
        super(deviceId, position, RANGE);
    }
}
