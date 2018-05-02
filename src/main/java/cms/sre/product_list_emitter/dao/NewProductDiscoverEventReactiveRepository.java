package cms.sre.product_list_emitter.dao;

import cms.sre.dna_common_data_model.product_list.NewProductDiscoveredEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface NewProductDiscoverEventReactiveRepository extends ReactiveCrudRepository<NewProductDiscoveredEvent, UUID> {

}
