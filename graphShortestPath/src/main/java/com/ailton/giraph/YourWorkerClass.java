package com.ailton.giraph;

import java.io.IOException;
import org.apache.giraph.aggregators.TextAppendAggregator;
import org.apache.giraph.conf.StrConfOption;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.Text;

public class YourWorkerClass extends BasicComputation<Text, Text, Text, Text> {
	private static final StrConfOption QUERY = new StrConfOption("MyQuery.target", null, "target node");

	@Override
	public void compute(Vertex<Text, Text, Text> vertex, Iterable<Text> messages) throws IOException {
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
