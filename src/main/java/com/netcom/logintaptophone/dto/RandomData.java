package com.netcom.logintaptophone.dto;

import javax.crypto.spec.IvParameterSpec;

public final class RandomData {
    private final byte[] key;
    private final byte[] iv;
    private IvParameterSpec ivParameterSpec;

    public RandomData(final byte[] key, final byte[] iv, IvParameterSpec ivParameterSpec) {
        this.key = key;
        this.iv = iv;
        this.ivParameterSpec = ivParameterSpec;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }

    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
        this.ivParameterSpec = ivParameterSpec;
    }

    @Override
    public String toString() {
        return "RandomData{" + '}';
    }
}
