package katsapa.spring.productservice.domain.service;

import katsapa.spring.productservice.api.OrderCreateRequest;
import katsapa.spring.productservice.api.OrderUpdateRequest;
import katsapa.spring.productservice.domain.db.OrderEntity;
import katsapa.spring.productservice.domain.db.OrderRepository;
import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.db.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderEntity create(OrderCreateRequest request) {
        log.info("Creating order for productId={}, quantity={}", request.productId(), request.quantity());

        ProductEntity product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + request.productId()));

        OrderEntity order = OrderEntity.builder()
                .product(product)
                .quantity(request.quantity())
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public OrderEntity update(Long id, OrderUpdateRequest request) {
        log.info("Updating order id={}", id);

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        if (request.quantity() != null) {
            order.setQuantity(request.quantity());
        }
        if (request.status() != null) {
            order.setStatus(request.status());
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderEntity getById(Long id) {
        log.info("Getting order from DB: id={}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getByProductId(Long productId) {
        log.info("Getting orders for productId={}", productId);
        return orderRepository.findAllByProductId(productId);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting order id={}", id);
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }
}