package com.simplefanc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static byte getCode(String name) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getName().equals(name)) {
                return c.code;
            }
        }
        return 0x00;
    }
}
