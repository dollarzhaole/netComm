package com.crscd.cds.ctc.protocol;

/**
 * @author zhaole
 * @date 2022-04-14
 */
public class NetAddress {
    private short bureauType;
    private short bureauCode;
    private short sysType;
    private short sysId;
    private short unitType;
    private int unitId;
    private short devType;
    private int devId;
    private short procType;
    private int procId;
    private short userType;
    private long userId;

    public short getBureauType() {
        return bureauType;
    }

    public void setBureauType(short bureauType) {
        this.bureauType = bureauType;
    }

    public short getBureauCode() {
        return bureauCode;
    }

    public void setBureauCode(short bureauCode) {
        this.bureauCode = bureauCode;
    }

    public short getSysType() {
        return sysType;
    }

    public void setSysType(short sysType) {
        this.sysType = sysType;
    }

    public short getSysId() {
        return sysId;
    }

    public void setSysId(short sysId) {
        this.sysId = sysId;
    }

    public short getUnitType() {
        return unitType;
    }

    public void setUnitType(short unitType) {
        this.unitType = unitType;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public short getDevType() {
        return devType;
    }

    public void setDevType(short devType) {
        this.devType = devType;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public short getProcType() {
        return procType;
    }

    public void setProcType(short procType) {
        this.procType = procType;
    }

    public int getProcId() {
        return procId;
    }

    public void setProcId(int procId) {
        this.procId = procId;
    }

    public short getUserType() {
        return userType;
    }

    public void setUserType(short userType) {
        this.userType = userType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}