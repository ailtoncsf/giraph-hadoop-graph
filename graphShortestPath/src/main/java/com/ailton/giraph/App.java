package com.ailton.giraph;


import org.apache.giraph.conf.GiraphConfiguration;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.GiraphFileInputFormat;
import org.apache.giraph.io.formats.JsonLongDoubleFloatDoubleVertexOutputFormat;
import org.apache.giraph.job.GiraphJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import teste.JsonPairDoubleFloatDoubleVertexInputFormat;

/**
 * Hello world!
 *
 */
public class App implements Tool {

    private static final Logger LOG = Logger.getLogger(App.class); 
    private Configuration conf; 
 
    @Override 
    public void setConf(Configuration conf) { 
        this.conf = conf; 
    } 
 
    @Override 
    public Configuration getConf() { 
        return conf; 
    } 
 
    @Override 
    public int run(String[] args) throws Exception { 
        if (args.length != 3) { 
            throw new IllegalArgumentException( 
                "Syntax error: Must have 3 arguments <numbersOfWorkers> <inputLocaiton> <outputLocation>"); 
        } 
 
        int numberOfWorkers = Integer.parseInt(args[0]); 
        String inputLocation = args[1]; 
        String outputLocation = args[2]; 
        System.err.println(System.getProperty("java.library.path"));        
                   
        GiraphConfiguration config = new GiraphConfiguration();			
		config.set("fs.default.name","hdfs://hdnode01:9000");		
		config.set("mapred.job.tracker","hdnode01:9001");
		config.set("mapreduce.framework.name","yarn");	

		config.set("fs.hdfs.impl", 
	            org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
				
	        );
		config.set("fs.file.impl",
	            org.apache.hadoop.fs.LocalFileSystem.class.getName()
	        );
		
		/*config.set("yarn.application.classpath ",
				   "$HADOOP_CONF_DIR, "
				   + "$HADOOP_COMMON_HOME/share/hadoop/common/*, "
				   + "$HADOOP_COMMON_HOME/share/hadoop/common/lib/*, "
				   + "$HADOOP_HDFS_HOME/share/hadoop/hdfs/*,    "
				   + "$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*, "
				   + "$YARN_HOME/share/hadoop/mapreduce/*, "
				   + "$YARN_HOME/share/hadoop/mapreduce/lib/*, "
				   + "$YARN_HOME/share/hadoop/yarn/*,  "
				   + "$YARN_HOME/share/hadoop/yarn/lib/*");		*/
		GiraphJob job = new GiraphJob(config, getClass().getName());
		        
                                   
        
		//original
		//GiraphJob job = new GiraphJob(getConf(), getClass().getName());
	   
	    job.getInternalJob().setJarByClass(getClass());
	    
		
		job.getConfiguration().setMasterComputeClass(YourMasterCompute.class);
		job.getConfiguration().setVertexClass(Vertex.class);
		job.getConfiguration().setVertexInputFormatClass(JsonPairDoubleFloatDoubleVertexInputFormat.class);
		job.getConfiguration().setVertexOutputFormatClass(JsonLongDoubleFloatDoubleVertexOutputFormat.class);
				
		GiraphFileInputFormat.addVertexInputPath(job.getConfiguration(), new Path(inputLocation)); 
		FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(outputLocation));

		job.getConfiguration().setBoolean("giraph.SplitMasterWorker", false);
		job.getConfiguration().setWorkerConfiguration(numberOfWorkers, numberOfWorkers, 100.0f);
        
        /*
        GiraphJob job = new GiraphJob(getConf(), getClass().getName()); 
        GiraphConfiguration gconf = job.getConfiguration(); 
        gconf.setWorkerConfiguration(numberOfWorkers, numberOfWorkers, 100.0f); 
 
        GiraphFileInputFormat.addVertexInputPath(gconf, new Path(inputLocation)); 
        FileOutputFormat.setOutputPath(job.getInternalJob(), new Path(outputLocation)); 
 
        gconf.setComputationClass(ZombieComputation.class); 
        gconf.setMasterComputeClass(ZombieMasterCompute.class); 
        gconf.setVertexInputFormatClass(ZombieTextVertexInputFormat.class); 
        gconf.setVertexOutputFormatClass(ZombieTextVertexOutputFormat.class); 
        gconf.setWorkerContextClass(ZombieWorkerContext.class); 
        */
 
        boolean verbose = true; 
        if (job.run(verbose)) { 
            return 0; 
        } else { 
            return -1; 
        } 
    } 
 
    public static void main(String[] args) throws Exception { 
        int ret = ToolRunner.run(new App(), args); 
        if (ret == 0) { 
            System.out.println("Ended Good"); 
        } else { 
            System.out.println("Ended with Failure"); 
        } 
        System.exit(ret); 
    } 
}	
	
