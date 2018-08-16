package com.aws.bq.common.model.enumeration;

import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/16/2018
 */
public enum CapitalEnum {
    NULL("0", "N/A"),
    CAPITAL_ONE("1", "资方1"),
    CAPITAL_TWO("2", "资方2"),
    CAPITAL_THREE("3", "资方3")
    ;

    @Getter
    private String identifier;
    @Getter
    private String capitalName;

    CapitalEnum(String identifier, String capitalName) {
        this.identifier = identifier;
        this.capitalName = capitalName;
    }

    public static CapitalEnum from(String identifier) {
        if (!StringUtils.isEmpty(identifier)) {
            for (CapitalEnum capital : CapitalEnum.values()) {
                if (identifier.equals(capital.getIdentifier())) {
                    return capital;
                }
            }
        }
        return NULL;
    }
}
