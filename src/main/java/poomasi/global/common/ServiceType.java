package poomasi.global.common;

import poomasi.global.error.ApplicationException;

import static poomasi.global.error.ApplicationError.ENUM_TYPE_ERROR;

public enum ServiceType {
    PRODUCT, FARM;

    public static ServiceType of(String type) {
        if (type.equals("product")) {
            return PRODUCT;
        } else if (type.equals("farm")) {
            return FARM;
        } else {
            throw new ApplicationException(ENUM_TYPE_ERROR);
        }
    }
}
