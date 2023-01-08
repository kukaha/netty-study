package netty.privateprotocol;

/**
 * netty 消息
 */
public final class NettyMessage {

    /**
     * 长度：变长 消息头定义
     */
    private Header header;

    /**
     * 长度：变长 对于请求消息，它是方法的参数、对于响应消息，它是返回值
     */
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
