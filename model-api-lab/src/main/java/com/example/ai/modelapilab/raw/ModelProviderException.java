package com.example.ai.modelapilab.raw;

public class ModelProviderException extends RuntimeException {

    private final Integer providerStatus;

    public ModelProviderException(String message) {
        this(message, null, null);
    }

    public ModelProviderException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public ModelProviderException(String message, Integer providerStatus) {
        this(message, providerStatus, null);
    }

    private ModelProviderException(String message, Integer providerStatus, Throwable cause) {
        super(message, cause);
        this.providerStatus = providerStatus;
    }

    public Integer providerStatus() {
        return providerStatus;
    }
}
