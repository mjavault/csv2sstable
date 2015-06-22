package com.tlotu.csv2sstable;

public class Main {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: csv2sstable <keyspace> <table> <schema_file> <csv_file> <output_folder>");
            System.exit(1);
        }

        String keyspace = args[0];
        String table = args[1];
        String schema = args[2];
        String input = args[3];
        String output = args[4];

        try {
            CsvConverter csv = new CsvConverter(keyspace, table, schema, input);
            csv.export(output);
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
}