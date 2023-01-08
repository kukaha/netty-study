package netty.privateprotocol;

public enum MessageType {

    BUSINESS_REQ(0, "业务请求消息"),

    BUSINESS_RESP(1, "业务响应消息"),

    ONE_WAY(2, "业务既是请求又是响应消息"),

    LOGIN_REQ(3, "握手请求消息"),

    LOGIN_RESP(4, "握手应答消息"),

    HEARTBEAT_REQ(5, "心跳请求"),

    HEARTBEAT_RESP(6, "心跳响应");

    /**
     * Describe
     */
    private final String describe;
    /**
     * Code
     */
    private final byte code;

    /**
     * Message type
     *
     * @param code     code
     * @param describe describe
     * @since 1.0
     */
    MessageType(Integer code, String describe) {
        this.describe = describe;
        this.code = code.byteValue();
    }

    public String getDescribe() {
        return describe;
    }

    public byte getCode() {
        return code;
    }
}
