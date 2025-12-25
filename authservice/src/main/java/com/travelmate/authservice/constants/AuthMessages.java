package com.travelmate.authservice.constants;

public enum AuthMessages {
    UNAUTHORIZED_NO_TOKEN("Unauthorized access: No token provided"),
    INVALID_AUTH_HEADER("Invalid authorization header"),
    TOKEN_VALIDATION_FAILED("Token validation failed"),

    USER_UPDATED_SUCCESS("User updated successfully"),
    PASSWORD_CHANGED_SUCCESS("Password changed successfully"),
    USER_DELETED_SUCCESS("User deleted successfully"),
    USER_INFO_FETCHED_SUCCESS("User info fetched successfully"),
    ALL_USERS_FETCHED_SUCCESS("All users fetched successfully"),

    EMAIL_CHECK_COMPLETE("Email check complete"),

    USERNAME_CHECK_COMPLETE("Username check complete"),
    USERNAME_ALREADY_TAKEN("Username already taken");

    private final String value;

    AuthMessages(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
