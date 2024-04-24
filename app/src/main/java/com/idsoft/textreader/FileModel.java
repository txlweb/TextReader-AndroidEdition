package com.idsoft.textreader;
public class FileModel {
    private String fileName;
    private long fileSize;
    // 其他文件属性...

    public FileModel(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Getter 和 Setter 方法...
}
