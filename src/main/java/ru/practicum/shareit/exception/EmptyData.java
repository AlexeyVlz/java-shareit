package ru.practicum.shareit.exception;

public class EmptyData extends RuntimeException {
    public EmptyData(String message) {
        super(message);
    }
}
