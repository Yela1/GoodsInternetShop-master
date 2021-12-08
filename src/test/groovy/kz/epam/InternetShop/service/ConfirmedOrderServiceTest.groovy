package kz.epam.InternetShop.service

import kz.epam.InternetShop.repository.OrderDetailsRepository
import kz.epam.InternetShop.repository.OrderRepository
import kz.epam.InternetShop.service.impl.ConfirmedOrderServiceImpl
import kz.epam.InternetShop.service.interfaces.ConfirmedOrderService
import spock.lang.Specification


import static kz.epam.InternetShop.ObjectCreator.*

class ConfirmedOrderServiceTest extends Specification{

    OrderRepository orderRepository = Mock()
    OrderDetailsRepository orderDetailsRepository = Mock()

    ConfirmedOrderService confirmedOrderService = new ConfirmedOrderServiceImpl(orderRepository,orderDetailsRepository)


    def "getAllOrders() should return all orders for specific user"() {
        given:
            def order = createOrder(true, 5)
            def user = createUser(1L)
            def expectedList = [order]

        when:
            def result = confirmedOrderService.getAllOrders(user)

        then:
            1 * orderRepository.findAllByUserAndStatus({it.id == user.getId()}, {it == 1}) >> expectedList

        and:
            expectedList == result
    }

    def "getAllOrderDetails() should return details for specific order"() {

        given:
            def orderDetails = createOrderDetails()
            def expectedList = [orderDetails]
            def order = createOrder(true, 5)


        when:
            def result = confirmedOrderService.getAllOrderDetails(order)

        then:
            1 *  orderDetailsRepository.findByOrder({it.status == order.getStatus()}) >> expectedList

        and:
            expectedList == result
    }
}
