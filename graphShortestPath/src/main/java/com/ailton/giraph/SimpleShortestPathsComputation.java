/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ailton.giraph;

import java.io.IOException;

import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.VertexOutputFormat;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

/**
 * Demonstrates the basic Pregel shortest paths implementation.
 */
// @Algorithm(name = "Shortest paths", description = "Finds all shortest paths
// from a selected vertex")
public class SimpleShortestPathsComputation
		extends BasicComputation<LongWritable, DoubleWritable, FloatWritable, DoubleWritable> implements Tool{
	/** The shortest paths id */
	public static final LongConfOption SOURCE_ID = new LongConfOption("SimpleShortestPathsVertex.sourceId", 1,
			"The shortest paths id");
	/** Class logger */
	private static final Logger LOG = Logger.getLogger(SimpleShortestPathsComputation.class);

	/**
	 * Is this vertex the source id?
	 *
	 * @param vertex
	 *            Vertex
	 * @return True if the source id
	 */
	private boolean isSource(Vertex<LongWritable, ?, ?> vertex) {
		return vertex.getId().get() == SOURCE_ID.get(getConf());
	}

	@Override
	public void compute(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex, Iterable<DoubleWritable> messages)
			throws IOException {
		if (getSuperstep() == 0) {
			vertex.setValue(new DoubleWritable(Double.MAX_VALUE));
		}
		double minDist = isSource(vertex) ? 0d : Double.MAX_VALUE;
		for (DoubleWritable message : messages) {
			minDist = Math.min(minDist, message.get());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(
					"Vertex " + vertex.getId() + " got minDist = " + minDist + " vertex value = " + vertex.getValue());
		}
		if (minDist < vertex.getValue().get()) {
			vertex.setValue(new DoubleWritable(minDist));
			for (Edge<LongWritable, FloatWritable> edge : vertex.getEdges()) {
				double distance = minDist + edge.getValue().get();
				if (LOG.isDebugEnabled()) {
					LOG.debug("Vertex " + vertex.getId() + " sent to " + edge.getTargetVertexId() + " = " + distance);
				}
				sendMessage(edge.getTargetVertexId(), new DoubleWritable(distance));
			}
		}
		vertex.voteToHalt();
	}

	public int run(String[] argArray) throws Exception {
		/*
		 * if (argArray.length != 4) { throw new IllegalArgumentException(
		 * "run: Must have 4 arguments <input path> <output path> " +
		 * "<source vertex id> <# of workers>"); }
		 */

		GiraphJob job = new GiraphJob(getConf(), getClass().getName());
		
		job.getConfiguration().setVertexClass(Vertex.class);
		job.getConfiguration().setVertexInputFormatClass(TextTextTextTextInputFormat.class);
		job.getConfiguration().setVertexOutputFormatClass(VertexOutputFormat.class);

		FileInputFormat.addInputPath(job.getInternalJob(), new Path(argArray[0]));
		FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(argArray[1]));
		
		job.getConfiguration().setLong("9999.9", 1);
		job.getConfiguration().setBoolean("giraph.SplitMasterWorker", false);
		job.getConfiguration().setWorkerConfiguration(1, 1, 100.0f);		
					
		if (job.run(true) == true) {
			return 0;
		} else {
			return -1;
		}
		
		
	}

	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new SimpleShortestPathsComputation(), args));
	}	
	/*
	public static class RunnerJob  extends GiraphRunner{
		
	}*/

	@Override
	public void setConf(Configuration arg0) {
		// TODO Auto-generated method stub
		
	}
}
