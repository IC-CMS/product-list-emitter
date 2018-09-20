package cms.sre.product_list_emitter.service;

import cms.sre.dna_common_data_model.product_list.Product;
import cms.sre.product_list_emitter.repository.ProductRepository;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductPersistenceService {
    private static Logger logger = LoggerFactory.getLogger(ProductPersistenceService.class);
    private MongoTemplate mongoTemplate;
    private ProductRepository productRepository;

    @Autowired
    public ProductPersistenceService(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Product> getProducts() {
        return this.mongoTemplate.findAll(Product.class);
    }

    public boolean upsert(Product product) {
        logger.debug("upsert: product.title = " + product.getTitle());
        Query query = new Query()
                .addCriteria(Criteria.where("title").is(product.getTitle()));
        Update update = new Update()
                .set("classification", product.getClassification())
                .set("developers", product.getDevelopers())
                .set("division", product.getDivision())
                .set("lane", product.getLane())
                .set("lifecycleStatus", product.getLifecycleStatus())
                .set("needsSCM", product.getNeedsSCM())
                .set("org", product.getOrg())
                .set("productStatus", product.getProductStatus())
                .set("program", product.getProgram())
                .set("scmLocation", product.getSCMLocation())
                .set("section", product.getSection())
                .set("sspName", product.getSspName())
                .set("title", product.getTitle());
        UpdateResult updateResult = this.mongoTemplate.upsert(query,update,Product.class);
        return updateResult.wasAcknowledged();
    }

    public boolean remove(List<Product> products) {
        Criteria criteria = new Criteria();
        ArrayList<String> productTitles = new ArrayList<>();
        for (Product product : products) {
            productTitles.add(product.getTitle());
        }
        criteria.where("title").in(productTitles);

        return this.mongoTemplate.remove(new Query().addCriteria(criteria), Product.class).wasAcknowledged();
    }
}
