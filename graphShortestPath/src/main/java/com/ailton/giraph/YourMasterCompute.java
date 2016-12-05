package com.ailton.giraph;

import java.io.IOException;

import org.apache.giraph.aggregators.TextAppendAggregator;
import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.conf.StrConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.master.DefaultMasterCompute;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.log4j.Logger;

/*
 * http://www.kaushikbaruah.com/posts/get-started-with-giraph/
 * */

public class YourMasterCompute extends DefaultMasterCompute {

	private static final StrConfOption QUERY = new StrConfOption("MasterQuery.R", null, "total steps");
	/** The shortest paths id */
	public static final LongConfOption SOURCE_ID = new LongConfOption("SimpleShortestPathsVertex.sourceId", 1,
			"The shortest paths id");

	/** Class logger */
	private static final Logger LOG = Logger.getLogger(YourMasterCompute.class);
	
	public void initialize() throws InstantiationException, IllegalAccessException {
		registerAggregator(TextAppendAggregator.class.getName(), TextAppendAggregator.class);
	}

	public void compute() {
		// Get the paths collected from previous super step and if you want, you
		// can write them
		// into a file.
		String str = getAggregatedValue(TextAppendAggregator.class.getName()).toString();
		int R = Integer.parseInt(QUERY.get(getConf()).toLowerCase());

		if (R == getSuperstep())
			haltComputation();
	}

}