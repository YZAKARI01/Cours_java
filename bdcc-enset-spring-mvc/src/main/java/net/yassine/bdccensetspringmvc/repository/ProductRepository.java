package net.yassine.bdccensetspringmvc.repository;

import net.yassine.bdccensetspringmvc.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
