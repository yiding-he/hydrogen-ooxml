package com.hyd.ms.io;

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
    public void close() throws IOException {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public abstract long length();

    public abstract void setLength(long length);

    public abstract InputStream read();

    public abstract OutputStream write();
}
