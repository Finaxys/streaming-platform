package com.finaxys.streaming;


import com.finaxys.utils.Converter;
import com.finaxys.utils.StreamConfiguration;
import com.finaxys.utils.StreamLayerException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class KafkaToHBase{

    private static Logger LOGGER = Logger.getLogger(KafkaToHBase.class);

    private static JavaSparkContext jsc;
    private static StreamConfiguration streamConfig = StreamConfiguration.getInstance();
    private static String kafkaTopic = streamConfig.getKafkaTopic() ;
    private static String kafkaQuorum = streamConfig.getKafkaQuorum() ;
    private static String hbaseConf = streamConfig.getHbaseConfHbase();
    private static String tableName = streamConfig.getTableName();
    private static byte[] columnFamily = streamConfig.getColumnFamily();

    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf().setAppName("kafkaStreaming");
        try{
            jsc = new JavaSparkContext(sparkConf);
            LOGGER.info("spark started with distributed mode");
        }
        catch(Exception e){
            LOGGER.info("spark can't start with distributed mode");
            sparkConf = new SparkConf().setMaster("local[*]").setAppName("kafkaStreaming");
            jsc = new JavaSparkContext(sparkConf);
        }
        JavaStreamingContext jssc = new JavaStreamingContext(jsc,
                Durations.seconds(2));
        Map<String, Integer> topics  = new HashMap<String, Integer>();
        topics.put(kafkaTopic,new Integer(1));
        JavaPairReceiverInputDStream<String, String> kafkaStream = KafkaUtils.createStream(jssc, kafkaQuorum, "groupid", topics);



        kafkaStream.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {

            private static final long serialVersionUID = -6487126638165154032L;

            @Override
            public void call(JavaPairRDD<String, String> rdd) throws Exception {
                // create connection with HBase
                final Configuration config = new Configuration();
                try {
                    config.addResource(new Path(hbaseConf));
                    config.reloadConfiguration();
                    HBaseAdmin.checkHBaseAvailable(config);
                    LOGGER.info("HBase is running!");
                }
                catch (Exception e) {
                    LOGGER.error("HBase is not running!" + e.getMessage());
                    throw new StreamLayerException(e.getMessage());
                }
                config.set(TableInputFormat.INPUT_TABLE, tableName);

                final Job newAPIJobConfiguration1 ;
                try {
                    newAPIJobConfiguration1 = Job.getInstance(config);
                } catch (IOException e) {
                    LOGGER.error("can't create mapRed conf");
                    throw new StreamLayerException(e.getMessage());
                }
                newAPIJobConfiguration1.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, tableName);
                newAPIJobConfiguration1.setOutputFormatClass(org.apache.hadoop.hbase.mapreduce.TableOutputFormat.class);



                JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = rdd.mapToPair(new PairFunction<Tuple2<String, String>, ImmutableBytesWritable, Put>() {
                    @Override
                    public Tuple2<ImmutableBytesWritable, Put> call(Tuple2<String, String> stringStringTuple2) throws Exception {
                        Converter converter = new Converter(columnFamily);
                        Put put = converter.convertStringToPut(stringStringTuple2._1(),stringStringTuple2._2());
                        //LOGGER.info("data inserted : " + stringStringTuple2._1());
                        return new Tuple2<ImmutableBytesWritable, Put>(new ImmutableBytesWritable(), put);
                    }
                });

                hbasePuts.saveAsNewAPIHadoopDataset(newAPIJobConfiguration1.getConfiguration());
            }
        });



        /**
         * Older version before upgrading spark from 1.5.2 to 2.0.0
         */

        /*
        kafkaStream.foreachRDD(new Function<JavaPairRDD<String, String>, Void>() {

            private static final long serialVersionUID = -6487126638165154032L;

            @Override
            public Void call(JavaPairRDD<String, String> rdd) throws Exception {

                // create connection with HBase
                final Configuration config = new Configuration();
                try {
                    config.addResource(new Path(hbaseConf));
                    config.reloadConfiguration();
                    HBaseAdmin.checkHBaseAvailable(config);
                    LOGGER.info("HBase is running!");
                }
                catch (Exception e) {
                    LOGGER.error("HBase is not running!" + e.getMessage());
                    throw new StreamLayerException(e.getMessage());
                }
                config.set(TableInputFormat.INPUT_TABLE, tableName);

                final Job newAPIJobConfiguration1 ;
                try {
                    newAPIJobConfiguration1 = Job.getInstance(config);
                } catch (IOException e) {
                    LOGGER.error("can't create mapRed conf");
                    throw new StreamLayerException(e.getMessage());
                }
                newAPIJobConfiguration1.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, tableName);
                newAPIJobConfiguration1.setOutputFormatClass(org.apache.hadoop.hbase.mapreduce.TableOutputFormat.class);



                JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = rdd.mapToPair(new PairFunction<Tuple2<String, String>, ImmutableBytesWritable, Put>() {
                    @Override
                    public Tuple2<ImmutableBytesWritable, Put> call(Tuple2<String, String> stringStringTuple2) throws Exception {
                        Converter converter = new Converter(columnFamily);
                        Put put = converter.convertStringToPut(stringStringTuple2._1(),stringStringTuple2._2());
                        //LOGGER.info("data inserted : " + stringStringTuple2._1());
                        return new Tuple2<ImmutableBytesWritable, Put>(new ImmutableBytesWritable(), put);
                    }
                });

                hbasePuts.saveAsNewAPIHadoopDataset(newAPIJobConfiguration1.getConfiguration());
                return null;
            }
        });
        */

        jssc.start();
        jssc.awaitTermination(); // throws interrupted exception
    }
}
