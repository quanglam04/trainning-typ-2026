package com.example.coursemanagement_finalprojectmodule03.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RabbitMqConfig {
    public static final String EXCHANGE = "course.events";
    public static final String QUEUE = "course.enrollment.notifications";
    public static final String ROUTING_KEY = "enrollment.created";
    public static final String DLX = "course.events.dlx";
    public static final String DLQ = "course.enrollment.notifications.dlq";
    public static final String DLQ_KEY = "enrollment.failed";

    @Bean
    DirectExchange courseExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    Queue enrollmentQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DLQ_KEY)
                .build();
    }

    @Bean
    Queue enrollmentDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    Binding enrollmentBinding() {
        return BindingBuilder.bind(enrollmentQueue()).to(courseExchange()).with(ROUTING_KEY);
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(enrollmentDeadLetterQueue()).to(deadLetterExchange()).with(DLQ_KEY);
    }

    @Bean
    JacksonJsonMessageConverter rabbitJsonConverter() {
        return new JacksonJsonMessageConverter("com.example.coursemanagement_finalprojectmodule03.mq");
    }
}
