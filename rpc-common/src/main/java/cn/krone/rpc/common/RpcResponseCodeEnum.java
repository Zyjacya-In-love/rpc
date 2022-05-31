package cn.krone.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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
