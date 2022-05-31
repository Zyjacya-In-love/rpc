package cn.krone.rpc.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消费者 向 提供者 发送的 请求类
 * @author xzq
 * @create 2022-05-31-21:20
 */
@Data // 提供类所有属性的 get 和 set 方法，此外还提供了equals、canEqual、hashCode、toString 方法。
@AllArgsConstructor // 注解在 类 上；为类提供一个全参的构造方法，加了这个注解后，类中不提供默认构造方法了。
@NoArgsConstructor // 注解在 类 上；为类提供一个无参的构造方法。
@Builder // 建造者模式
public class RpcRequest implements Serializable { // 因为要网络传输实现 Serializable 接口以序列化
    // 接口名
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数类型
    private Class<?>[] paramTypes;
    // 参数实际值
    private Object[] args;
}
