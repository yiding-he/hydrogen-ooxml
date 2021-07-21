package com.hyd.ms.io;

public enum FileMode {
    CreateNew(false),
    Create(false),
    Open(false),
    OpenOrCreate(true),
    Truncate(false),
    Append(false);

    private final boolean available;

    FileMode(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}
