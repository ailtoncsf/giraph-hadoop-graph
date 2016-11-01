package com.ailton.giraph;

import com.google.common.collect.Lists;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple text-based {@link org.apache.giraph.io.VertexInputFormat} for
 * unweighted graphs with int ids.
 *
 * Each line consists of: vertex neighbor1 neighbor2 ...
 */
public class TextTextTextTextInputFormat extends TextVertexInputFormat<Text, Text, Text> {
	/** Separator of the vertex and neighbors */
	private static final Pattern SEPARATOR = Pattern.compile("[,]");

	@Override
	public TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException {
		return new TextTextNullIntVertexReader();
	}

	/**
	 * Vertex reader associated with {@link TextTextNullTextInputFormat}.
	 */
	public class TextTextNullIntVertexReader extends TextVertexReaderFromEachLineProcessed<String[]> {
		/**
		 * Cached vertex id for the current line
		 */
		private Text id;

		@Override
		protected String[] preprocessLine(Text line) throws IOException {
			String[] tokens = SEPARATOR.split(line.toString().toLowerCase());
			id = new Text(tokens[0]);
			return tokens;
		}

		@Override
		protected Text getId(String[] tokens) throws IOException {
			return id;
		}

		@Override
		protected Text getValue(String[] tokens) throws IOException {
			return id;
		}

		@Override
		protected Iterable<Edge<Text, Text>> getEdges(String[] tokens) throws IOException {
			List<Edge<Text, Text>> edges = Lists.newArrayListWithCapacity(1);
			edges.add(EdgeFactory.create(new Text(tokens[2]), new Text(tokens[1])));
			return edges;
		}
	}
}
