package arden.java;

import java.util.Collection;
import java.util.List;

public interface CustomList<T> {
    void add(T data);

    T get(int index);

    int size();

    boolean remove(T data);

    boolean contains(T data);

    void addAll(Collection<T> c);

    void display();

    List<T> toList();

    CustomIterator<T> iterator();
}
