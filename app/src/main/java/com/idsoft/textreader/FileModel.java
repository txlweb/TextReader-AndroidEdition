package com.idsoft.textreader;
public class FileModel {
    private String fileName;
    private String title_name;
    private long fileSize;
    // 其他文件属性...

    public FileModel(String fileName, String titleName, long fileSize) {
        this.fileName = fileName;
        this.title_name = titleName;
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

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }

    // Getter 和 Setter 方法...
}
