package com.courseshop.courseshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;



import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration 
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;
    
    @Value("${rabbitmq.queue.json.name}")
    private String jsonQueue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.routing.json.key}")
    private String routingJsonKey;

    //spring bean for rabbitmq queue
    @Bean 
    public Queue queue(){
        return new Queue(queue, true);
    }

    //spring bean for queue_json
    @Bean
    public Queue jsonQueue(){
        return new Queue(jsonQueue, true);
    }

    //rabbit exchange
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    //binding giữa exchange và queue sử dụng routing key
    @Bean
    public Binding binding(
            @Qualifier("queue") Queue queue,
            @Qualifier("exchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }

    //binding giữa exchange và queue_json sử dụng routing key
    @Bean
    public Binding jsonBinding(
            @Qualifier("jsonQueue") Queue jsonQueue,
            @Qualifier("exchange") TopicExchange exchange) {
        return BindingBuilder.bind(jsonQueue)
                .to(exchange)
                .with(routingJsonKey);
    }

    // json object khi gửi và object lại khi nhận...
    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }
    // gửi json...
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
