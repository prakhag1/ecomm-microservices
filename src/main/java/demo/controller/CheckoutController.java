package demo.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import demo.model.Cart;
import demo.service.CartService;
import io.zeebe.client.ZeebeClient;

@Controller
public class CheckoutController {

  @Autowired private HttpSession session;
  @Autowired private CartService cartService;
  @Autowired private ZeebeClient zeebe;

  @RequestMapping(value = "/cart/checkout", method = RequestMethod.POST)
  public String checkout(Model model) throws Exception {
    Cart cart = cartService.getCart();

    // Start BPMN workflow
    zeebe
        .newCreateInstanceCommand()
        .bpmnProcessId("order-process")
        .latestVersion()
        .variables(cart)
        .send()
        .join();

    // Empty cart and render confirmation page to the end user
    cart = cartService.emptyCart();
    session.setAttribute("cart", cart);
    session.setAttribute("cart_size", cart.getCartItems().size());

    return "orderconfirmation";
  }
}
