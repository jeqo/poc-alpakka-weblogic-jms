package io.github.jeqo.poc;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.jms.JmsSinkSettings;
import akka.stream.alpakka.jms.javadsl.JmsSink;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import weblogic.jms.client.JMSXAConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 *
 */
public class AlpakkaWebLogicJms {
    public static void main(String[] args) {
        //from https://redstack.wordpress.com/2009/12/21/a-simple-jms-client-for-weblogic-11g/
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        // NOTE: The port number of the server is provided in the next line,
        //       followed by the userid and password on the next two lines.
        properties.put(Context.PROVIDER_URL, "t3://localhost:7001");
        properties.put(Context.SECURITY_PRINCIPAL, "weblogic");
        properties.put(Context.SECURITY_CREDENTIALS, "welcome1");

        InitialContext ctx = null;
        try {
            ctx = new InitialContext(properties);
        } catch (NamingException ne) {
            ne.printStackTrace(System.err);
            System.exit(0);
        }
        System.out.println("Got InitialContext " + ctx.toString());
        // create QueueConnectionFactory
        ConnectionFactory connectionFactory = null;
        try {
            connectionFactory = (JMSXAConnectionFactory) ctx.lookup("jms/TestConnectionFactory");
        } catch (NamingException ne) {
            ne.printStackTrace(System.err);
            System.exit(0);
        }

        final ActorSystem system = ActorSystem.create("reactive-tweets");
        final Materializer mat = ActorMaterializer.create(system);

        //from http://developer.lightbend.com/docs/alpakka/current/jms.html
        Sink<String, NotUsed> jmsSink = JmsSink.create(
                JmsSinkSettings
                        .create(connectionFactory)
                        //<serverName>/<jmsDestination.toString=moduleName!queueName>
                        .withQueue("wlsbJMSServer/SystemModule-0!Queue-0"));

        List<String> in = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
        Source.from(in)
                .runWith(jmsSink, mat);
    }
}
