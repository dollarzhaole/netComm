package com.crscd.cds.spring.netcomm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author zhaole
 * @date 2022-04-24
 */
@ConfigurationProperties(prefix = "spring.net-comm")
public class NetCommProperties {
    private Server server1;
    @Nullable
    private Server server2;
    private LocalAddress local;
    private ReceiveFilter inCondition;

    public Server getServer1() {
        return server1;
    }

    public void setServer1(Server server1) {
        this.server1 = server1;
    }

    @Nullable
    public Server getServer2() {
        return server2;
    }

    public void setServer2(@Nullable Server server2) {
        this.server2 = server2;
    }

    public LocalAddress getLocal() {
        return local;
    }

    public void setLocal(LocalAddress local) {
        this.local = local;
    }

    public ReceiveFilter getInCondition() {
        return inCondition;
    }

    public void setInCondition(ReceiveFilter inCondition) {
        this.inCondition = inCondition;
    }

    public static class ReceiveFilter {
        private Collection<TypeFunc> rec;

        public Collection<TypeFunc> getRec() {
            return rec;
        }

        public void setRec(Collection<TypeFunc> rec) {
            this.rec = rec;
        }
    }

    public static class TypeFunc {
        private short type;
        private short func;

        public short getType() {
            return type;
        }

        public void setType(short type) {
            this.type = type;
        }

        public short getFunc() {
            return func;
        }

        public void setFunc(short func) {
            this.func = func;
        }
    }

    public static class Server {
        private String host;
        private Integer port;
        private String localHost;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getLocalHost() {
            return localHost;
        }

        public void setLocalHost(String localHost) {
            this.localHost = localHost;
        }
    }

    public static class LocalAddress {
        private Short bureauCode;
        private UnitTypeEnum unitType;
        private Integer unitId;

        public enum UnitTypeEnum {
            CENTER((short) 0x01),
            STATION((short) 0x02),
            ;

            private final short value;

            public short getValue() {
                return this.value;
            }

            UnitTypeEnum(short value) {
                this.value = value;
            }
        }

        public Short getBureauCode() {
            return bureauCode;
        }

        public void setBureauCode(Short bureauCode) {
            this.bureauCode = bureauCode;
        }

        public UnitTypeEnum getUnitType() {
            return unitType;
        }

        public void setUnitType(UnitTypeEnum unitType) {
            this.unitType = unitType;
        }

        public Integer getUnitId() {
            return unitId;
        }

        public void setUnitId(Integer unitId) {
            this.unitId = unitId;
        }
    }
}
