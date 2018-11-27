package com.dl.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class MapReduceTest {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	/**
	 * 1.eclipse里直接运行main方法，job提交给本地执行器执行(mapred-site.xml,yarn-site.xml拷贝到工程下，Configuration设置jar文件，也可以提交给hadoop集群执行)
	 * 
	 * 2.job提交给hadoop集群执行,可以通过yarn和jobhistory查看job
	 * hadoop fs -rm -r /Users/tallong/JavaProjects/test/hadoop-test/output
	 * hadoop fs -mkdir -p /Users/tallong/JavaProjects/test/hadoop-test/input
	 * hadoop fs -put /Users/tallong/JavaProjects/test/hadoop-test/input/file01 /Users/tallong/JavaProjects/test/hadoop-test/input/file01
	 * hadoop fs -put /Users/tallong/JavaProjects/test/hadoop-test/input/file02 /Users/tallong/JavaProjects/test/hadoop-test/input/file02 
	 * hadoop fs -ls /Users/tallong/JavaProjects/test/hadoop-test/input  
	 * hadoop jar hadoop-test-0.0.1-SNAPSHOT.jar com.dl.hadoop.MapReduceTest
	 * hadoop fs -cat /Users/tallong/JavaProjects/test/hadoop-test/output/part-r-00000          
	 */
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(MapReduceTest.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("/Users/tallong/JavaProjects/test/hadoop-test/input"));
		FileOutputFormat.setOutputPath(job, new Path("/Users/tallong/JavaProjects/test/hadoop-test/output"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
