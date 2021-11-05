package com.hyd.ms.io;

import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Stream implements Closeable {

    private InputStream inputStream;

    private OutputStream outputStream;

    protected InputStream getInputStream() {
        return inputStream;
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }

    protected InputStream setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return inputStream;
    }

    protected OutputStream setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return outputStream;
    }

    @Override
    public void close() {
        try {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    public void copyTo(Stream targetStream) {
        try {
            IOUtils.copy(read(), targetStream.write());
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    public void writeBytes(byte[] bytes) {
        try {
            write().write(bytes);
        } catch (IOException e) {
            throw new IoException(e);
        }
    }

    public abstract long length();

    public abstract void setLength(long length);

    public abstract InputStream read();

    public abstract OutputStream write();
}
