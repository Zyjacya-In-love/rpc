package cn.krone.rpc.common.exchange;

import lombok.*;

import java.io.Serializable;

/**
 * 提供者 执行完成或出错后 向 消费者 返回的 结果类
 * @author xzq
 * @create 2022-05-31-21:21
 */
@Data // 提供类所有属性的 get 和 set 方法，此外还提供了equals、canEqual、hashCode、toString 方法。
@AllArgsConstructor // 注解在 类 上；为类提供一个全参的构造方法，加了这个注解后，类中不提供默认构造方法了。
@NoArgsConstructor // 注解在 类 上；为类提供一个无参的构造方法。
public class RpcResponse extends RpcMessage { // 因为要网络传输实现 Serializable 接口以序列化
    // 状态码
    private int statusCode;
    // 出错的说明信息
    private String message;
    // 执行完成得到的返回结果数据，泛型
    private Object data;

    public static RpcResponse success(Object data) {
        RpcResponse response = new RpcResponse();
        response.setStatusCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    public static RpcResponse fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse response = new RpcResponse();
        response.setStatusCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

    /**
     * 来源于 javaGuide：https://github.com/Snailclimb/guide-rpc-framework/blob/master/rpc-framework-common/src/main/java/github/javaguide/enums/RpcResponseCodeEnum.java
     * @author shuang.kou
     * @createTime 2020年05月12日 16:24:00
     */
    @AllArgsConstructor
    @Getter
    @ToString
    public enum RpcResponseCodeEnum {

        SUCCESS(200, "The remote call is successful"),
        FAIL(500, "The remote call is fail");

        private final int code;
        private final String message;

    }
}
