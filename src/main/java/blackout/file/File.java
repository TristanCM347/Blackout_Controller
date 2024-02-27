package blackout.file;

import blackout.entity.Entity;
import response.models.FileInfoResponse;

public class File {
    private String fileName;
    private String data;
    private String content;
    private Entity ownerEntity;
    private Entity senderEntity;

    public File(String fileName, String data, String content, Entity ownerEntity) {
        this.fileName = fileName;
        this.content = content;
        this.ownerEntity = ownerEntity;
        this.senderEntity = null;
        this.data = data;
    }

    public File(String fileName, String data, String content, Entity ownerEntity, Entity senderEntity) {
        this.fileName = fileName;
        this.content = content;
        this.ownerEntity = ownerEntity;
        this.senderEntity = senderEntity;
        this.data = data;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Entity getOwnerEntity() {
        return this.ownerEntity;
    }

    public void setOwnerEntity(Entity ownerEntity) {
        this.ownerEntity = ownerEntity;
    }

    public Entity getSenderEntity() {
        return this.senderEntity;
    }

    public void setSenderEntity(Entity senderEntity) {
        this.senderEntity = senderEntity;
    }

    public String getOwnerEntityId() {
        return this.ownerEntity.getId();
    }

    public int getFileSize() {
        return this.content.length();
    }

    public boolean isFileComplete() {
        return this.data.length() == this.content.length();
    }

    public int getOwnerEntitiesReceivingSpeed() {
        return this.ownerEntity.getReceivingSpeed();
    }

    public void removeFileFromOwnerEntity() {
        this.ownerEntity.removeFile(this.fileName);
    }

    public void removeFileUploadFromSenderEntity() {
        this.senderEntity.removeFileUpload(this.fileName);
    }

    public void addData(int bytes) {
        int i = 0;
        while (!this.isFileComplete() && i < bytes) {
            int dataLength = this.data.length();
            char newByteData = content.charAt(dataLength);
            this.data = this.data + newByteData;
            i++;
        }
    }

    public void downloadRestOfFile() {
        this.data = this.content;
    }

    public FileInfoResponse getFileInfoResponse() {
        return new FileInfoResponse(this.fileName, this.data, this.getFileSize(), this.isFileComplete());
    }

    public void removeAllTsFromContent() {
        StringBuilder newContent = new StringBuilder();
        for (int i = 0; i < this.getFileSize(); i++) {
            char c = this.content.charAt(i);
            if (c != 't') {
                newContent.append(c);
            }
        }
        this.content = newContent.toString();
        this.data = this.content;
    }

    public void removeRestOfTsFromContent() {
        StringBuilder newContent = new StringBuilder();
        newContent.append(this.data);
        for (int i = this.data.length(); i < this.getFileSize(); i++) {
            char c = this.content.charAt(i);
            if (c != 't') {
                newContent.append(c);
            }
        }
        this.content = newContent.toString();
    }

    public void removeAllTsFromOriginalFile() {
       for (File file: senderEntity.getFiles()) {
            if (file.getFileName().equals(this.fileName)) {
                file.removeAllTsFromContent();
            }
       }
    }
}
