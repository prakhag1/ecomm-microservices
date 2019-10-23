package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import demo.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{

}
