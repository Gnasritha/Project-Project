package com.aaseya.momthathel.dto;

import java.time.LocalDateTime;

public class AttachmentResponse {

    private Long attachmentId;
    private Long inspectionId;
    private String fileName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadedAt;

    private boolean success;
    private String message;

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }

    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
