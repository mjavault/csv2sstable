package com.tlotu.csv2sstable;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/22/2015
 * Time: 11:35 AM
 */
public class SchemaTest {

    public Schema createSchema() {
        Schema schema = new Schema("keyspace", "table");
        schema.add(new Column("id", ColumnType.BigInt));
        schema.add(new Column("version", ColumnType.BigInt));
        schema.add(new Column("accuracy", ColumnType.Float));
        schema.add(new Column("bearing", ColumnType.Float));
        Column c = new Column("car_id", ColumnType.BigInt);
        schema.add(c);
        schema.addPrimaryKey(c);
        schema.add(new Column("lat", ColumnType.Double));
        schema.add(new Column("lon", ColumnType.Double));
        c = new Column("recorded", ColumnType.Timestamp);
        c.setOrdering(Ordering.Desc);
        schema.add(c);
        schema.addClusteringKey(c);
        schema.add(new Column("speed", ColumnType.Double));
        schema.add(new Column("privacy_level", ColumnType.Int));
        schema.add(new Column("to_be_processed", ColumnType.Boolean));
        schema.add(new Column("gsm_signal_strength", ColumnType.Int));
        return schema;
    }

    public Schema readSchema() throws IOException {
        return Schema.parse("keyspace", "table", "schema.txt");
    }

    @Test
    public void testGetSchema() throws Exception {
        assertEquals("CREATE TABLE keyspace.table (id bigint, version bigint, accuracy float, bearing float, car_id bigint, lat double, lon double, recorded timestamp, speed double, privacy_level int, to_be_processed boolean, gsm_signal_strength int, PRIMARY KEY((car_id), recorded)) WITH CLUSTERING ORDER BY (recorded DESC);",
                createSchema().getSchema());
        assertEquals("CREATE TABLE keyspace.table (id bigint, version bigint, accuracy float, bearing float, car_id bigint, lat double, lon double, recorded timestamp, speed double, privacy_level int, to_be_processed boolean, gsm_signal_strength int, PRIMARY KEY((car_id), recorded)) WITH CLUSTERING ORDER BY (recorded DESC);",
                readSchema().getSchema());
    }

    @Test
    public void testGetInsertStatement() throws Exception {
        assertEquals("INSERT INTO keyspace.table (id, version, accuracy, bearing, car_id, lat, lon, recorded, speed, privacy_level, to_be_processed, gsm_signal_strength) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                createSchema().getInsertStatement());
        assertEquals("INSERT INTO keyspace.table (id, version, accuracy, bearing, car_id, lat, lon, recorded, speed, privacy_level, to_be_processed, gsm_signal_strength) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                readSchema().getInsertStatement());
    }
}