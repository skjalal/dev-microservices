package com.example.beerorderservice.sm;

import com.example.beerorderservice.domain.BeerOrderEventEnum;
import com.example.beerorderservice.domain.BeerOrderStatusEnum;
import com.example.beerorderservice.repositories.BeerOrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class StateMachineServiceConfig {

  @Bean
  public StateMachineRuntimePersister<BeerOrderStatusEnum, BeerOrderEventEnum, String> stateMachinePersist(
      BeerOrderRepository beerOrderRepository) {
    return new StateMachinePersistImpl(beerOrderRepository);
  }

  @Bean
  public StateMachineService<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineService(
      StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory,
      StateMachineRuntimePersister<BeerOrderStatusEnum, BeerOrderEventEnum, String> stateMachinePersist) {
    return new DefaultStateMachineService<>(stateMachineFactory, stateMachinePersist);
  }
}
