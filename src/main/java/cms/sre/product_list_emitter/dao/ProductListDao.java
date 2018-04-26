package cms.sre.product_list_emitter.dao;

import cms.sre.dna_common_data_model.product_list.Product;

import java.util.List;

public interface ProductListDao {
    public List<Product> getProducts();
}
