package be.ecom_order_be.kafka;

import com.yourapp.events.order.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    public static final String ORDER_PLACED_TOPIC = "order.placed";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderPlaced(OrderPlacedEvent event) {
        kafkaTemplate.send(ORDER_PLACED_TOPIC, event.orderId(), event);
        log.info("Published OrderPlacedEvent {} for order {}", event.eventId(), event.orderId());
    }
}
