package com.example.bankcards.constants;

public class ExceptionConstants {
    private ExceptionConstants() {}

    public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера.";
    public static final String VALIDATION_ERROR_PREFIX = "Validation error:";

    public static final String USER_NOT_FOUND_BY_ID = "Пользователь с id = %s не найден.";
    public static final String USER_NOT_FOUND_BY_USERNAME = "Пользователь с именем '%s' не найден.";
    public static final String USERNAME_ALREADY_TAKEN = "Имя пользователя '%s' уже занято.";

    public static final String CARD_NOT_FOUND_BY_ID = "Карта с id = %s не найдена.";
    public static final String CARD_NOT_ACTIVE = "Карта с id = %s не активна.";


    public static final String TRANSACTION_NOT_ALLOWED = "Операция перевода запрещена для данного пользователя.";
    public static final String AMOUNT_ERROR = "Недостаточно средств на карте id = %s.";

    public static final String UNAUTHORIZED_OPERATION = "Вы не имеете прав для выполнения данной операции.";

    public static final String INVALID_STATE = "Недопустимое состояние: %s";
}
