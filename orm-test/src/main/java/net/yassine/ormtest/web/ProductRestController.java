package net.yassine.ormtest.web;

import net.yassine.ormtest.entities.Product;
import net.yassine.ormtest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> products(){
        return productRepository.findAll();
    }
    @GetMapping("/products/{id}")
    public Product productById(@PathVariable Long id){
        return productRepository.findById(id).get();
    }
    @GetMapping("/find/products")
    public List<Product> findProducts(@RequestParam(name="kw" , defaultValue = "") String kw){
        return productRepository.findByNameContains(kw);
    }
    @PostMapping("/products")
    public Product save(@RequestBody Product product){
        return productRepository.save(product);
    }

}
