package com.vulpes.velox.services;

import com.vulpes.velox.models.Order;
import com.vulpes.velox.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

  private OrderRepository orderRepository;

  @Autowired
  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public List<Order> getAll() {
    return orderRepository.findAll();
  }

  @Override
  public void save(Order order) {
    orderRepository.save(order);
  }
}
