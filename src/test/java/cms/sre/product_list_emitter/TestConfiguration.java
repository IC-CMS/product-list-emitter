package cms.sre.product_list_emitter;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

//@PropertySource("classpath:test.properties")
@org.springframework.boot.test.context.TestConfiguration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class TestConfiguration extends App{
    private static Logger logger = LoggerFactory.getLogger(TestConfiguration.class);

    private MongodExecutable mongodExecutable;

    public TestConfiguration(){
        super.mongoDatabaseName = "test";
        super.sharepointListLocation = "http://localhost:8080";


    }

    @Override
    public HttpClient httpClient() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException{
        super.httpclientKeyStoreLocation = PathUtils.getAbsolutePathForClasspathResource("test_client_keystore.jks");
        super.httpclientTrustStoreLocation = PathUtils.getAbsolutePathForClasspathResource("test_cacerts.jks");
        return super.httpClient();
    }

    @Override
    public com.mongodb.reactivestreams.client.MongoClient reactiveMongoClient(){
        super.mongoKeyStoreLocation = PathUtils.getAbsolutePathForClasspathResource("test_client_keystore.jks");
        super.mongoTrustStoreLocation = PathUtils.getAbsolutePathForClasspathResource("test_cacerts.jks");
        logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!! RUNNING TEST CONFIGURATION !!!!!!!!!!!!!!!");
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

    @Override
    public void finalize(){
        logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!! STOPPING TEST CONFIGURATION !!!!!!!!!!!!!!!");
        if(this.mongodExecutable != null)
            this.mongodExecutable.stop();
    }
}
