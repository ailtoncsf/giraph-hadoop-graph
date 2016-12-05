package com.ailton.giraph;

import java.io.IOException;
import org.apache.giraph.aggregators.TextAppendAggregator;
import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.conf.StrConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

//public class YourWorkerClass extends BasicComputation<Text, Text, Text, Text> {
public class YourWorkerClass extends BasicComputation <LongWritable, DoubleWritable, FloatWritable, DoubleWritable>{
	private static final StrConfOption QUERY = new StrConfOption("MyQuery.target", null, "target node");

	/** The shortest paths id */
	public static final LongConfOption SOURCE_ID = new LongConfOption("YourWorker.sourceId", 1,
			"The shortest paths id");
	/** Class logger */
	private static final Logger LOG = Logger.getLogger(YourWorkerClass.class);

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
		
		System.out.println("aeeeeew");
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
	
	
	//@Override
	public void compute2(Vertex<Text, Text, Text> vertex, Iterable<Text> messages) throws IOException {
		String target = QUERY.get(getConf()).toLowerCase();
		Text id = vertex.getId();
		String textAggregatorName = TextAppendAggregator.class.getName();
		// If this the target node then append this to the message and aggregate
		// the whole
		// message as a path from source to target. # is used as separator
		// between different paths
		if (id.toString().toLowerCase().equals(target)) {
			for (Text msg : messages) {
				if (msg == null)
					continue;
				aggregate(textAggregatorName, new Text(msg.toString() + "," + target + "#"));
			}
		}
	}
}
