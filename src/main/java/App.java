import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;

import java.util.Arrays;
import java.util.List;

/**
 *
 * Spark—to-Solace This app shows a Spark application writing to Solace. It
 * opens one connection to Solace per partition.
 *
 * Some helpful resources:
 *
 * Apache Spark - foreach Vs foreachPartition - When to use what?
 * https://stackoverflow.com/questions/30484701/apache-spark-foreach-vs-foreachpartition-when-to-use-what
 *
 * Writing to Solace in a Distributed Way Using Spark
 * https://harrysingh-nitj.medium.com/spark-solace-connector-writing-to-solace-in-a-distributed-way-using-spark-3e5ff73cee04
 *
 */
public class App {
  public static void main(String[] args) {
    // initialize spark session
    SparkSession spark = SparkSession.builder().appName("Spark->Solace Demo").getOrCreate();

    // create dummy dataset to mimic ingesting data from multiple data sources
    List<String> dummyValues = Arrays.asList("foo", "bar", "baz", "qux");
    Dataset<String> dataset = spark.createDataset(dummyValues, Encoders.STRING());

    // iterate over partitions
    dataset.foreachPartition(partitionOfRecords -> {

      // create a Solace message producer session for each partition
      final String hostName = "tcp://localhost:55555";
      final String vpnName = "default";
      final String username = "default";
      final String password = "default";
      final JCSMPProperties properties = new JCSMPProperties();
      properties.setProperty(JCSMPProperties.HOST, hostName); // host:port
      properties.setProperty(JCSMPProperties.VPN_NAME, vpnName); // message-vpn
      properties.setProperty(JCSMPProperties.USERNAME, username); // client-username
      properties.setProperty(JCSMPProperties.PASSWORD, password); // client-password
      final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);
      session.connect(); // session connected
      XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishCorrelatingEventHandler() {
        @Override
        public void responseReceivedEx(Object o) {
          System.out.println("Producer received response for msg: " + o);

        }

        @Override
        public void handleErrorEx(Object o, JCSMPException e, long l) {
          System.out.println("Producer received response for msg: " + o);
        }
      });

      // publish a message for each record in the partition using the partition's
      // dedicated message producer
      partitionOfRecords.forEachRemaining(val -> {
        // form dynamic Solace topic using values from Dataset row
        final Topic topic = JCSMPFactory.onlyInstance().createTopic("root/" + val); // include value in topic
        // attach payload to Solace message object
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText(val);
        // publish message to Solace broker
        try {
          prod.send(msg, topic);
        } catch (JCSMPException e) {
          e.printStackTrace();
        }
        System.out.println("Published message: " + val);
      });
    });
  }
}
