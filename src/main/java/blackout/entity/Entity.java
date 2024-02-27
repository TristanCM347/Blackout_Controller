package blackout.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import blackout.FileTransferException.VirtualFileAlreadyExistsException;
import blackout.FileTransferException.VirtualFileNotFoundException;
import blackout.entity.device.Device;
import blackout.entity.satellite.RelaySatellite;
import blackout.entity.satellite.Satellite;
import blackout.file.File;
import blackout.functionality.FileTransfer;
import response.models.EntityInfoResponse;
import response.models.FileInfoResponse;
import utils.Angle;
import utils.MathsHelper;

public abstract class Entity implements FileTransfer {
    private String id;
    private double height;
    private Angle position;
    private int range;
    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<File> fileTransferList = new ArrayList<>();

    public Entity(String id, double height, Angle position, int range) {
        this.id = id;
        this.height = height;
        this.position = position;
        this.range = range;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Angle getPosition() {
        return this.position;
    }

    public void setPosition(Angle position) {
        double degrees = position.toDegrees() % 360;
        if (degrees < 0) {
            degrees += 360;
        }
        Angle validPosition = Angle.fromDegrees(degrees);
        this.position = validPosition;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public ArrayList<File> getFiles() {
        return this.files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public ArrayList<File> getFileTransferList() {
        return fileTransferList;
    }

    public void setFileTransferList(ArrayList<File> fileTransferList) {
        this.fileTransferList = fileTransferList;
    }

    public void addFileUploading(File file) {
        fileTransferList.add(file);
    }

    public void addFile(File file) {
        files.add(file);
    }

    public void removeFile(String fileName) {
        files.removeIf(file -> file.getFileName().equals(fileName));
    }

    public void removeFileUpload(String fileName) {
        fileTransferList.removeIf(file -> file.getFileName().equals(fileName));
    }

    public int getFilesAmountOfBytes(String fileName) {
        for (File file : files) {
            if (file.getFileName().equals(fileName)) {
                return file.getFileSize();
            }
        }
        return -1;
    }

    public String getFilesContent(String fileName) {
        for (File file : files) {
            if (file.getFileName().equals(fileName)) {
                return file.getContent();
            }
        }
        return null;
    }

    public void createCompleteFile(String filename, String content) {
        File file = new File(filename, content, content, this);
        this.addFile(file);
    }

    public void createPartialFile(String fileName, String content, Entity reciever, Entity sender) {
        File file = new File(fileName, "", content, reciever, sender);
        this.addFileUploading(file);
        reciever.addFile(file);
    }

    public EntityInfoResponse entityInfoResponse() {
        Map<String, FileInfoResponse> filesMap = new HashMap<>();
        for (File file : files) {
            filesMap.put(file.getFileName(), file.getFileInfoResponse());
        }
        return new EntityInfoResponse(this.id, this.position, this.height, this.getClass().getSimpleName(), filesMap);
    }

    public void moveSatellites() {
        if (this instanceof Satellite) {
            Satellite satellite = (Satellite) this;
            satellite.move();
        }
    }

    @Override
    public void removeOutOfRangeTransfers(ArrayList<Entity> entities) {
        List<String> communicableEntitiesInRange = this.findCommunicableEntitiesInRange(entities);
        fileTransferList.removeIf(uploadingFile -> {
            if (!communicableEntitiesInRange.contains(uploadingFile.getOwnerEntityId())) {
                uploadingFile.removeFileFromOwnerEntity();
                return true;
            }
            return false;
        });
    }

    @Override
    public void transferDataFromThisEntity() {
        int sendingSpeed = this.getSendingSpeed();
        fileTransferList.removeIf(downloadingFile -> {
            int receivingSpeed = downloadingFile.getOwnerEntitiesReceivingSpeed();
            int transferRate = Math.min(sendingSpeed, receivingSpeed);
            downloadingFile.addData(transferRate);
            return downloadingFile.isFileComplete();
        });
    }

    @Override
    public void checkFileExistsSender(String fileName) throws VirtualFileNotFoundException {
        boolean fileExists = files.stream()
            .anyMatch(file -> file.getFileName().equals(fileName) && file.isFileComplete());

        if (!fileExists) {
            throw new VirtualFileNotFoundException(fileName);
        }
    }

    @Override
    public void checkFileExistsReceiver(String fileName) throws VirtualFileAlreadyExistsException {
        boolean fileExists = files.stream()
            .anyMatch(file -> file.getFileName().equals(fileName));

        if (fileExists) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }
    }

    @Override
    public int getAmountOfFiles() {
        return this.files.size();
    }

    @Override
    public int getAmountFilesUploading() {
        return fileTransferList.size();
    }

    @Override
    public int getAmountFilesDownloading() {
        return (int) files.stream()
            .filter(file -> !file.isFileComplete()).count();
    }

    @Override
    public int getUsedStorage() {
        return files.stream()
            .mapToInt(File::getFileSize).sum();
    }

    public ArrayList<String> findCommunicableEntitiesInRange(ArrayList<Entity> entities) {
        ArrayList<Entity> communicableEntitiesInRange = new ArrayList<>();
        ArrayList<Entity> relaysVisited = new ArrayList<>();
        Stack<Entity> relayStack = new Stack<>();
        relayStack.push(this);
        relaysVisited.add(this);

        while (!relayStack.isEmpty()) {
            Entity node = relayStack.pop();
            // get neighbours and add to communicable enitities
            ArrayList<Entity> neighbours = node.getEntitiesInRange(entities);
            communicableEntitiesInRange.addAll(
                neighbours.stream().filter(entity -> !communicableEntitiesInRange.contains(entity))
                .collect(Collectors.toList())
            );

            // add all neighbour relays to stack to repeat dfs search
            for (Entity entity : neighbours) {
                if (!relaysVisited.contains(entity) && entity instanceof RelaySatellite) {
                    relayStack.push(entity);
                    relaysVisited.add(entity);
                }
            }
        }
        ArrayList<String> validCommunicableEntitiesInRange = communicableEntitiesInRange.stream()
            .filter(this::isEntityCompatible)
            .map(Entity::getId).collect(Collectors.toCollection(ArrayList::new));

        return validCommunicableEntitiesInRange;
    }

    private ArrayList<Entity> getEntitiesInRange(ArrayList<Entity> entities) {
        ArrayList<Entity> entitiesInRange = new ArrayList<>();
        double range = this.getRange();
        for (Entity entity : entities) {
            if (this instanceof Satellite && entity instanceof Satellite
            && MathsHelper.isVisible(entity.getHeight(), entity.getPosition(), this.getHeight(), this.getPosition())
            && MathsHelper.getDistance(this.getHeight(), this.getPosition(), entity.getHeight(), entity.getPosition())
            <= range) {
                entitiesInRange.add(entity);
            } else if (this instanceof Device && entity instanceof Satellite
            && MathsHelper.isVisible(entity.getHeight(), entity.getPosition(), this.getPosition())
            && MathsHelper.getDistance(entity.getHeight(), entity.getPosition(), this.getPosition())
            <= range) {
                entitiesInRange.add(entity);
            } else if (this instanceof Satellite && entity instanceof Device
            && MathsHelper.isVisible(this.getHeight(), this.getPosition(), entity.getPosition())
            && MathsHelper.getDistance(this.getHeight(), this.getPosition(), entity.getPosition())
            <= range) {
                entitiesInRange.add(entity);
            }
        }
        return entitiesInRange;
    }

    public boolean isEntityCompatible(Entity entity) {
        return !this.equals(entity);
    }

    public static Entity findEntity(ArrayList<Entity> entities, String id) {
        return entities.stream()
            .filter(entity -> entity.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Entity otherEntity = (Entity) obj;
        return
            id.equals(otherEntity.id);
    }
}
