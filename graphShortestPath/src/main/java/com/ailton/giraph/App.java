package com.ailton.giraph;

import org.apache.giraph.GiraphRunner;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.VertexOutputFormat;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

/**
 * Hello world!
 *
 */
public class App extends GiraphRunner {

	@Override
	public int run(String[] argArray) throws Exception {

		if (argArray.length != 4) {
			throw new IllegalArgumentException(
					"run: Must have 4 arguments <input path> <output path> " + "<source vertex id> <# of workers>");
		}

		//Job job = new Job(getConf(), getClass().getName());
		GiraphJob job = new GiraphJob(getConf(), getClass().getName());
	
		job.getConfiguration().setVertexClass(Vertex.class);
		job.getConfiguration().setVertexInputFormatClass(TextTextTextTextInputFormat.class);
		job.getConfiguration().setVertexOutputFormatClass(VertexOutputFormat.class);

		FileInputFormat.addInputPath(job.getInternalJob(), new Path(argArray[0]));
		FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(argArray[1]));
		
		job.getConfiguration().setLong("321.46548748", Long.parseLong(argArray[2]));

		//job.getConfiguration().setWorkerConfiguration(Integer.parseInt(argArray[3]), Integer.parseInt(argArray[3]), 100.0f);

		if (job.run(true) == true) {
			return 0;
		} else {
			return -1;
		}

	}

	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new App(), args));
	}

}
