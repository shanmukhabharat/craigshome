package org.jtb.jrentrent;

public enum Type {
    RENTAL_ROOMS(1),
    RENTAL_APARTMENTS_HOUSES(2),
    FORSALE_OWNER(4),
    FORSALE_BROKER(5);

    private int code;

    private Type(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
