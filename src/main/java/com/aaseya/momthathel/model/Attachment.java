package com.aaseya.momthathel.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Maps to the {@code ATTACHMENTS} table.
 */
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachments_seq")
    @SequenceGenerator(name = "attachments_seq", sequenceName = "attachments_attachment_id_seq", allocationSize = 1)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attachment_inspection"))
    private InspectionCase inspectionCase;

    @Column(name = "file_name", nullable = false, length = 300)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public Attachment() {}

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }

    public InspectionCase getInspectionCase() { return inspectionCase; }
    public void setInspectionCase(InspectionCase inspectionCase) { this.inspectionCase = inspectionCase; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
