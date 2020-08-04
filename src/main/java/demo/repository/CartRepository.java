package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import demo.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	@Query("SELECT t FROM Cart t where t.id = ?1") 
	Cart findOrderById(String orderId);
}

