package kz.epam.InternetShop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.epam.InternetShop.service.interfaces.GoodsBasketService
import kz.epam.InternetShop.service.interfaces.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static kz.epam.InternetShop.ObjectCreator.*

class GoodsBasketControllerTest extends Specification{
    GoodsBasketService goodsBasketService = Mock()
    UserService userService = Mock()
    GoodsBasketController goodsBasketController = new GoodsBasketController(goodsBasketService, userService)
    MockMvc mockMvc = standaloneSetup(goodsBasketController).build()


    def "getBasketGoods() should return list of OrderDetails"(){
        given:
            def user = createUser()
            def expected =  createOrderDetails()
            def json = new ObjectMapper().writeValueAsString([expected])

        when:
            mockMvc.perform(get("/goods/basket"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(json))

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.getAllOrderDetails({it.id == user.getId()}) >> [expected]
    }

    def "clearBasket() should clear the basket"(){
        given:
            def user = createUser()

        when:
            mockMvc.perform(get("/goods/basket/clear"))
                    .andExpect(status().isNoContent())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.clear({ it.id == user.getId() })
    }

    def "placeOrder() should change status of basket"() {
        given:
            def user = createUser()

        when:
            mockMvc.perform(get("/goods/basket/order")).andExpect(status().isAccepted())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.setStatusToOne({ it.id == user.getId() })
    }

    def "createOrderDetailsInBasket() should create new OrderDetails"() {
        given:
            def user = createUser()
            def orderDetailsTO = createOrderDetailsTo()
            def requestJson = new ObjectMapper().writeValueAsString(orderDetailsTO)

        when:
            mockMvc.perform(post("/goods/toBasket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isCreated())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.createOrderDetailsInBasket({ it.id == orderDetailsTO.getId() && it.cost == orderDetailsTO.getCost()}, user)
    }

    def "createOrderDetailsInBasket() should return 400 if request not valid"() {
        given:

            def orderDetailsTO = null
            def requestJson = new ObjectMapper().writeValueAsString(orderDetailsTO)

        expect:
            mockMvc.perform(post("/goods/toBasket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest())

    }

    def "updateCountOrderDetailsInBasket() should update basket"(){
        given:
            def user = createUser()
            def orderDetailsTO = createOrderDetailsTo()
            def json = new ObjectMapper().writeValueAsString([orderDetailsTO])

        when:
            mockMvc.perform(put("/goods/basket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.updateCountOrderDetailsInBasket({it[0].cost == orderDetailsTO.getCost()}, user)
     }

    def "updateCountOrderDetailsInBasket() should return 400 if request not valid"(){
        given:
            def orderDetailsTO = "invalidRequest"
            def json = new ObjectMapper().writeValueAsString([orderDetailsTO])

        expect:
            mockMvc.perform(put("/goods/basket")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest())

    }

    def "removeFromBasket() should delete orderDetails from basket"(){
        given:
            def id = 1L
            def user = createUser()

        when:
            mockMvc.perform(delete("/goods/basket/{id}", id)).andExpect(status().isOk())

        then:
            1 * userService.findById(_) >> user
            1 * goodsBasketService.removeFromBasket({id == 1L}, user)
    }



}
