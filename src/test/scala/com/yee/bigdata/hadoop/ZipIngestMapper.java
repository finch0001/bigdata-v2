package com.yee.bigdata.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class ZipIngestMapper{

    public static class ZipMapper extends Mapper<Text,BytesWritable,Text,Text>{

        public void map(Text key, BytesWritable value, Context context) throws IOException, InterruptedException {
            String fileName = key.toString();
            String fileContent = value.toString();

            Text outFileName = new Text(fileName);
            Text outFileOontext = new Text(fileContent);

            context.write(outFileName,outFileOontext);
        }

    }

    public static class ZipReducer extends Reducer<Text,Text,Text,Text>{

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            String resKey = key.toString();
            for (Text val : values) {
                String value = Bytes.toString(val.getBytes());
                context.write(key,new Text(value));
            }
        }
    }

    public static void main(String[] args) throws IOException,ClassNotFoundException,InterruptedException{
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, "zip ingest job");
        job.setJarByClass(ZipIngestMapper.class);
        job.setMapperClass(ZipMapper.class);
        //job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(ZipReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }

        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
