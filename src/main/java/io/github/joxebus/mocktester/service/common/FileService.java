package io.github.joxebus.mocktester.service.common;

import java.io.File;

import io.github.joxebus.mocktester.model.FileResponse;

public interface FileService {

    FileResponse upload(String filename, File file);

    FileResponse download(String filename);
}
