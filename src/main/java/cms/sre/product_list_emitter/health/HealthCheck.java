package cms.sre.product_list_emitter.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;

public class HealthCheck implements HealthIndicator {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Health health() {
        Health ret = null;
        if(this.mongoTemplate != null){
            ret = new MongoHealthIndicator(this.mongoTemplate)
                    .health();
        } else {
            ret = Health.down()
                    .withDetail("MongoTemplate-Autowrited", false)
                    .build();
        }
        return ret;
    }
}
