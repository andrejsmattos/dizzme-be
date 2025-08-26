package com.dizzme.entity;

public enum ClientRole {
    CLIENT,
    ADMIN;

    @Override
    public String toString() {
        return this.name();
    }
}