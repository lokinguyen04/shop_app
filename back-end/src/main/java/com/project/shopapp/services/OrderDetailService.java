package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception {
        //tìm xem orderId có tồn tại không?
        Order order = orderRepository.findById(newOrderDetail.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                                "Can not find order with id: " + newOrderDetail.getOrderId()));
        //tìm xem productId có tồn tại không?
        Product product = productRepository.findById(newOrderDetail.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Can not find product with id: " + newOrderDetail.getProductId()));
        //tạo mới một orderDetail
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(newOrderDetail.getNumberOfProducts())
                .price(newOrderDetail.getPrice())
                .totalMoney(newOrderDetail.getTotalMoney())
                .color(newOrderDetail.getColor())
                .build();
        // lưu vào CSDL
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id).orElseThrow(() -> new DataNotFoundException(
                "Can not find order detail with id: " + id));
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO)
            throws DataNotFoundException {
        //tìm xem orderDetail có tồn tại không?
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        "Can not find order detail with id: " + id));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Can not find order with id: " + orderDetailDTO.getOrderId()));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Can not find product with id: " + orderDetailDTO.getProductId()));
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
