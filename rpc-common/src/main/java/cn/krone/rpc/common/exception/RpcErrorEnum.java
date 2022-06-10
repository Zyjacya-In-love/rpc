package cn.krone.rpc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xzq
 * @create 2022-06-01-21:05
 */
@Getter
@AllArgsConstructor
public enum RpcErrorEnum {

    SERVICE_NOT_FOUND("SERVICE_NOT_FOUND : There is no such service here"),
    SERVICE_NOT_IMPLEMENT_INTERFACE("SERVICE_NOT_IMPLEMENT_INTERFACE : a service must implement at least one interface"),
    SERIALIZE_ERROR("SERIALIZE_ERROR : serialize fail"),
    DESERIALIZE_ERROR("DESERIALIZE_ERROR : deserialize fail");

    private final String message;
}
