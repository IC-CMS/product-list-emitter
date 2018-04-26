package cms.sre.product_list_emitter;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@org.springframework.boot.test.context.TestConfiguration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class TestConfiguration extends App{
    private MongodExecutable mongodExecutable;

    public TestConfiguration(){ super.mongoDatabaseName = "test";}

    @Override
    public MongoClient reactiveMongoClient(){
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!! RUNNING TEST CONFIGURATION !!!!!!!!!!!!!!!");
        MongodStarter starter = MongodStarter.getDefaultInstance();

        int port = 27018;
        try {
            IMongodConfig mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();

            this.mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess process = mongodExecutable.start();

        } catch(Exception e){
            //Wrapping Exceptions
            throw new RuntimeException(e);
        }
        return MongoClients.create(new ConnectionString("mongodb://localhost:"+port+"/replicaSet=rs0"));
    }
}
