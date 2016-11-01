package com.ailton.giraph;

import org.apache.giraph.aggregators.TextAppendAggregator;
import org.apache.giraph.conf.StrConfOption;
import org.apache.giraph.master.DefaultMasterCompute;

/*
 * http://www.kaushikbaruah.com/posts/get-started-with-giraph/
 * */

public class YourMasterCompute extends DefaultMasterCompute {
	private static final StrConfOption QUERY = new StrConfOption("MasterQuery.R", null, "total steps");

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