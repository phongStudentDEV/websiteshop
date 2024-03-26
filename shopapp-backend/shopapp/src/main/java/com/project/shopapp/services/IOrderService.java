package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;

    Order getOrderById(Long id);

    List<Order> getOrders(Long userId);

    Order updateOrder(Long id, OrderDTO orderDTO) throws Exception;

    void deleteOrder(Long id);
}
