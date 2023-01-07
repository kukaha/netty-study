package netty.protobuf.test;

import cn.hutool.core.util.RandomUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import netty.protobuf.SubscribeReqProto;

import java.util.ArrayList;
import java.util.List;

public class TestSubscribe {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        List<String> address = new ArrayList<>();
        address.add("天津");
        address.add("北京");
        SubscribeReqProto.SubscribeReq subscribeReq =
                SubscribeReqProto.SubscribeReq
                        .newBuilder()
                        .setSubReqID(RandomUtil.randomInt(6))
                        .setUserName("董晓斌")
                        .setProductName("python书")
                        .addAllAddress(address)
                        .build();
        System.out.println(subscribeReq.toBuilder());
        System.out.println("---------------------------------");

        byte[] bytes = subscribeReq.toByteArray();
        SubscribeReqProto.SubscribeReq subscribeReq1 = SubscribeReqProto.SubscribeReq.parseFrom(bytes);
        System.out.println(subscribeReq1);

        System.out.println("---------------------------------");

        System.out.println(subscribeReq1.equals(subscribeReq));

    }

}
