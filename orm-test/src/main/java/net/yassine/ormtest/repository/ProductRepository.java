package net.yassine.ormtest.repository;

import net.yassine.ormtest.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Queue;

public interface ProductRepository extends JpaRepository<Product , Long > {

    List<Product> findByNameContainsIgnoreCase(String kw);
    List<Product> findByNameContains(String kw);

    List<Product> findByNameContainsAndPriceGreaterThan(String mc, double price);
    @Query("select p from Product p where p.name like :x and p.price>:y")
    List<Product> search(@Param("x") String mc, @Param("y") double price);
}
