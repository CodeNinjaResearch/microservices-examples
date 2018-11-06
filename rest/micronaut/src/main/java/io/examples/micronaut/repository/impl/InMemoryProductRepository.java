package io.examples.micronaut.repository.impl;

import io.examples.micronaut.entity.Product;
import io.examples.micronaut.repository.ProductRepository;

import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implement Product repository in memory
 *
 * @author Gary Cheng
 */
@Singleton
public class InMemoryProductRepository implements ProductRepository {

    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Map<Integer, Product> productMap = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
        ProductUtils.initData().forEach(product -> productMap.put(product.getId(), product));
        idGenerator.set(productMap.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
    }

    @Override
    public List<Product> getProducts() {
        return productMap.values()
                .stream()
                .sorted(Comparator.comparing(Product::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productMap.values()
                .stream()
                .filter(product -> category.equals(product.getCategory()))
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getProductById(Integer id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public Product addProduct(Product product) {
        product.setId(idGenerator.incrementAndGet());
        productMap.put(product.getId(), product);
        return product;
    }

    @Override
    public void updateProduct(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        productMap.put(product.getId(), product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productMap.remove(id);
    }
}
