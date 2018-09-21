package cms.sre.product_list_emitter.repository;

import cms.sre.dna_common_data_model.product_list.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {
    public List<Product> findByTitle(String title);
}
