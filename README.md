# poc-alpakka-weblogic-jms
proof of concept: send/receive messages from weblogic jms queue from/to alpakka

## Run it

Import WLS Thing JMS client:

```bash
mvn deploy:deploy-file
    -Dfile=${ORACLE_HOME}/wlserver/server/lib/wlthint3client.jar
    -DgeneratePom=true
    -DrepositoryId=internal
    -DgroupId=custom.com.oracle
    -DartifactId=wlthint3client
    -DversionId=12.1.3.0.0
    -Dpackaging=jar
```

Create the following WebLogic JMS artifacts:

* JMS Server: wlsbJMSServer (already exist in SOA 12c)

* JMS Module: SystemModule-0

* JMS Connection Factory: ConnectionFactory-0 (jndi: jms/TestConnectionFactory)

* JMS Queue: Queue-0 (jndi: jms/queue0)

And run `AlpakkaWebLogicJms` :)