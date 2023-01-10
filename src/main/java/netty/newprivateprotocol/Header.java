package netty.newprivateprotocol;

import java.util.HashMap;
import java.util.Map;


/**
 * netty 协议头
 */
public final class Header {

    /**
     * 长度：32 netty消息的校验码，它由三部分组成：
     * 1) 0xABEF：固定值表面该消息是netty协议消息，2个字节；
     * 2) 主版本号：1~255，1个字节；
     * 3) 次版本号：1~255，1个字节；
     * crcCode = 0xABEF + 主版本号 + 次版本号
     */
    private int crcCode = 0xABEF0101;

    /**
     * 长度：32 消息长度，整个消息，包括消息头和消息体
     */
    private int length;

    /**
     * 长度：64 集群节点内全局唯一，由会话ID生成器生成
     */
    private long sessionID;

    /**
     * 长度：8
     * 0：业务请求消息
     * 1：业务响应消息
     * 2：业务ONE WAY 消息（既是请求又是响应消息）
     * 3：握手请求消息
     * 4：握手应答消息
     * 5：心跳请求消息
     * 6：心跳应答消息
     */
    private byte type;

    /**
     * 长度：8 消息优先级：0~255
     */
    private byte priority;

    /**
     * 长度：变长 可选字段，用于扩展消息头
     */
    private Map<String, Object> attachment = new HashMap<>();

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionID=" + sessionID +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
