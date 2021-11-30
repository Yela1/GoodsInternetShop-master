package kz.epam.InternetShop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.epam.InternetShop.model.Goods
import kz.epam.InternetShop.model.Order
import kz.epam.InternetShop.model.OrderDetails
import kz.epam.InternetShop.model.TO.OrderDetailsTO
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.service.interfaces.GoodsBasketService
import kz.epam.InternetShop.service.interfaces.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class GoodsBasketControllerTest extends Specification{
    GoodsBasketService goodsBasketService = Mock()
    UserService userService = Mock()
    GoodsBasketController goodsBasketController = new GoodsBasketController(goodsBasketService, userService)
    MockMvc mockMvc = standaloneSetup(goodsBasketController).build()

    def "getBasketGoods() should return list of OrderDetails"(){
        given:
            def user = new User()
            def expected =  new OrderDetails(1L, 1 ,2, new Order(), new Goods(), true)
            def json = new ObjectMapper().writeValueAsString([expected])

        when:
            mockMvc.perform(get("/goods/basket"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(json))

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.getAllOrderDetails(_) >> [expected]
    }

    def "clearBasket() should clear the basket"(){
        given:
            User user = new User()

        when:
            mockMvc.perform(get("/goods/basket/clear"))
                    .andExpect(status().isNoContent())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.clear(user)
    }

    def "placeOrder() should change status of basket"() {
        given:
            User user = new User()

        when:
            mockMvc.perform(get("/goods/basket/order")).andExpect(status().isAccepted())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.setStatusToOne(user)
    }

    def "createOrderDetailsInBasket() should create new OrderDetails"() {
        given:
            User user = new User()
            def orderDetailsTO = new OrderDetailsTO(1L, 1L, "goods_name",15,15,"photo",1L,true)
            def json = new ObjectMapper().writeValueAsString(orderDetailsTO)

        when:
            mockMvc.perform(post("/goods/toBasket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.createOrderDetailsInBasket(_, user)
    }

    def "updateCountOrderDetailsInBasket() should update basket"(){
        given:
            def user = new User()
            def orderDetailsTO = new OrderDetailsTO(1L, 1L, "goods_name",15,15,"photo",1L,true)
            def json = new ObjectMapper().writeValueAsString([orderDetailsTO])

        when:
            mockMvc.perform(put("/goods/basket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.updateCountOrderDetailsInBasket(_, user)
     }

    def "removeFromBasket() should delete orderDetails from basket"(){
        given:
            def user = new User()

        when:
            mockMvc.perform(delete("/goods/basket/{id}", 1L)).andExpect(status().isOk())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.removeFromBasket(_, user)
    }



}
