package com.clinicops.application.command;

public interface Command {
    String domain();
    String resource();
    String action();
}
