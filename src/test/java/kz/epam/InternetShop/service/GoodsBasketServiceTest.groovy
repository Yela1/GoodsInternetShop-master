package kz.epam.InternetShop.service

import kz.epam.InternetShop.model.Goods
import kz.epam.InternetShop.model.Order
import kz.epam.InternetShop.model.OrderDetails
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.repository.GoodsRepository
import kz.epam.InternetShop.repository.OrderDetailsRepository
import kz.epam.InternetShop.repository.OrderRepository
import kz.epam.InternetShop.service.impl.GoodsBasketServiceImpl
import kz.epam.InternetShop.service.interfaces.GoodsBasketService
import kz.epam.InternetShop.util.exception.NotAvailableGoodsException
import kz.epam.InternetShop.util.exception.NotFoundException
import spock.lang.Specification
class GoodsBasketServiceTest extends Specification{

    OrderRepository orderRepository = Mock()
    OrderDetailsRepository orderDetailsRepository = Mock()
    GoodsRepository goodsRepository = Mock()
    GoodsBasketService goodsBasketService = new GoodsBasketServiceImpl(
                                                    orderRepository,
                                                    orderDetailsRepository,
                                                    goodsRepository)

    User user
    OrderDetails orderDetails
    Order order
    Goods goods

    def setup(){
        goods = Goods.builder()
                .id(1L)
                .count(5)
                .build()

        orderDetails = OrderDetails.builder()
                .id(1L)
                .available(true)
                .goods(goods)
                .count(5)
                .build()

        order = Order.builder()
                .id(1L)
                .orderDetails([orderDetails])
                .status(0)
                .build()

        user = User.builder()
                .orders([order,order])
                .build()

    }

    def "getAllOrderDetail() should return OrderDetails"(){
        given:
            def orderDetailsList = [orderDetails]
            def orderList = [order]

        when:
            def result = goodsBasketService.getAllOrderDetails(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user,0) >> orderList
            1 * orderDetailsRepository.findByOrder(order) >> orderDetailsList
            1 * goodsRepository.findById(1L) >> Optional.of(goods)

        and:
            orderDetailsList == result
    }

    def "clear() should delete OrderDetails"() {
        given:
            def orderList = [order]

        when:
            goodsBasketService.clear(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0) >> orderList
            1 * orderRepository.delete(order)

    }

    def "setStatusToOne() should set status of basket"(){
        given:
            def orderList = [order]

        when:
            goodsBasketService.setStatusToOne(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0) >> orderList
            1 * goodsRepository.findById(1L) >> Optional.of(goods)
            1 * orderRepository.save(order)

        and:
            order.getStatus() == 1

    }

    def "setStatusToOne() should throw NotAvailableGoodsException if orderDetails not available"(){
        given:
            orderDetails.setAvailable(false)
            orderDetails.setCount(6)
            def orderList = [order]

        when:
            goodsBasketService.setStatusToOne(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0 ) >> orderList
            1 * goodsRepository.findById(1L) >> Optional.of(goods)

        and:
            thrown(NotAvailableGoodsException)

    }

    def "getBasket() should return order if order size > 0"() {
        given:
            def orderList = [order]

        when:
            goodsBasketService.getBasket(user)

        then:
            orderRepository.findAllByUserAndStatus(user,0) >> orderList
    }

    def "getBasket() should save order if order size == 0"(){
        given:
            def expectedStatus = 0

        when:
            def result = goodsBasketService.getBasket(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0) >> []
            1 * orderRepository.save(_) >> order

        and:
            expectedStatus == result.getStatus()


    }


    def "createOrderDetailsInBasket() should save OrderDetails if exist" (){
        when:
            def result = goodsBasketService.createOrderDetailsInBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0) >> [order]
            1 * orderDetailsRepository.findByOrderAndGoodsAndCost(_, orderDetails.getGoods(), orderDetails.getCost()) >> orderDetails
            1 * orderDetailsRepository.save(_) >> orderDetails

        and:
            result.getCount() == 10

    }

    def "createOrderDetailsInBasket() should create new OrderDetails"() {
        when:
            goodsBasketService.createOrderDetailsInBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0) >> [order]
            1 * orderDetailsRepository.findByOrderAndGoodsAndCost(_, orderDetails.getGoods(), orderDetails.getCost()) >> null
            1 * orderDetailsRepository.save(_)

    }

    def "updateCountOrderDetailsInBasket() delete orderDetails if count==0"(){
        given:
            orderDetails.setCount(0)

        when:
            goodsBasketService.updateCountOrderDetailsInBasket([orderDetails], user)

        then:
            1 * orderDetailsRepository.findById(orderDetails.getId()) >> Optional.of(orderDetails)
            1 * orderDetailsRepository.delete(orderDetails)

    }

    def "updateCountOrderDetailsInBasket() should update if count > 0"(){
        given:
            orderDetails.setOrder(Order.builder().id(1L).build());

        when:
            goodsBasketService.updateCountOrderDetailsInBasket([orderDetails], user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user,0) >> [order]
            1 * orderDetailsRepository.findById(orderDetails.getId()) >> Optional.of(orderDetails)
            1 * orderDetailsRepository.updateCount(orderDetails.getId(), orderDetails.getCount())
    }

    def "updateCountOrderDetailsInBasket() should throw NotFoundException"(){
        when:
            goodsBasketService.updateCountOrderDetailsInBasket([orderDetails], user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user,0) >> [order]
            1 * orderDetailsRepository.findById(orderDetails.getId()) >> Optional.empty()

        and:
            thrown(NotFoundException)
    }

    def "removeFromBasket() should remove orderDetails"() {
        given:
            orderDetails.setOrder(Order.builder().id(1L).build());

        when:
            goodsBasketService.removeFromBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, 0) >> [order]
            1 * orderDetailsRepository.findById(orderDetails.getId()) >> Optional.of(orderDetails)
            1 * orderDetailsRepository.delete(orderDetails)

    }

    def "removeFromBasket() should throw NotFoundException"() {
        when:
            goodsBasketService.removeFromBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user,0) >> [order]
            1 * orderDetailsRepository.findById(orderDetails.getId()) >> Optional.empty()

        and:
            thrown(NotFoundException)

    }
}
