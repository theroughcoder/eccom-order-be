package be.ecom_order_be.controller;

import be.ecom_order_be.dto.request.MarkOrderPaidRequest;
import be.ecom_order_be.dto.request.OrderRequest;
import be.ecom_order_be.dto.response.OrderHistoryResponse;
import be.ecom_order_be.dto.response.OrderResponse;
import be.ecom_order_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/history")
    public List<OrderHistoryResponse> getOrderHistory(@RequestHeader("Authorization") String authHeader) {
        return orderService.getOrderHistory(authHeader);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderDetail(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable Long id) {
        return orderService.getOrderDetail(id, authHeader);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody OrderRequest request) {
        return orderService.createOrder(request, authHeader);
    }

    @PutMapping("/{id}/pay")
    public void markOrderPaid(@PathVariable Long id, @RequestBody MarkOrderPaidRequest request) {
        orderService.markOrderPaid(id, request);
    }
}
