package com.github.mayconr.juoserver.game.core.model;

import java.util.EnumSet;

public enum CharacterStatus {
    NORMAL(0x00),
    UNKNOWN_1(0x01),
    CAN_ALTER_PAPERDOLL(0x02),
    POISONED(0x04),
    GOLDEN_HEALTH(0x08),
    UNKNOWN_2(0x10),
    UNKNOWN_3(0x20),
    WAR_MODE(0x40);

    private final int code;

    CharacterStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EnumSet<CharacterStatus> fromBitmask(int bitmask) {
        EnumSet<CharacterStatus> result = EnumSet.noneOf(CharacterStatus.class);
        for (CharacterStatus flag : values()) {
            if ((bitmask & flag.code) != 0) {
                result.add(flag);
            }
        }
        return result;
    }
}
