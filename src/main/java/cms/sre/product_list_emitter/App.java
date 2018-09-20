package cms.sre.product_list_emitter;

import cms.sre.httpclient_connection_helper.HttpClientFactory;
import cms.sre.httpclient_connection_helper.HttpClientParameters;
import cms.sre.product_list_emitter.dao.ProductListDao;
import cms.sre.product_list_emitter.dao.SharepointProductListDao;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@EnableMongoRepositories
@EnableScheduling
@SpringBootApplication
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

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

    @Bean
    public HttpClient httpClient() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        logger.info("HttpClient Configuration Values");
        //logger.info("httpclient.keyStoreKeyPassword : " + this.httpclientKeyStoreKeyPassword);
        logger.info("httpclient.keyStoreLocation : " + this.httpclientKeyStoreLocation);
        //logger.info("httpclient.keyStorePassword : " + this.httpclientKeyStorePassword);
        logger.info("httpclient.trustStoreLocation : " + this.httpclientTrustStoreLocation);
        //logger.info("httpclient.trustStorePassword : " + this.httpclientTrustStorePassword);

        HttpClientParameters parameters = new HttpClientParameters()
                .setKeyStoreKeyPassword(this.httpclientKeyStoreKeyPassword)
                .setKeyStoreLocation(this.httpclientKeyStoreLocation)
                .setKeyStorePassword(this.httpclientKeyStorePassword)
                .setTrustStoreLocation(this.httpclientTrustStoreLocation)
                .setTrustStorePassword(this.httpclientTrustStorePassword);

        return HttpClientFactory.httpClient(parameters);
    }

}
