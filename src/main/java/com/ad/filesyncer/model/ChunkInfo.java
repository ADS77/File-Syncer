package com.ad.filesyncer.model;

import java.util.Objects;

public class ChunkInfo {
    private long weakCheakSum;
    private String strongCheckSum;

    public ChunkInfo(long weakCheakSum, String strongCheckSum) {
        this.weakCheakSum = weakCheakSum;
        this.strongCheckSum = strongCheckSum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkInfo chunkInfo = (ChunkInfo) o;
        return weakCheakSum == chunkInfo.weakCheakSum && Objects.equals(strongCheckSum, chunkInfo.strongCheckSum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weakCheakSum, strongCheckSum);
    }
}
