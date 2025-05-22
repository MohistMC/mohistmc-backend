package com.mohistmc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BStatsTypeEnum {
    SERVERS("servers"),
    PLAYERS("players");

    private final String type;
}
