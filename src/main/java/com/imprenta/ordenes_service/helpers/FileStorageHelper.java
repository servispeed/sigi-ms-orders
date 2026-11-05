package com.imprenta.ordenes_service.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.imprenta.ordenes_service.exception.FileStorageException;

@Service
public class FileStorageHelper {
    private final Path fileStorageLocation;
    private final Path receiptsStorageLocation;

    public FileStorageHelper(
            @Value("${file.upload-dir:/app/uploads}") String uploadDir,
            @Value("${file.receipts-dir:/app/receipts}") String receiptsDir) {

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.receiptsStorageLocation = Paths.get(receiptsDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.receiptsStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("No se pudo crear los directorios de archivos.", ex);
        }
    }

    public String guardarArchivo(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "archivo_desconocido";

        String fileName = UUID.randomUUID().toString() + "_" + originalName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("No se pudo guardar el archivo " + fileName, ex);
        }
    }

    public Path cargarArchivo(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    public String guardarComprobante(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "comprobante_desconocido";

        String fileName = UUID.randomUUID().toString() + "_" + originalName;

        try {
            Path targetLocation = this.receiptsStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("No se pudo guardar el comprobante " + fileName, ex);
        }
    }

    public Path cargarComprobante(String fileName) {
        return this.receiptsStorageLocation.normalize().resolve(fileName).normalize();
    }
}