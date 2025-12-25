package com.travelmate.authservice.constants;

/**
 * Compile-time constant API paths for Spring mapping annotations.
 *
 * Note: Java annotation values must be compile-time constants; enums don't work here.
 */
public final class AuthApiPaths {
    private AuthApiPaths() {}

    public static final String AUTH_BASE = "/api/auth";

    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String VALIDATE = "/validate";
    public static final String REFRESH = "/refresh";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String RESEND_VERIFICATION = "/resend-verification";
    public static final String RESET_PASSWORD_REQUEST = "/reset-password-request";
    public static final String RESET_PASSWORD = "/reset-password";
    public static final String LOGOUT = "/logout";

    public static final String UPDATE_USER = "/update-user";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String DELETE_USER = "/delete-user/{userId}";
    public static final String USER_INFO = "/user-info";
    public static final String ALL_USERS = "/all-users";
    public static final String CHECK_EMAIL = "/check-email/{email}";

    public static final String REGISTER_SUBADMIN = "/register-subadmin";
    public static final String DELETE_REQUEST = "/delete-request";
    public static final String UPDATE_ROLE = "/update-role";
    public static final String GET_USER_NAME = "/get-user-name";
    public static final String ALL_SUBADMINS = "/all-subadmins";
    public static final String ALL_DELETE_REQUESTED_USERS = "/all-delete-requested-users";

    public static final String CHECK_USERNAME = "/check-username/{username}";
}

