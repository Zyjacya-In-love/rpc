package cn.krone.rpc.common.exception;

/**
 * @author xzq
 * @create 2022-06-01-20:58
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorEnum error) {
        super(error.getMessage());
    }

}
