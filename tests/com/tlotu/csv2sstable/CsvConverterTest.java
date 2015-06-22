package com.tlotu.csv2sstable;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/19/2015
 * Time: 12:49 PM
 */
public class CsvConverterTest {

    @Test
    public void testExport() throws Exception {
        Date date = (Date) Utils.convert(ColumnType.Timestamp, "2012-02-06 20:09:08.486-05");
        assertEquals("Mon Feb 06 17:09:08 PST 2012", date.toString());

        date = (Date) Utils.convert(ColumnType.Timestamp, "2012-02-07 17:06:52-05");
        assertEquals("Tue Feb 07 14:06:52 PST 2012", date.toString());
    }
}