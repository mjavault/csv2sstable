package com.tlotu.csv2sstable;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.dht.Murmur3Partitioner;
import org.apache.cassandra.io.sstable.CQLSSTableWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/19/2015
 * Time: 12:06 PM
 */
public class CsvConverter {
    private final String input;
    private final Schema schema;

    public CsvConverter(String keyspace, String table, String schemaFile, String inputFile) throws IOException {
        this.input = inputFile;
        this.schema = Schema.parse(keyspace, table, schemaFile);
    }

    public void export(String output) throws Exception {
        long start = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(input));

        // Create output directory that has keyspace and table name in the path
        File outputDir = new File(output + File.separator + schema.getKeyspace() + File.separator + schema.getTable());
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Cannot create output directory: " + outputDir);
        }

        //prepare export
        Config.setClientMode(true);
        CQLSSTableWriter.Builder builder = CQLSSTableWriter.builder();
        builder.inDirectory(outputDir)
                .forTable(schema.toString())
                .using(schema.getInsertStatement())
                .withPartitioner(new Murmur3Partitioner());
        CQLSSTableWriter writer = builder.build();

        //read import
        long lastTick = System.currentTimeMillis();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            //parse values
            String[] items = Utils.split(line, ",");
            lineNumber++;

            //insert values
            List<Object> values = new LinkedList<>();
            for (Column c : schema.getColumns()) {
                if (c.getMapping() != null) {
                    values.add(Utils.convert(c.getType(), items[c.getMapping()]));
                } else {
                    values.add(null);
                }
            }
            writer.addRow(values);

            //show status
            if (lineNumber % 100000 == 0) {
                try {
                    System.out.print(String.format("%dK %dm/s\r", (lineNumber / 1000), 100000000 / (System.currentTimeMillis() - lastTick)));
                    lastTick = System.currentTimeMillis();
                } catch (Exception ignored) {
                }
            }
        }
        try {
            writer.close();
        } catch (IOException ignore) {
        }

        long end = System.currentTimeMillis();
        System.out.println("Successfully parsed " + lineNumber + " lines in " + (end - start) + " ms.");
    }
}
