# csv2sstable
Convert CSV file to Cassandra SSTABLE file for bulk import.
This app will convert any input CSV file into a set of sstables that you can then use with sstableloader to import into your cluster.

## How to
```Usage: csv2sstable <keyspace> <table> <schema_file> <csv_file> <output_folder>```

- `keyspace` is the destination keyspace.
- `table` is the destination table inside the keyspace.
- `schema_file` is the file used to describe the destination table schema (see below for file format).
- `csv_file` is the CSV file to import.
- `output_folder` is where the sstable will be stored.

### External libraries
To compile this project, you will need the Cassandra libraries version 2.1.6:
http://archive.apache.org/dist/cassandra/2.1.6/apache-cassandra-2.1.6-bin.tar.gz

### Schema file
The schema file is where you will define the format of the destination Cassandra table. The format is:
```
column_name:column_type:[csv_mapping_id][:pk|ck[:asc|desc]]
```
where:
- `column_type` is one of the following: `int`, `bigint`, `double`, `float`, `boolean`, `timestamp`.
- `csv_mapping_id` is the column number in the CSV file you are importing. Leave this column empty if you don't want to map that column to the CSV file.
- `pk` indicates a column that will be part of the table's Primary Key, and `ck` a column that will be part of the table's Clustering Key.
- `asc` and `desc` are used to specify the clustering order of a given  Primary Key or Clustering Key.

For example, the following Cassandra table:
```
CREATE TABLE gps (
	id                   bigint,                   
	internal             double,
	lat                  double,
	lon                  double,
	time                 timestamp,
	PRIMARY KEY((id), time)
) WITH CLUSTERING ORDER BY (time DESC);
```
becomes:
```
id:bigint:0:pk
internal:double:
lat:double:1
lon:double:2
time:timestamp:3:ck:desc
```
where `id` will be mapped to the first column of the CSV file (column 0), `internal` will have the value `null`, `lat` to the second CSV column (column 1), and so on.

### CSV file
The CSV file shouldn't have any header. The columns are numbered `0` to `n`, and that number can be used in the `schema.txt` file to map them to the destination Cassandra table.
Example:
```
851968,37.7765529,-122.39782487,2012-02-06 20:09:08.486-05
851969,37.7762448,-122.3976393,2012-02-06 20:09:10.627-05
851970,37.7762486,-122.3976377,2012-02-06 20:09:11.864-05
```
You don't have to map all the columns (you can leave columns out).
