package blackout.entity.satellite;
import static utils.MathsHelper.ANTI_CLOCKWISE;
import static utils.MathsHelper.CLOCKWISE;

import blackout.entity.device.Device;
import utils.Angle;

public class TeleportingSatellite extends Satellite {
    public static final int SPEED = 1000;
    public static final int RANGE = 200000;
    public static final int RECIEVE_BANDWIDTH = 15;
    public static final int SEND_BANDWIDTH = 10;
    public static final int MAX_FILE = Integer.MAX_VALUE;
    public static final int MAX_BYTES = 200;

    public TeleportingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, ANTI_CLOCKWISE, RANGE);
    }

    public void teleportRecievingFiles() {
        super.getFiles().removeIf(file -> {
            if (!file.isFileComplete() && file.getSenderEntity() instanceof Satellite) {
                file.removeFileUploadFromSenderEntity();
                file.removeRestOfTsFromContent();
                file.downloadRestOfFile();
                return false;

            } else if (!file.isFileComplete() && file.getSenderEntity() instanceof Device) {
                file.removeFileUploadFromSenderEntity();
                file.removeAllTsFromOriginalFile();
                return true;
            }
            return false;
        });
    }

    @Override
    public void move() {
        Angle positionChange = Angle.fromRadians(SPEED / super.getHeight());
        // if super position plus/minus change_in_position is past 180 then teleport to 0 position
        if (super.getDirection() == ANTI_CLOCKWISE) {
            Angle newPosition = super.getPosition().add(positionChange);

            if (super.getPosition().compareTo(Angle.fromDegrees(180)) < 0
            && newPosition.compareTo(Angle.fromDegrees(180)) >= 0) {
                super.setDirection(CLOCKWISE);
                super.setPosition(Angle.fromDegrees(0));
                this.transferDataFromThisEntity();
                this.teleportRecievingFiles();
            } else {
                super.setPosition(newPosition);
            }

        } else if (super.getDirection() == CLOCKWISE) {
            Angle newPosition = super.getPosition().subtract(positionChange);

            if (super.getPosition().compareTo(Angle.fromDegrees(180)) > 0
            && newPosition.compareTo(Angle.fromDegrees(180)) <= 0) {
                super.setDirection(ANTI_CLOCKWISE);
                super.setPosition(Angle.fromDegrees(0));
                this.transferDataFromThisEntity();
                this.teleportRecievingFiles();
            } else {
                super.setPosition(newPosition);
            }
        }
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
    public void transferDataFromThisEntity() {
        if (super.getPosition().compareTo(Angle.fromDegrees(0)) == 0) {
            super.getFileTransferList().removeIf(file -> {
                file.removeRestOfTsFromContent();
                file.downloadRestOfFile();
                return true;
            });
        } else {
            super.transferDataFromThisEntity();
        }
    }
}
