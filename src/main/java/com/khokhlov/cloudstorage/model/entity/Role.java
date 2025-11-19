package com.khokhlov.cloudstorage.model.entity;

public enum Role {
    USER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
