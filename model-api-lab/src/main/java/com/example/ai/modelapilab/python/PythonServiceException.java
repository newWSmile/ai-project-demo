package com.example.ai.modelapilab.python;

/** Python 服务网络失败、非成功状态码或响应结构异常。 */
public final class PythonServiceException extends RuntimeException {

    private final Integer upstreamStatus;

    public PythonServiceException(String message, Integer upstreamStatus, Throwable cause) {
        super(message, cause);
        this.upstreamStatus = upstreamStatus;
    }

    public Integer upstreamStatus() {
        return upstreamStatus;
    }
}

