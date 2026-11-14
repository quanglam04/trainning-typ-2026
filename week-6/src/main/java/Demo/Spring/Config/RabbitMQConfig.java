package Demo.Spring.Config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //configuring the queue
    @Bean
    public Queue queue(){
        return new Queue("queue");
    }
    //configuring the exchange
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange("exchange" );
    }
    //Creating the binding
    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with("routing_key");
    }


    @Bean
    public Queue orderConfirmationQueue(){
        return QueueBuilder.durable("order_confirmation_queue").build();
    }
    @Bean
    public Binding orderConfirmationBinding(){
        return BindingBuilder
                .bind(orderConfirmationQueue())
                .to(exchange())
                .with("order.created");
    }
    @Bean
    public MessageConverter converter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
