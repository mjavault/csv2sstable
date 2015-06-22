package com.tlotu.csv2sstable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/22/2015
 * Time: 10:47 AM
 */
public class Schema {

    private final String keyspace;
    private final String table;
    private final List<Column> columns = new LinkedList<>();
    private final List<Column> primaryKeys = new LinkedList<>();
    private final List<Column> clusteringKeys = new LinkedList<>();

    public static Schema parse(String keyspace, String table, String filename) throws IOException {
        Schema schema = new Schema(keyspace, table);
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] items = line.split(":", -1);
            if (items.length >= 3) {
                //normal column
                Integer mapping = items[2].length() > 0 ? Integer.parseInt(items[2]) : null;
                Column c = new Column(items[0], Utils.valueOfIgnoreCase(ColumnType.class, items[1]), mapping);
                schema.add(c);
                if (items.length >= 4) {
                    //primary key or clustering key
                    if ("pk".equals(items[3])) {
                        schema.addPrimaryKey(c);
                    } else if ("ck".equals(items[3])) {
                        schema.addClusteringKey(c);
                    }
                    if (items.length >= 5) {
                        //ordering
                        if ("asc".equals(items[4])) {
                            c.setOrdering(Ordering.Asc);
                        } else if ("desc".equals(items[4])) {
                            c.setOrdering(Ordering.Desc);
                        }
                    }
                }
            }
        }
        return schema;
    }

    public Schema(String keyspace, String table) {
        this.keyspace = keyspace;
        this.table = table;
    }

    public void add(Column c) {
        columns.add(c);
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        String str = "CREATE TABLE " + keyspace + "." + table + " (";
        //add columns
        for (Column c : columns) {
            str += c.getName() + " " + c.getType().toString().toLowerCase() + ", ";
        }
        //add primary key definition
        if (clusteringKeys.size() > 0) {
            str += "PRIMARY KEY((" + Utils.join(primaryKeys) + "), " + Utils.join(clusteringKeys) + ")";
        } else {
            str += "PRIMARY KEY(" + Utils.join(primaryKeys) + ")";
        }
        //close table
        str += ")";
        //add clustering order
        List<String> ordering = new LinkedList<>();
        for (Column c : primaryKeys) {
            if (c.getOrdering() != Ordering.None) {
                ordering.add(c.getName() + " " + c.getOrdering().toString());
            }
        }
        for (Column c : clusteringKeys) {
            if (c.getOrdering() != Ordering.None) {
                ordering.add(c.getName() + " " + c.getOrdering().toString());
            }
        }
        if (ordering.size() > 0) {
            str += " WITH CLUSTERING ORDER BY (" + Utils.join(ordering) + ")";
        }
        str += ";";
        return str;
    }

    public String getSchema() {
        return toString();
    }

    public String getInsertStatement() {
        String str = "INSERT INTO " + keyspace + "." + table + " (";
        str += Utils.join(columns);
        str += ") VALUES (";
        str += Utils.join(Arrays.asList(Utils.fill(new String[columns.size()], "?")));
        str += ");";
        return str;
    }

    public void addPrimaryKey(Column column) {
        primaryKeys.add(column);
    }

    public void addClusteringKey(Column column) {
        clusteringKeys.add(column);
    }

    public String getKeyspace() {
        return keyspace;
    }

    public String getTable() {
        return table;
    }
}
