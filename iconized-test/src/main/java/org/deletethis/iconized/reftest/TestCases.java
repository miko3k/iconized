package org.deletethis.iconized.reftest;

import java.util.*;

public class TestCases<T extends IcoTestCase> implements Iterable<T> {
    private List<T> list;

    public TestCases(List<T> list) {
        this.list = list;
    }

    public TestCases() {
        this.list = new ArrayList<>();
    }

    public void add(T t) {
        list.add(t);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public Iterable<T> byName(String name) {
        // always return one element as list
        for(T t: list) {
            if(t.getName().equals(name)) {
                return Collections.singletonList(t);
            }
        }
        throw new IllegalArgumentException("test case not found: " + name);
    }
}
