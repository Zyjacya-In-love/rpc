package cn.krone.rpc.serialization;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xzq
 * @create 2022-06-09-14:59
 */
@AllArgsConstructor
@Getter
public enum SerializationAlgorithmEnum {
    JDK((byte) 0x00, "jdk"),
    JSON((byte) 0x01, "json"),
    KRYO((byte) 0x02, "kryo"),
    PROTOSTUFF((byte) 0x03, "protostuff"),
    HESSIAN((byte) 0X04, "hessian");

    private final byte code;
    private final String name;

    public static String getNameByCode(byte code) {
        for (SerializationAlgorithmEnum c : SerializationAlgorithmEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static byte getCodeByName(String name) {
        for (SerializationAlgorithmEnum c : SerializationAlgorithmEnum.values()) {
            if (c.getName().equals(name)) {
                return c.code;
            }
        }
        return -1;
    }
}
