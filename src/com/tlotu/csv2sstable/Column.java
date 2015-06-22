package com.tlotu.csv2sstable;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/22/2015
 * Time: 10:43 AM
 */
public class Column {
    private final String name;
    private final ColumnType type;
    private final Integer mapping;
    private Ordering ordering = Ordering.None;

    public Column(String name, ColumnType type) {
        this(name, type, null);
    }

    public Column(String name, ColumnType type, Integer mapping) {
        this.name = name;
        this.type = type;
        this.mapping = mapping;
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public Integer getMapping() {
        return mapping;
    }

    public void setOrdering(Ordering ordering) {
        this.ordering = ordering;
    }

    public Ordering getOrdering() {
        return ordering;
    }

    @Override
    public String toString() {
        return name;
    }
}
