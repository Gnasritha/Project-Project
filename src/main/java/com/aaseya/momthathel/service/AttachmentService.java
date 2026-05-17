package com.aaseya.momthathel.service;

import com.aaseya.momthathel.dao.AttachmentRepository;
import com.aaseya.momthathel.dao.InspectionCaseRepository;
import com.aaseya.momthathel.dto.AttachmentResponse;
import com.aaseya.momthathel.model.Attachment;
import com.aaseya.momthathel.model.InspectionCase;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private InspectionCaseRepository caseRepository;

    @Value("${momthathel.attachments.storage-path:./uploads/attachments}")
    private String storageRoot;

    @PostConstruct
    public void ensureStorageDirectoryExists() {
        try {
            Files.createDirectories(Path.of(storageRoot));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create attachment storage directory: " + e.getMessage());
        }
    }

    @Transactional
    public AttachmentResponse upload(Long inspectionId, MultipartFile file) {

        AttachmentResponse response = new AttachmentResponse();

        try {
            if (inspectionId == null) {
                throw new RuntimeException("inspectionId is required");
            }
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is required and cannot be empty.");
            }

            InspectionCase inspection = caseRepository.findById(inspectionId);
            if (inspection == null) {
                throw new RuntimeException("Inspection not found: " + inspectionId);
            }

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.bin";
            String safeName = sanitize(originalName);
            String storedName = UUID.randomUUID() + "_" + safeName;

            Path inspectionDir = Path.of(storageRoot, String.valueOf(inspectionId));
            Files.createDirectories(inspectionDir);
            Path target = inspectionDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Attachment attachment = new Attachment();
            attachment.setInspectionCase(inspection);
            attachment.setFileName(originalName);
            attachment.setFilePath(target.toString());
            attachment.setFileType(file.getContentType());

            Attachment saved = attachmentRepository.save(attachment);

            toResponse(saved, response);
            response.setSuccess(true);
            response.setMessage("Attachment uploaded successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<AttachmentResponse> list(Long inspectionId) {
        try {
            if (inspectionId == null) {
                throw new RuntimeException("inspectionId is required");
            }
            if (!caseRepository.existsById(inspectionId)) {
                throw new RuntimeException("Inspection not found: " + inspectionId);
            }

            List<Attachment> rows = attachmentRepository.findByInspectionId(inspectionId);
            List<AttachmentResponse> out = new ArrayList<>();
            for (Attachment a : rows) {
                AttachmentResponse r = new AttachmentResponse();
                toResponse(a, r);
                out.add(r);
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long inspectionId, Long attachmentId) {
        try {
            Attachment attachment = attachmentRepository.findByIdAndInspectionId(attachmentId, inspectionId);
            if (attachment == null) {
                throw new RuntimeException(
                        "Attachment " + attachmentId + " not found for inspection " + inspectionId);
            }

            try {
                Files.deleteIfExists(Path.of(attachment.getFilePath()));
            } catch (IOException ignored) {
                // file-deletion failure is non-fatal; DB metadata removal still proceeds
            }
            attachmentRepository.delete(attachment);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void assertHasAtLeastOneAttachment(Long inspectionId) {
        try {
            long count = attachmentRepository.countByInspectionId(inspectionId);
            if (count == 0) {
                throw new RuntimeException("At least one attachment is required to proceed with the inspection.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void toResponse(Attachment a, AttachmentResponse r) {
        r.setAttachmentId(a.getAttachmentId());
        r.setInspectionId(a.getInspectionCase() != null ? a.getInspectionCase().getInspectionId() : null);
        r.setFileName(a.getFileName());
        r.setFileType(a.getFileType());
        r.setFilePath(a.getFilePath());
        r.setUploadedAt(a.getUploadedAt());
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
