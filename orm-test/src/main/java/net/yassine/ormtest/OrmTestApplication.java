package net.yassine.ormtest;

import net.yassine.ormtest.entities.Product;
import net.yassine.ormtest.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class OrmTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrmTestApplication.class, args);
    }

    @Bean
    CommandLineRunner star(ProductRepository productRepository){
        return args -> {
            Product p = Product.builder()
                    .name("Computer")
                    .price(6500)
                    .quantity(4)
                    .build();
            productRepository.save(p);
            productRepository.save(Product.builder().name("Printer").quantity(4).price(640).build());
            productRepository.save(Product.builder().name("Smart Phone").quantity(34).price(1200).build());

            List<Product> products = productRepository.findByNameContainsIgnoreCase("M");
            products.forEach(prod->{
                System.out.println(prod.toString());
            });
        };
    }
}
