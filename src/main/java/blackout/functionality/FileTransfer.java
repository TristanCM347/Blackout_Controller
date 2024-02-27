package blackout.functionality;

import java.util.ArrayList;

import blackout.FileTransferException.VirtualFileAlreadyExistsException;
import blackout.FileTransferException.VirtualFileNoBandwidthException;
import blackout.FileTransferException.VirtualFileNoStorageSpaceException;
import blackout.FileTransferException.VirtualFileNotFoundException;
import blackout.entity.Entity;

public interface FileTransfer {
    public void checkFileExistsSender(String fileName) throws VirtualFileNotFoundException;

    public void checkFileExistsReceiver(String fileName) throws VirtualFileAlreadyExistsException;

    public int getSendingBandwidth();

    public int getRecievingBandwidth();

    public int getAmountFilesUploading();

    public int getAmountFilesDownloading();

    default int getSendingSpeed() {
        if (this.getAmountFilesUploading() == 0) {
            return this.getSendingBandwidth();
        } else {
            return this.getSendingBandwidth() / this.getAmountFilesUploading();
        }
    }

    default int getReceivingSpeed() {
        if (this.getAmountFilesDownloading() == 0) {
            return this.getRecievingBandwidth();
        } else {
            return this.getRecievingBandwidth() / this.getAmountFilesDownloading();
        }
    }

    default void checkSpareSendingBandwidth(String id) throws VirtualFileNoBandwidthException {
        if (this.getAmountFilesUploading() >= this.getSendingBandwidth()) {
            throw new VirtualFileNoBandwidthException(id);
        }
    }

    default void checkSpareReceivingBandwidth(String id) throws VirtualFileNoBandwidthException {
        if (this.getAmountFilesDownloading() >= this.getRecievingBandwidth()) {
            throw new VirtualFileNoBandwidthException(id);
        }
    }

    default void checkStorage(int newFileSize) throws VirtualFileNoStorageSpaceException {
        if (this.getAmountOfFiles() >= this.getMaxFileStorage()) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }
        if (this.getUsedStorage() + newFileSize > this.getMaxByteStorage()) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
    }

    public int getAmountOfFiles();

    public int getUsedStorage();

    public int getMaxFileStorage();

    public int getMaxByteStorage();

    public void transferDataFromThisEntity();

    public void removeOutOfRangeTransfers(ArrayList<Entity> entities);

}
