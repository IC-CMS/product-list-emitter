package cms.sre.product_list_emitter;

import cms.sre.httpclient_connection_helper.HttpClientFactory;
import cms.sre.httpclient_connection_helper.HttpClientParameters;
import cms.sre.mongo_reactive_connection_helper.MongoClientParameters;
import cms.sre.mongo_reactive_connection_helper.MongoReactiveClientFactory;
import cms.sre.product_list_emitter.dao.ProductListDao;
import cms.sre.product_list_emitter.dao.SharepointProductListDao;
import com.mongodb.reactivestreams.client.MongoClient;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@EnableScheduling
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class})
@PropertySource(value = "file:/data/product-list-emitter/configuration/application.properties", ignoreResourceNotFound = true)
@EnableReactiveMongoRepositories
public class App extends AbstractReactiveMongoConfiguration {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

    @Value("${mongodb.databaseName:#{null}}")
    protected String mongoDatabaseName;

    @Value("${mongodb.keyStoreKeyPassword:#{null}}")
    protected String mongoKeyStoreKeyPassword;

    @Value("${mongodb.keyStoreLocation:#{null}}")
    protected String mongoKeyStoreLocation;

    @Value("${mongodb.keyStorePassword:#{null}}")
    protected String mongoKeyStorePassword;

    @Value("${mongodb.trustStoreLocation:#{null}}")
    protected String mongoTrustStoreLocation;

    @Value("${mongodb.trustStorePassword:#{null}}")
    protected String mongoTrustStorePassword;

    @Value("${mongodb.username:#{null}}")
    protected String mongoUsername;

    @Value("${mongodb.password:#{null}}")
    protected String mongoPassword;

    @Value("${mongodb.replicaSetLocation:#{null}}")
    protected String[] mongoReplicaSetLocation;

    @Value("${mongodb.mongoReplicaSetName:#{null}}")
    protected String mongoReplicaSetName;

    @Value("${sharepoint.listLocation:#{null}}")
    protected String sharepointListLocation;

    @Value("${httpclient.keyStoreKeyPassword:#{null}}")
    protected String httpclientKeyStoreKeyPassword;

    @Value("${httpclient.keyStoreLocation:#{null}}")
    protected String httpclientKeyStoreLocation;

    @Value("${httpclient.keyStorePassword:#{null}}")
    protected String httpclientKeyStorePassword;

    @Value("${httpclient.trustStoreLocation:#{null}}")
    protected String httpclientTrustStoreLocation;

    @Value("${httpclient.trustStorePassword:#{null}}")
    protected String httpclientTrustStorePassword;

    protected String productListLocation(){
        String ending = "/AllItems.aspx";
        String ret = this.sharepointListLocation;
        if(ret != null && ret.endsWith(ending)){
            ret.substring(0, ret.length() - ending.length());
        }
        return ret;
    }

    @Bean
    protected ProductListDao productListDao() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logger.info("sharepoint.listLocation : " + this.sharepointListLocation);

        return new SharepointProductListDao(this.httpClient(), this.productListLocation());
    }

    private static String nullWrapper(String value){
        return value == null ? "(null)" : value;
    }

    private static String nullWrapper(String[] values){
        String ret = null;
        if(values != null && values.length > 0){
            ret = "";
            for (String value : values) {
                ret += nullWrapper(value);
            }
        }
        return ret;
    }

    @Bean
    public HttpClient httpClient() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logger.info("HttpClient Configuration Values");
        logger.info("httpclient.keyStoreKeyPassword : " + this.httpclientKeyStoreKeyPassword);
        logger.info("httpclient.keyStoreLocation : " + this.httpclientKeyStoreLocation);
        logger.info("httpclient.keyStorePassword : " + this.httpclientKeyStorePassword);
        logger.info("httpclient.trustStoreLocation : " + this.httpclientTrustStoreLocation);
        logger.info("httpclient.trustStorePassword : " + this.httpclientTrustStorePassword);

        HttpClientParameters parameters = new HttpClientParameters()
                .setKeyStoreKeyPassword(this.httpclientKeyStoreKeyPassword)
                .setKeyStoreLocation(this.httpclientKeyStoreLocation)
                .setKeyStoreKeyPassword(this.httpclientKeyStorePassword)
                .setTrustStoreLocation(this.httpclientTrustStoreLocation)
                .setTrustStorePassword(this.mongoTrustStorePassword);

        return HttpClientFactory.httpClient(parameters);
    }

    @Override
    public MongoClient reactiveMongoClient() {
        logger.info("Mongo Configuration Values");
        logger.info("mongodb.keyStoreKeyPassword : " + nullWrapper(this.mongoKeyStoreKeyPassword));
        logger.info("mongodb.keyStorePassword : " + nullWrapper(this.mongoKeyStorePassword));
        logger.info("mongodb.keyStoreLocation : " + nullWrapper(this.mongoKeyStoreLocation));
        logger.info("mongodb.trustStoreLocation : " + nullWrapper(this.mongoTrustStoreLocation));
        logger.info("mongodb.trustStorePassword : " + nullWrapper(this.mongoTrustStorePassword));
        logger.info("mongodb.databaseName : " + nullWrapper(this.mongoDatabaseName));
        logger.info("mongodb.username : " + nullWrapper(this.mongoUsername));
        logger.info("mongodb.password : " + nullWrapper(this.mongoPassword));
        logger.info("mongodb.replicaSetLocation : " + nullWrapper(this.mongoReplicaSetLocation));
        logger.info("mongodb.mongoReplicaSetName : " + nullWrapper(this.mongoReplicaSetName));

        MongoClientParameters params = new MongoClientParameters()
                .setKeyStoreKeyPassword(this.mongoKeyStoreKeyPassword)
                .setKeyStoreLocation(this.mongoKeyStoreLocation)
                .setKeyStorePassword(this.mongoKeyStorePassword)
                .setTrustStoreLocation(this.mongoTrustStoreLocation)
                .setTrustStorePassword(this.mongoTrustStorePassword)
                .setDatabaseName(this.mongoDatabaseName)
                .setMongoUsername(this.mongoUsername)
                .setMongoPassword(this.mongoPassword)
                .setReplicaSetLocations(this.mongoReplicaSetLocation)
                .setReplicaSetName(this.mongoReplicaSetName);

        MongoClient client = null;
        if(this.mongoReplicaSetLocation.length == 1 && this.mongoReplicaSetLocation[0].equalsIgnoreCase("localhost")){
            logger.debug("Connecting to Local Mongo");
            if(this.mongoUsername != null && this.mongoUsername.length() > 0 && this.mongoPassword != null && this.mongoPassword.length() > 0){
                client = MongoReactiveClientFactory.getLocahostMongoClient(this.mongoDatabaseName, this.mongoUsername, this.mongoPassword);
            }else{
                client = MongoReactiveClientFactory.getLocalhostMongoClient();
            }
        }else{
            logger.debug("Connecting to Remote Mongo located at " + this.mongoReplicaSetLocation + " with the replica set name of " + this.mongoReplicaSetName);

            client = MongoReactiveClientFactory.getMongoClient(params);
        }
        return client;
    }

    protected String getDatabaseName() {
        return this.mongoDatabaseName;
    }
}
