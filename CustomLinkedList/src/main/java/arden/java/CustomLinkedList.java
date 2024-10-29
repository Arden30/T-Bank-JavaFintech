package arden.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class CustomLinkedList<T> implements CustomList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size = 0;

    public void add(T data) {
        Node<T> newNode = new Node<>(data);

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }

        tail = newNode;
        size++;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> node = head;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }

        return node.data;
    }

    public boolean remove(T data) {
        if (head == null) {
            return false;
        }

        Node<T> node = head;
        while (node != null) {
            if (node.data.equals(data)) {
                if (node == head) {
                    head = node.next;
                    if (head != null) {
                        head.prev = null;
                    } else {
                        tail = null;
                    }
                } else if (node == tail) {
                    tail = node.prev;
                    tail.next = null;
                } else {
                    node.prev.next = node.next;
                    node.next.prev = node.prev;
                }
                size--;

                return true;
            }

            node = node.next;
        }

        return false;
    }

    public boolean contains(T data) {
        if (head == null) {
            return false;
        }

        Node<T> node = head;
        while (node != null) {
            if (node.data.equals(data)) {
                return true;
            }

            node = node.next;
        }

        return false;
    }

    public void addAll(Collection<T> c) {
        for (T item : c) {
            add(item);
        }
    }

    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (Node<T> node = head; node != null; node = node.next) {
            list.add(node.data);
        }

        return list;
    }

    @Override
    public CustomIterator<T> iterator() {
        return new CustomLinkedListIterator();
    }

    public void display() {
        Node<T> node = head;
        while (node != null) {
            System.out.print(node.data + " -> ");
            node = node.next;
        }
        System.out.print("null");
    }

    public int size() {
        return size;
    }


    public static class Node<T> {
        Node<T> prev;
        Node<T> next;
        T data;

        public Node(T data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    public class CustomLinkedListIterator implements CustomIterator<T> {
        private Node<T> next = head;
        private int nextIndex = 0;

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T data = next.data;
            next = next.next;
            nextIndex++;

            return data;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            while (nextIndex < size) {
                action.accept(next());
            }
        }
    }
}
