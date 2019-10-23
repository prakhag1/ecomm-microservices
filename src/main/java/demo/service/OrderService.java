package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.model.Cart;
import demo.repository.CartRepository;

@Service
public class OrderService {
	
	@Autowired
	CartRepository cartRepository;

	public String addCustomerOrder(Cart cart) {
		cartRepository.save(cart);
		return cart.getId();
	}
}
