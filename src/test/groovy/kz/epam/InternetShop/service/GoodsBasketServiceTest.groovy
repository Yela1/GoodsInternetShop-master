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


    def "getAllOrderDetail() should return OrderDetails"(){
        given:
            def orderDetails = createOrderDetails()
            def order = createOrder()
            def user = createUser()
            def goods = createGoods()

        when:
            def result = goodsBasketService.getAllOrderDetails(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0}) >> [order]
            1 * orderDetailsRepository.findByOrder({ it.id == order.getId() && it.status == order.getStatus() }) >> [orderDetails]
            1 * goodsRepository.findById({it == orderDetails.getId()}) >> Optional.of(goods)

        and:
            [orderDetails] == result
    }

    def "getAllOrderDetail() should return OrderDetails when basket size == 0 "(){
        given:
            def orderDetails = createOrderDetails()
            def user = createUser()
            def goods = createGoods()

        when:
            def result = goodsBasketService.getAllOrderDetails(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 }) >> []
            1 * orderRepository.save({it.status == 0 && it.user == user})
            1 * orderDetailsRepository.findByOrder({ it.id == null && it.status == 0 }) >> [orderDetails]
            1 * goodsRepository.findById({it == orderDetails.getId()}) >> Optional.of(goods)

        and:
            [orderDetails] == result
    }

    def "clear() should delete OrderDetails"() {
        given:
            def order = createOrder()
            def user = createUser()


        when:
            goodsBasketService.clear(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 }) >> [order]
            1 * orderRepository.delete({ it.id == order.getId() })

    }

    def "clear() should delete OrderDetails when basket size == 0"(){
        given:
            def user = createUser()

        when:
            goodsBasketService.clear(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, {it == 0}) >> []
            1 * orderRepository.save({it.status == 0 && it.user == user})
            1 * orderRepository.delete({it.id == null && it.status == 0})
    }


    def "setStatusToOne() should set status of basket"(){
        given:
            def order = createOrder()
            def user = createUser()
            def goods = createGoods()

        when:
            goodsBasketService.setStatusToOne(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, {it == 0}) >>  [order]
            1 * goodsRepository.findById({ it == order.getId() }) >> Optional.of(goods)
            1 * orderRepository.save({ it.id == order.getId() && it.status == 1 })


    }

    def "setStatusToOne() should set status of basket when basket size == 0"(){
        given:
            def user = createUser()

        when:
            goodsBasketService.setStatusToOne(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, {it == 0}) >>  []
            1 * orderRepository.save({it.status == 0 && it.user == user })
            1 * orderRepository.save({ it.id == null && it.status == 1 })


    }




    def "setStatusToOne() should throw NotAvailableGoodsException if orderDetails not available"(){
        given:
            def user = createUser()
            def order = createOrder()
            order.getOrderDetails()[0].setAvailable(false)
            order.getOrderDetails()[0].setCount(6)
            def msg = "Order contains inaccessible item."

        when:
            goodsBasketService.setStatusToOne(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 } ) >> [order]
            1 * goodsRepository.findById(1L) >> Optional.of(order.getOrderDetails()[0].getGoods())

        and:
            def result = thrown(NotAvailableGoodsException)
            msg == result.getMessage()

    }

    def "getBasket() should return order if order size > 0"() {
        given:
            def order = createOrder()
            def user = createUser()

        when:
            goodsBasketService.getBasket(user)

        then:
            orderRepository.findAllByUserAndStatus(user,{it == 0}) >> [order]
    }

    def "getBasket() should save order if order size == 0"(){
        given:
            def user = createUser()

        when:
            goodsBasketService.getBasket(user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, {it == 0}) >> []
            1 * orderRepository.save({ it.id == null && it.status == 0 })

    }


    def "createOrderDetailsInBasket() should save OrderDetails if exist" (){
        given:
            def user = createUser()
            def order = createOrder()
            def orderDetails = createOrderDetails()


        when:
            goodsBasketService.createOrderDetailsInBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 }) >> [order]
            1 * orderDetailsRepository.findByOrderAndGoodsAndCost(order, {it == orderDetails.getGoods()}, {it == orderDetails.getCost() }) >> orderDetails
            1 * orderDetailsRepository.save({it.count == 10}) >> orderDetails


    }

    def "createOrderDetailsInBasket() should create new OrderDetails"() {
        given:
            def user = createUser()
            def order = createOrder()
            def orderDetails = createOrderDetails()

        when:
            goodsBasketService.createOrderDetailsInBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, {it == 0}) >> [order]
            1 * orderDetailsRepository.findByOrderAndGoodsAndCost(order, {it == orderDetails.getGoods()}, {it == orderDetails.getCost()}) >> null
            1 * orderDetailsRepository.save({it.order == order})

    }

    def "updateCountOrderDetailsInBasket() delete orderDetails if count==0"(){
        given:
            def user = createUser()
            def orderDetails = createOrderDetails()
            orderDetails.setCount(0)

        when:
            goodsBasketService.updateCountOrderDetailsInBasket([orderDetails], user)

        then:
            1 * orderDetailsRepository.findById({it == orderDetails.getId()}) >> Optional.of(orderDetails)
            1 * orderDetailsRepository.delete(orderDetails)

    }

    def "updateCountOrderDetailsInBasket() should update if count > 0"(){
        given:
            def user = createUser()
            def orderDetails = createOrderDetails()
            def order = createOrder()
            orderDetails.setOrder(order)

        when:
            goodsBasketService.updateCountOrderDetailsInBasket([orderDetails], user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user,{it == 0}) >> [order]
            1 * orderDetailsRepository.findById({it == orderDetails.getId()}) >> Optional.of(orderDetails)
            1 * orderDetailsRepository.updateCount({it == orderDetails.getId()}, {it == orderDetails.getCount() } )
    }

    def "updateCountOrderDetailsInBasket() should throw NotFoundException"(){
        given:
            def user = createUser()
            def orderDetails = createOrderDetails()
            def order = createOrder()

        when:
            goodsBasketService.updateCountOrderDetailsInBasket([orderDetails], user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 }) >> [order]
            1 * orderDetailsRepository.findById({it == orderDetails.getId()}) >> Optional.empty()

        and:
            thrown(NotFoundException)
    }

    def "removeFromBasket() should remove orderDetails"() {
        given:
            def user = createUser()
            def orderDetails = createOrderDetails()
            def order = createOrder()
            orderDetails.setOrder(order)
        when:
            goodsBasketService.removeFromBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 }) >> [order]
            1 * orderDetailsRepository.findById({it == orderDetails.getId()}) >> Optional.of(orderDetails)
            1 * orderDetailsRepository.delete({ it == orderDetails })

    }

    def "removeFromBasket() should throw NotFoundException"() {
        given:
            def user = createUser()
            def orderDetails = createOrderDetails()
            def order = createOrder()

        when:
            goodsBasketService.removeFromBasket(orderDetails, user)

        then:
            1 * orderRepository.findAllByUserAndStatus(user, { it == 0 }) >> [order]
            1 * orderDetailsRepository.findById({it == orderDetails.getId()}) >> Optional.empty()

        and:
            thrown(NotFoundException)

    }





    static Goods createGoods(){
        return Goods.builder()
                .id(1L)
                .count(5)
                .build()
    }

    static OrderDetails createOrderDetails(){
        return OrderDetails.builder()
                .id(1L)
                .available(true)
                .goods(createGoods())
                .count(5)
                .build()
    }

    static Order createOrder(){
        Order.builder()
                .id(1L)
                .orderDetails([createOrderDetails()])
                .status(0)
                .build()
    }

    static User createUser(){
        return User.builder()
                .orders([createOrder(),createOrder()])
                .build()
    }
}
