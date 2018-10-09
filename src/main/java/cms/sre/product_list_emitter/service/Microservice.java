package cms.sre.product_list_emitter.service;

import cms.sre.dna_common_data_model.product_list.Product;
import cms.sre.product_list_emitter.dao.ProductListDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class Microservice implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(Microservice.class);

    @Autowired
    private ProductPersistenceService productPersistenceService;

    @Autowired
    private ProductListDao productListDao;

    //This denotes the method should run every five minutes Monday - Friday, unless overridden by properties.
    //@see https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
    @Scheduled(cron = "${sharepoint.schedule:0 */5 * * * MON-FRI}")
    public void run(){
        try {
            // Populate list of products to delete; start with all products
            ArrayList<Product> productsToDelete = new ArrayList<>();
            productsToDelete.addAll(productPersistenceService.getProducts());
            for (Product product: productListDao.getProducts()) {
                //TODO: implmement RESTful POST/DELETE to persistence-api, maybe?
                //TODO: delete products that are no longer in the list
                logger.debug("product.title = " + product.getTitle());
                // Upsert products; if new it'll be added, otherwise it'll be updated
                boolean upsertStatus = productPersistenceService.upsert(product);
                logger.debug("upsertStatus = " + upsertStatus);
                // Remove product that is in sharepoint from the list of products to delete
                // We're doing it in this roundabout way because I ran into problems
                // finding or removing objects from the collection
                int idx = -1;
                for (Product p2 : productsToDelete) {
                    if (p2.getTitle().equals(product.getTitle()))
                        idx = productsToDelete.indexOf(p2);
                }
                if (idx != -1)
                    productsToDelete.remove(idx);
            }
            // Delete any products that are no longer in sharepoint
            logger.debug("productsToDelete.size = " + productsToDelete.size());
            if (productsToDelete.size() > 0) {
                productPersistenceService.remove(productsToDelete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e){
            e.printStackTrace();
        }
    }
}
