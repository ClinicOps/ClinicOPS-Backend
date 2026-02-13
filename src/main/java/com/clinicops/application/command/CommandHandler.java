package com.clinicops.application.command;

public interface CommandHandler<C extends Command> {
    void handle(C command);
}
