package com.tlotu.csv2sstable;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/22/2015
 * Time: 2:09 PM
 */
public enum Ordering {
    None,
    Asc,
    Desc;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
