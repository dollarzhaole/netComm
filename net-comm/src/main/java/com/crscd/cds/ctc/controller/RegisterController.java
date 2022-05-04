package com.crscd.cds.ctc.controller;

/**
 * 注册转发包的控制器。当第一次连接时发送注册xml，此后断开重连时，不再发送
 *
 * @author zhaole
 * @date 2022-05-04
 */
public class RegisterController {
    private boolean registered = false;
    private boolean negotiated = false;

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered() {
        this.registered = true;
    }

    public boolean isNegotiated() {
        return negotiated;
    }

    public void setNegotiated() {
        this.negotiated = negotiated;
    }
}
