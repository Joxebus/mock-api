package io.github.joxebus.mocktester.service;

import java.io.File;
import java.util.List;

import io.github.joxebus.mocktester.model.FileResponse;

public interface FileService {

    FileResponse upload(String filename, File file);

    FileResponse download(String filename);

    List<String> filesWithExtension(String extension);
}
