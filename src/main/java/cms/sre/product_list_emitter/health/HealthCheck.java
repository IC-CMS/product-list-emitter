package cms.sre.product_list_emitter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;

public class HealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        Health ret = Health.up().build();
        return ret;
    }
}
