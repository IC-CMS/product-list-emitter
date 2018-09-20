package cms.sre.product_list_emitter;

import com.mongodb.ConnectionString;
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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@org.springframework.boot.test.context.TestConfiguration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class TestConfiguration extends App{
    private static Logger logger = LoggerFactory.getLogger(TestConfiguration.class);

    private MongodExecutable mongodExecutable;

    public TestConfiguration(){
        super.sharepointListLocation = "http://localhost:8080";
    }

    @Override
    public HttpClient httpClient() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException{
        super.httpclientKeyStoreLocation = PathUtils.getAbsolutePathForClasspathResource("test_client_keystore.jks");
        super.httpclientTrustStoreLocation = PathUtils.getAbsolutePathForClasspathResource("test_cacerts.jks");
        return super.httpClient();
    }

    @Override
    public void finalize(){
        logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!! STOPPING TEST CONFIGURATION !!!!!!!!!!!!!!!");
        if(this.mongodExecutable != null)
            this.mongodExecutable.stop();
    }
}
