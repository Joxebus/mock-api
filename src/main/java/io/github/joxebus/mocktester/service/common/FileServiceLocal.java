package io.github.joxebus.mocktester.service.common;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.joxebus.mocktester.model.FileResponse;
import io.github.joxebus.mocktester.model.FileResponseError;

@Service
public class FileServiceLocal implements FileService {

    @Value("${file.upload.folder}")
    private String fileUploadFolder;

    @PostConstruct
    private void init() {
        File file = new File(fileUploadFolder);
        if(!file.exists() && !file.mkdirs()) {
            System.exit(1);
        }
    }

    @Override
    public FileResponse upload(String filename, File file) {
        FileResponse fileResponse = new FileResponse();
        try {
            if(Objects.isNull(file)) {
                throw new Exception("Failed to create empty file");
            }

            String name = filename.isBlank() ? UUID.randomUUID().toString() : filename;
            File destination = new File(fileUploadFolder, name);
            FileOutputStream fos = new FileOutputStream(destination);
            fos.write(Files.readAllBytes(file.getAbsoluteFile().toPath()));
            fos.close();

            fileResponse.setSuccess(true);
        } catch(Exception e) {
            fileResponse.setError(FileResponseError.newError(e.getMessage()));
        }
        return fileResponse;
    }

    @Override
    public FileResponse download(String filename) {
        FileResponse fileResponse = new FileResponse();
        try {
            File downloaded = new File(fileUploadFolder, filename);
            if(!downloaded.exists()) {
                throw new Exception("File [${filename}] doesn't exist");
            }
            fileResponse.setSuccess(true);
            fileResponse.setFile(downloaded);
        } catch(Exception e) {
            fileResponse.setError(FileResponseError.newError(e.getMessage()));
        }
        return fileResponse;
    }
}
