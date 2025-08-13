package com.example.bankcards.constants;

public class LogConstants {
    private LogConstants() {}

    public static final String NOT_FOUND = "NotFound: {}";
    public static final String UNEXPECTED_ERROR = "Unexpected error: {}";
    public static final String BAD_REQUEST = "Bad request: {}";
    public static final String VALIDATION_FAILED = "Validation failed: {}";
    public static final String CONSTRAINT_VIOLATION = "Constraint violation: {}";

    public static final String USER_GET_START = "Get user by id: {}";
    public static final String USER_GET_ERROR = "Failed to get user: {}";

    public static final String USER_UPDATE_START = "Updating user id={}";
    public static final String USER_UPDATE_SUCCESS = "User updated: id={}";

    public static final String USER_DELETE_START = "Deleting user id={}";
    public static final String USER_DELETE_SUCCESS = "User deleted: id={}";
    public static final String USER_DELETE_ERROR = "Failed to delete user id={}";

    public static final String AUTH_LOGIN_ATTEMPT = "Login attempt: username={}";
    public static final String AUTH_LOGIN_SUCCESS = "Login success: username={}";
    public static final String AUTH_REGISTER_START = "Register user: username={}";
    public static final String AUTH_REGISTER_SUCCESS = "User registered: username={}";
    public static final String AUTH_REGISTER_FAILURE = "User register failed: username={} reason={}";

    public static final String CARD_CREATE_START = "Creating card for userId={} initialBalance={} expiration={}";
    public static final String CARD_CREATE_SUCCESS = "Card created: id={} owner={} last4={}";

    public static final String CARD_GET_START = "Get card by id: {}";
    public static final String CARD_GET_SUCCESS = "Card found: id={} owner={} last4={}";
    public static final String CARD_GET_ERROR = "Failed to get card id={}";

    public static final String CARD_BLOCK_START = "Blocking card id={} requestedBy={}";
    public static final String CARD_BLOCK_SUCCESS = "Card blocked: id={} by={}";
    public static final String CARD_BLOCK_ERROR = "Failed to block card id={} reason={}";

    public static final String CARD_ALREADY_EXISTS = "Card already exists: {}";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation: {}";

    public static final String CARD_ACTIVATE_START = "Activating card id={} requestedBy={}";
    public static final String CARD_ACTIVATE_SUCCESS = "Card activated: id={} by={}";
    public static final String CARD_ACTIVATE_ERROR = "Failed to activate card id={} reason={}";

    public static final String CARD_DELETE_START = "Deleting card id={} requestedBy={}";
    public static final String CARD_DELETE_SUCCESS = "Card deleted: id={}";
    public static final String CARD_DELETE_ERROR = "Failed to delete card id={} reason={}";

    public static final String TRANSFER_START = "Initiating transfer: senderId={} receiverId={} amount={}";
    public static final String TRANSFER_SUCCESS = "Transfer completed: txId={} senderId={} receiverId={} amount={}";
    public static final String TRANSFER_ERROR = "Transfer failed: senderId={} receiverId={} amount={} reason={}";

    public static final String TRANSACTION_LIST_REQUEST = "List transactions for userId={} pageable={}";

    public static final String ACCESS_DENIED = "Access denied for user: {}";

    public static final String AMOUNT_ERROR = "Amount error: {}";
}
