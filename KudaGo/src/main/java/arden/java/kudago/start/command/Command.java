package arden.java.kudago.start.command;

import java.util.List;

public interface Command<T> {
    void execute(List<T> data);
}
