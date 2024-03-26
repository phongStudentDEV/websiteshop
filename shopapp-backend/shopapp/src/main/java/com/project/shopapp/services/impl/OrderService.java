package com.project.shopapp.services.impl;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final LocalizationUtils localizationUtils;


    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()-> new DataNotFoundException("Cannot find user with id = "+ orderDTO.getUserId()));

        // conver orderDTO -> order dùng thư viện ModelMapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);

        // kiem tra shipping_date >= hom nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today");
        }
        order.setShippingDate(shippingDate);

        // luu order vao DB
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> getOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find order with id = " + id));

        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()-> new DataNotFoundException("Cannot find user of order"));

        if (order.getUser().getId() != user.getId()){
            throw new DataNotFoundException("the order does not belong to the user");
        }

        //ánh xạ các trường dữ liệu từ orderDTO -> order bỏ qua id
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper-> mapper.skip(Order::setId));

        modelMapper.map(orderDTO, order);
        order.setUser(user);

        orderRepository.save(order);
        return order;
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }


}
