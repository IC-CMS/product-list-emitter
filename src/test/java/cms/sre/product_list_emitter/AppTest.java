package cms.sre.product_list_emitter;

import cms.sre.product_list_emitter.dao.ProductListDao;
import cms.sre.product_list_emitter.dao.SharepointProductListDao;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class AppTest {
    @Autowired
    private ProductListDao productListDao;

    @Autowired
    private MongoClient mongoClient;
@Ignore
    @Test
    public void contextLoadsTest(){
        ;
    }
    @Ignore
    @Test
    public void productListDaoAutowiredTest(){
        Assert.assertNotNull(this.productListDao);
        Assert.assertTrue(this.productListDao instanceof SharepointProductListDao);
        SharepointProductListDao sharepointProductListDao = (SharepointProductListDao) this.productListDao;
        Assert.assertEquals("http://localhost:8080", sharepointProductListDao.getProductListLocation());
        Assert.assertNotNull(sharepointProductListDao.getHttpClient());
        Assert.assertNotNull(sharepointProductListDao.getHttpClient().getConnectionManager());
    }
    @Ignore
    @Test
    public void mongoClientAutowiredTest(){
        Assert.assertNotNull(this.mongoClient);
        Assert.assertNotNull(this.mongoClient.getSettings().getSslSettings().getContext());
    }
}
