package com.hyd.ms.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileStream extends Stream {

    private final String filePath;

    public FileStream(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public long length() {
        return new File(filePath).length();
    }

    @Override
    public void setLength(long length) {

    }

    @Override
    public InputStream read() {
        try {
            return setInputStream(Files.newInputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    @Override
    public OutputStream write() {
        try {
            return setOutputStream(Files.newOutputStream(Paths.get(filePath)));
        } catch (IOException e) {
            throw new IoException(e);
        }
    }
}
