# rpc



## Introduction

一个直观简单、高可配置的 RPC 框架。编程语言为 Java，目前网络编程框架是 Netty，注册中心采用 zookeeper 实现。



### 各个模块作用

`api` 服务消费端要调用的服务的接口，服务提供端存在实现接口的服务

`common` 多个模块可能会用到的 异常、实体、SPI、工具类 等

`consumer` 动态代理实现，利用注册中心进行服务发现

`loadbalance` 负载均衡

`provider` 服务发布，将服务注册到注册中心，针对服务请求通过反射调用实现类

`registry `注册中心，实现将服务注册到注册中心，提供已注册服务发现功能

`serialization` 序列化反序列化

`transport` 网络传输，实现服务提供端和服务消费端通信，eg：发送 服务请求 和 解析返回 服务响应

`test-client` 测试用的 服务消费端，通过 consumer 中的代理调用想要调用的服务

`test-server` 测试用的 服务提供端，通过 provider 进行服务发布和服务的使用



### 动态代理

由于在客户端这一侧我们并没有接口的具体实现类，就没有办法直接生成实例对象。这时，我们可以通过动态代理的方式生成实例，并且调用方法时生成需要的 RpcRequest 对象并且发送给服务端。目的是屏蔽调用过程中的 通信、负载均衡、容错 等。



### 反射

服务端提供通过反射调用请求的方法，返回方法的返回值。





## 快速开始

### 依赖版本，详见 pom.xml

Java：1.8.0_291

zookeeper：apache-zookeeper-3.5.10、curator 4.2.0

netty：4.1.39.Final

序列化：jackson：2.11.0、kryo 4.0.2、protostuff 1.7.2

其他：lombok 1.18.24、logback 1.2.3



### 远程调用样例代码：

服务提供端

```java
public class Server {
    public static void main(String[] args) {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = 9000;
        AddService addService = new AddServiceImpl();
        ServiceProvider serviceProvider = SingletonFactory.getInstance(
                                                                        ServiceProviderImpl.class,
                                                                        new Class[]{String.class, int.class},
                                                                        new Object[]{host, port});
        serviceProvider.register(addService);
        RpcServer rpcServer = new NettyRpcServer();
        rpcServer.start(port);
    }
}
```

服务消费端

```java
public class Client {
    public static void main(String[] args) {
        RpcProxyFactory rpcClientProxy = new RpcProxyFactoryJdkImpl();
        AddService addService = rpcClientProxy.getProxy(AddService.class);
        int addRes = addService.add(1, 2);
        int addRes2 = addService.add(2, 2);
        System.out.println("addRes : " + addRes);
        System.out.println("addRes2 : " + addRes2);
    }
}
```



## SPI

通过 SPI，几乎所有组件均可使用配置替换，并且扩展方便。



目前可配置的项及可选字段：

1. 网络编程框架（`rpc-consumer/src/main/resources/consumer.properties` 中，`client.transport` 字段可选：netty）
2. 序列化算法（`rpc-consumer/src/main/resources/consumer.properties` 中，`serialization.algorithm` 字段可选：jdk、json、kryo、protostuff）
3.  注册中心（`rpc-provider/src/main/resources/provider.properties` 中，`provider.registry` 字段可选：zookeeper）
4. 负载均衡算法（`rpc-consumer/src/main/resources/consumer.properties` 中，`load.balance` 字段可选：random、roundrobin）



若要对上述组件扩展其他框架或算法，均可 通过实现 他们的模块根目录下的接口来扩展，然后将 SPI 文件中添加对应的全类名，并修改配置文件对应字段即可使用。比如序列化算法，若添加新的序列化算法：

1. 新建 `rpc-serialization/src/main/java/cn/krone/rpc/serialization/Serializer.java ` 的实现类（eg：`yyy`）
2. 然后将实现类 `yyy` 的全类名添加到接口命名的文件 `rpc-serialization/src/main/resources/META-INF/extensions/cn.krone.rpc.serialization.Serializer` 中，这个文件里的内容就是这个接口的具体的实现类，格式为：`xxx=cn.krone.rpc.serialization.yyy`
3. 修改配置文件 `rpc-consumer/src/main/resources/consumer.properties` 中的 `serialization.algorithm` 字段为 `xxx`，`serialization.algorithm=xxx`



## 网络传输

### Request & Response

```java
public class RpcRequest { 
    // 接口名
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数类型
    private Class<?>[] paramTypes;
    // 参数实际值
    private Object[] args;
}
```

```java
public class RpcResponse {
    // 状态码
    private int statusCode;
    // 出错的说明信息
    private String message;
    // 执行完成得到的返回结果数据，泛型
    private Object data;
}
```



### 协议

```
+------------------------------------+---------+---------+---------+---------+------------------------------------+
|           Magic Number             | version | msg type| ser type| com type|          Data Length               |
|             4 bytes                | 1 byte  | 1 byte  | 1 byte  | 1 byte  |             4 bytes                |
+-----------------------------------------------------------------------------------------------------------------+
|                                                   Data Bytes                                                    |
|                                               Length: ${Data Length}                                            |
+-----------------------------------------------------------------------------------------------------------------+
```

消息头 6个部分 共 12 bytes

魔数 4 bytes

版本号 1 byte

消息类型 1 byte；eg：request、response、heart

序列化类型 1 byte

压缩类型 1 byte

消息体长度 4 bytes



## 注册中心

解决 客户端怎么知道服务端的地址 的问题。

### zookeeper

服务端提供把 service Object 放入Map 的时候，注册到 zookeeper，节点的层级目录举例：

```
/rpc/cn.krone.rpc.api.AddService/127.0.0.1:9000
```

服务消费端 在代理的 invoke 方法中基于上述命名格式，到 zookeeper 中找服务的通信地址 



## 计划要做的功能

- [x] 服务消费端 动态代理
- [x] 服务提供端 反射
- [x] 自定义协议
- [x] Netty 网络传输
- [x] SPI
- [x] 可配置 网络传输（暂只有 Netty）
- [x] 可配置多种序列化（至少三种）算法（JDK、JSON、Kryo克里奥、protostuff、Hessian）
- [x] 客户端 Channel 复用
- [x] 可配置注册中心（暂只有 zookeeper）
- [x] 可配置负载均衡策略（至少两种）（Random、轮询、一致性哈希）
- [ ] 可配置压缩算法（有时间暂定一种）
- [ ] +心跳机制，避免重连
- [ ] 容错
- [ ] spring 基于注解的自动服务注册

  



## 鸣谢（参考教程）

1. [JavaGuide：Github：guide-rpc-framework](https://github.com/Snailclimb/guide-rpc-framework)
3. [CSDN：何人听我楚狂声：一起写个Dubbo](https://blog.csdn.net/qq_40856284/category_10138756.html)
4. [CSDN：PANDA：手把手实现RPC框架--简易版Dubbo构造](https://blog.csdn.net/qq_38685503/category_10794078.html)
5. [Bilibili：黑马Netty](https://www.bilibili.com/video/BV1py4y1E7oA?p=107&spm_id_from=pageDriver)
6. [Bilibili：尚硅谷Netty](https://www.bilibili.com/video/BV1DJ411m7NR?p=112&spm_id_from=pageDriver)

