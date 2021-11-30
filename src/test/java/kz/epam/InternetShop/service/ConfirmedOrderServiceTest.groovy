package kz.epam.InternetShop.service

import kz.epam.InternetShop.model.Order
import kz.epam.InternetShop.model.OrderDetails
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.repository.OrderDetailsRepository
import kz.epam.InternetShop.repository.OrderRepository
import kz.epam.InternetShop.service.impl.ConfirmedOrderServiceImpl
import kz.epam.InternetShop.service.interfaces.ConfirmedOrderService
import spock.lang.Specification

class ConfirmedOrderServiceTest extends Specification{

    OrderRepository orderRepository = Mock()
    OrderDetailsRepository orderDetailsRepository = Mock()

    ConfirmedOrderService confirmedOrderService = new ConfirmedOrderServiceImpl(orderRepository,orderDetailsRepository)


    def "getAllOrders() should return all orders for specific user"() {

        given:
            Order order = Order.builder()
                .id(1L)
                .build()

            def list = [order]

        when:
            def result = confirmedOrderService.getAllOrders(new User())

        then:
            1 * orderRepository.findAllByUserAndStatus(new User(), 1) >> list

        and:
            list == result
    }

    def "getAllOrderDetails() should return details for specific order"() {

        given:
            OrderDetails orderDetails = OrderDetails.builder()
                    .id(1L)
                    .build()

            def list = [orderDetails]


        when:
            def result = confirmedOrderService.getAllOrderDetails(new Order())

        then:
            1 *  orderDetailsRepository.findByOrder(new Order()) >> list

        and:
            list == result
    }

}
