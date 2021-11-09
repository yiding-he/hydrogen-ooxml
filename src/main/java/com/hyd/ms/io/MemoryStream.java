package com.hyd.ms.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MemoryStream extends Stream {

    private static class MyByteArrayOutputStream extends ByteArrayOutputStream {

        public byte[] getBuf() {
            return this.buf;
        }
    }

    private final MyByteArrayOutputStream data = new MyByteArrayOutputStream();

    @Override
    public long length() {
        return this.data.size();
    }

    @Override
    public void setLength(long length) {

    }

    @Override
    public InputStream read() {
        return new ByteArrayInputStream(this.data.toByteArray());
    }

    @Override
    public OutputStream write() {
        this.data.reset();
        return this.data;
    }
}
