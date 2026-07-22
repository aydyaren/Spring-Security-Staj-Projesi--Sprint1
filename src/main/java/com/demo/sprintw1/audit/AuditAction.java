package com.demo.sprintw1.audit;

public enum AuditAction {

    LOGIN,
    LOGOUT,
    REFRESH_TOKEN,

    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,

    CREATE_DOCUMENT,
    UPDATE_DOCUMENT,
    DELETE_DOCUMENT,
    DOWNLOAD_DOCUMENT
}