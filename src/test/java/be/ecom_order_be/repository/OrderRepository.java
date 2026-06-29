package be.ecom_order_be.repository;

import be.ecom_order_be.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
