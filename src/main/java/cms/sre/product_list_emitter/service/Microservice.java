package cms.sre.product_list_emitter.service;

import cms.sre.dna_common_data_model.product_list.NewProductDiscoveredEvent;
import cms.sre.product_list_emitter.dao.NewProductDiscoverEventReactiveRepository;
import cms.sre.product_list_emitter.dao.ProductListDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Microservice implements Runnable{

    @Autowired
    private ProductListDao productListDao;

    @Autowired
    private NewProductDiscoverEventReactiveRepository newProductDiscoverEventReactiveRepository;

    //This denotes the method should run every ten seconds Monday - Friday.
    //@see https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
    @Scheduled(cron = "*/10 * * * * MON-FRI")
    public void run(){
        try {
            this.productListDao.getProducts()
                    .forEach(product -> {
                        this.newProductDiscoverEventReactiveRepository.save(new NewProductDiscoveredEvent().setProduct(product));
                    });
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e){
            e.printStackTrace();
        }
    }
}
