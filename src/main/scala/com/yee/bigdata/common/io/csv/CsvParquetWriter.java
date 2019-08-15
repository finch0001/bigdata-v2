package com.yee.bigdata.common.io.csv;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.Path;

import parquet.hadoop.ParquetWriter;
import parquet.hadoop.api.WriteSupport;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.schema.MessageType;

public class CsvParquetWriter extends ParquetWriter<List<String>> {

    public CsvParquetWriter(Path file, MessageType schema,CompressionCodecName codecName) throws IOException {
        this(file, schema,codecName, false);
    }


    public CsvParquetWriter(Path file, MessageType schema, CompressionCodecName codecName, boolean enableDictionary) throws IOException {
        super(file, (WriteSupport<List<String>>) new CsvWriteSupport(schema), codecName, DEFAULT_BLOCK_SIZE, DEFAULT_PAGE_SIZE, enableDictionary, false);
    }
}
