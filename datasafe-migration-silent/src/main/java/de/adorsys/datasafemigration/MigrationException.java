package de.adorsys.datasafemigration;

public class MigrationException extends RuntimeException {
    public MigrationException(String message) {
        super(message);
    }
}
