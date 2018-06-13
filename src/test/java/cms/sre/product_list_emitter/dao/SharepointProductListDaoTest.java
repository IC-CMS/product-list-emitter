package cms.sre.product_list_emitter.dao;

import cms.sre.dna_common_data_model.product_list.Product;
import cms.sre.httpclient_connection_helper.embedded.EmbeddedServer;
import cms.sre.httpclient_connection_helper.embedded.InsecureServer;
import cms.sre.product_list_emitter.TestConfiguration;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class SharepointProductListDaoTest {
    private static final String response = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"results\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"_metadata\":{\n" +
            "\t\t\t\t\t\"id\": \"Web/Lists(guid'2ead740c-40f2-45b6-9ce8-7aa69a901109')/Items(130)\",\n" +
            "\t\t\t\t\t\"uri\": \"https://sample.foo.bar.org/_api/Web/Lists(guid'2ead740c-40f2-45b6-9ce8-7aa69a901109')/Items(130)\",\n" +
            "\t\t\t\t\t\"etag\": \"115\",\n" +
            "\t\t\t\t\t\"type\": \"SP.Data.CMS_x0020_Tool_x0020_ListListItem\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Developer_x0028_s_x0029_\":{\n" +
            "\t\t\t\t\t\"_deferred\":{\n" +
            "\t\t\t\t\t\t\"uri\": \"https://sample.foo.bar.org/_api/Web/Lists(guid'2ead740c-40f2-45b6-9ce8-7aa69a901109')/Items(130)/Developer_x0028_s_x0029_\"\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Program\":{\n" +
            "\t\t\t\t\t\"_metadata\":{\n" +
            "\t\t\t\t\t\t\"id\": \"80b0b5a8-c1d2-45ff-a2a3-8e59170a9904\",\n" +
            "\t\t\t\t\t\t\"type\": \"SP.Data.PortfolioListItem\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"Title\": \"Information Sharing Services (ISS)\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Section\":{\n" +
            "\t\t\t\t\t\"_metadata\":{\n" +
            "\t\t\t\t\t\t\"id\": \"a67a7611-375d-4ffb-a6df-4ced98e7a168\",\n" +
            "\t\t\t\t\t\t\"type\": \"SP.Data.SectionListItem\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"Title\": \"Work Management\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Title\": \"Accomplishment\",\n" +
            "\t\t\t\t\"Text_x0020_Classification\": \"UNKNOWN\",\n" +
            "\t\t\t\t\"SSP_x0020_Name\": \"FOOBAR\",\n" +
            "\t\t\t\t\"Livecycle_x0020_Status\": \"Sustainment\",\n" +
            "\t\t\t\t\"Active\": \"Approved\",\n" +
            "\t\t\t\t\"Lane\": \"BPS\",\n" +
            "\t\t\t\t\"Org\": \"A1234\",\n" +
            "\t\t\t\t\"Division\": \"BPS\",\n" +
            "\t\t\t\t\"NoCode\": false,\n" +
            "\t\t\t\t\"SCMLocation\": null\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t}\n" +
            "}";

    @Autowired
    private ProductListDao productListDao;

    @Test
    public void autowiringTest(){
        Assert.assertNotNull(this.productListDao);
        Assert.assertTrue(this.productListDao instanceof SharepointProductListDao);

        SharepointProductListDao sharepointProductListDao = (SharepointProductListDao) this.productListDao;
        Assert.assertNotNull(sharepointProductListDao.getHttpClient());
    }

    @Test
    public void noDevelopersTest() throws IOException {
        EmbeddedServer server = new InsecureServer(response);
        server.start();
        List<Product> products = new SharepointProductListDao(HttpClients.createMinimal(), server.getUrl()).getProducts();
        server.stop();

        Assert.assertEquals(1, products.size());
        Assert.assertNotNull(products.get(0));
        Assert.assertEquals("Information Sharing Services (ISS)", products.get(0).getProgram());
        Assert.assertEquals("Work Management", products.get(0).getSection());
        Assert.assertEquals("Accomplishment", products.get(0).getTitle());
        Assert.assertEquals("UNKNOWN", products.get(0).getClassification());
        Assert.assertEquals("FOOBAR", products.get(0).getSspName());
        Assert.assertEquals("Sustainment", products.get(0).getLifecycleStatus());
        Assert.assertEquals("Approved", products.get(0).getProductStatus());
        Assert.assertEquals("BPS", products.get(0).getLane());
        Assert.assertEquals("A1234", products.get(0).getOrg());
        Assert.assertEquals("BPS", products.get(0).getDivision());
        Assert.assertEquals(true, products.get(0).getNeedsSCM());
        Assert.assertNull(products.get(0).getSCMLocation());
    }



}
