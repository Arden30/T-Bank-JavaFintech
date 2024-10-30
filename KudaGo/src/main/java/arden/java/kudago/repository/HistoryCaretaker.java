package arden.java.kudago.repository;

import java.util.ArrayList;
import java.util.List;

public class HistoryCaretaker<T> {
    private final List<T> history = new ArrayList<>();

    public void save(T memento) {
        history.add(memento);
    }

    public T undo(int id) {
        if (id > 0 && id <= history.size()) {
            return history.get(id - 1);
        }

        throw new IndexOutOfBoundsException("No such snapshot");
    }

    public List<T> getAllMementos() {
        return new ArrayList<>(history);
    }
}