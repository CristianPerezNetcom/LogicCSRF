package com.netcom.logintaptophone.dto;

import lombok.Getter;

@Getter
public final class AtallaHostDTO {

    private final String ip;
    private final int port;
    private final int timeOut;

    public AtallaHostDTO(String ip, int port, int timeOut) {
        this.ip = ip;
        this.port = port;
        this.timeOut = timeOut;
    }

}
