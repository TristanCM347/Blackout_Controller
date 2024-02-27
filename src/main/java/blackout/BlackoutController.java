package blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import blackout.entity.Entity;
import blackout.entity.device.DesktopDevice;
import blackout.entity.device.Device;
import blackout.entity.device.HandheldDevice;
import blackout.entity.device.LaptopDevice;
import blackout.entity.satellite.RelaySatellite;
import blackout.entity.satellite.Satellite;
import blackout.entity.satellite.StandardSatellite;
import blackout.entity.satellite.TeleportingSatellite;
import response.models.EntityInfoResponse;
import utils.Angle;

public class BlackoutController {
    private ArrayList<Entity> entities = new ArrayList<>();

    public void createDevice(String id, String deviceType, Angle position) {
        switch (deviceType) {
        case "HandheldDevice":
            entities.add(new HandheldDevice(id, position));
            break;
        case "DesktopDevice":
            entities.add(new DesktopDevice(id, position));
            break;
        case "LaptopDevice":
            entities.add(new LaptopDevice(id, position));
            break;
        default:
            break;
        }
    }

    public void removeDevice(String id) {
        entities.removeIf(entity -> entity.getId().equals(id));
    }

    public void createSatellite(String id, String satelliteType, double height, Angle position) {
        switch (satelliteType) {
        case "StandardSatellite":
            entities.add(new StandardSatellite(id, height, position));
            break;
        case "TeleportingSatellite":
            entities.add(new TeleportingSatellite(id, height, position));
            break;
        case "RelaySatellite":
            entities.add(new RelaySatellite(id, height, position));
            break;
        default:
            break;
        }
    }

    public void removeSatellite(String id) {
       entities.removeIf(entity -> entity.getId().equals(id));
    }

    public List<String> listDeviceIds() {
        return entities.stream()
            .filter(entity -> entity instanceof Device)
            .map(Entity::getId).collect(Collectors.toList());
    }

    public List<String> listSatelliteIds() {
        return entities.stream()
            .filter(entity -> entity instanceof Satellite)
            .map(Entity::getId).collect(Collectors.toList());
    }

    public void addFileToDevice(String id, String filename, String content) {
        entities.stream()
            .filter(entity -> entity.getId().equals(id))
            .forEach(entity -> entity.createCompleteFile(filename, content));
    }

    public EntityInfoResponse getInfo(String id) {
        return entities.stream()
            .filter(entity -> entity.getId().equals(id))
            .map(Entity::entityInfoResponse)
            .findFirst().orElse(null);
    }

    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public void simulate() {
        for (Entity entity : entities) {
            entity.moveSatellites();
        }
        for (Entity entity: entities) {
            entity.removeOutOfRangeTransfers(entities);
        }
        for (Entity entity : entities) {
            entity.transferDataFromThisEntity();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<String> communicableEntitiesInRange =  null;
        for (Entity entity : entities) {
            if (entity.getId().equals(id)) {
                communicableEntitiesInRange = entity.findCommunicableEntitiesInRange(entities);
            }
        }
        return communicableEntitiesInRange;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity sender = Entity.findEntity(entities, fromId);
        Entity reciever = Entity.findEntity(entities, toId);

        sender.checkFileExistsSender(fileName);
        sender.checkSpareSendingBandwidth(sender.getId());
        reciever.checkSpareReceivingBandwidth(reciever.getId());
        reciever.checkFileExistsReceiver(fileName);
        reciever.checkStorage(sender.getFilesAmountOfBytes(fileName));
        sender.createPartialFile(fileName, sender.getFilesContent(fileName), reciever, sender);
    }
}
