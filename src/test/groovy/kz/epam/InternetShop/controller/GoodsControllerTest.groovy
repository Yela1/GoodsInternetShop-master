package kz.epam.InternetShop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.epam.InternetShop.model.Goods
import kz.epam.InternetShop.model.GoodsCategory
import kz.epam.InternetShop.model.TO.GoodsCategoryTO
import kz.epam.InternetShop.model.TO.GoodsFiltersTO
import kz.epam.InternetShop.service.interfaces.GoodsCategoryService
import kz.epam.InternetShop.service.interfaces.GoodsService
import kz.epam.InternetShop.util.TOUtil
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static kz.epam.InternetShop.util.TOUtil.*
import java.util.stream.Collectors

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup



class GoodsControllerTest extends Specification{
    GoodsCategoryService goodsCategoryService = Mock()
    GoodsService goodsService = Mock()
    GoodsController goodsController = new GoodsController(goodsCategoryService, goodsService)
    MockMvc mockMvc = standaloneSetup(goodsController).build()

    def "getCategories() should return all categories"() {
        given:
            def goodsCategory = GoodsCategory.builder()
                    .id(15L)
                    .name("new")
                    .build()
            def goodsCategoryTO = new GoodsCategoryTO(goodsCategory.getId(),goodsCategory.getName())
            def expected = new ObjectMapper().writeValueAsString([goodsCategoryTO])

        when:
            mockMvc.perform(get("/goods/categories"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expected))
        then:
            1 * goodsCategoryService.getAll() >> [goodsCategory]
    }


    def "findAll() should return all goods"(){
        given:
            def goodsFiltersTo = new GoodsFiltersTO()
            def goods = Goods.builder().id(1L).name("goods").build()
            def goodsToList = [goods].stream().map(TOUtil.&asTO).collect(Collectors.toList())
            def expected = new ObjectMapper().writeValueAsString(goodsToList)
            def request = new ObjectMapper().writeValueAsString(goodsFiltersTo)

        when:
            mockMvc.perform(get("/goods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected))

        then:
            1 * goodsService.findAll(_) >> [goods]

    }

    def "findAll() should return 400 if request is not valid"(){
        given:
            def goodsFiltersTo = "InvalidRequest"
            def request = new ObjectMapper().writeValueAsString(goodsFiltersTo)

        expect:
            mockMvc.perform(get("/goods")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())


    }

    def "findAllByGoodsCategory() should return goods with specific category"(){
        given:
            def goods = Goods.builder().id(1L).name("goods").build()
            def goodsToList = [goods].stream().map(TOUtil.&asTO).collect(Collectors.toList())
            def expected = new ObjectMapper().writeValueAsString(goodsToList)

        when:
            mockMvc.perform(get("/goods/categories/{categoryId}",1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expected))

        then:
            1 * goodsService.findAllByGoodsCategory({ it.id == 1L} , {it instanceof Collection}) >> [goods]
    }

    def "findAllByGoodsCategory() should return goods with specific category and with filter"() {
        given:
            def goodsFiltersTo = new GoodsFiltersTO()
            def goods = Goods.builder().id(1L).name("goods").build()
            def goodsToList = [goods].stream().map(TOUtil.&asTO).collect(Collectors.toList())
            def expected = new ObjectMapper().writeValueAsString(goodsToList)
            def request = new ObjectMapper().writeValueAsString(goodsFiltersTo)

        when:
            mockMvc.perform(post("/goods/categories/{categoryId}/filter", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expected))

        then:
            1 * goodsService.findAllByGoodsCategory({it.id == 1L}, asList(goodsFiltersTo)) >> [goods]

    }
    def "findAllByGoodsCategory() should return 400 if request is not valid"() {
        given:
            def goodsFiltersTo = "InvalidRequest"
            def request = new ObjectMapper().writeValueAsString(goodsFiltersTo)

        expect:
            mockMvc.perform(post("/goods/categories/{categoryId}/filter", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())

    }

    def "get() should return goods with id"(){
        given:
            def goods = Goods.builder().id(1L).name("goods").build()
            def expected = new ObjectMapper().writeValueAsString(asTO(goods))

        when:
            mockMvc.perform(get("/goods/{goodsId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expected))

        then:
            2 * goodsService.get(_) >> goods
    }

    def "delete() should delete goods"(){
        when:
            mockMvc.perform(delete("/goods/{goodsId}", 1L))
                    .andExpect(status().isNoContent())

        then:
            1 * goodsService.delete(_)
    }

    def "update() should update goods"() {
        given:
            def goods = Goods.builder().id(1L).name("goods").build()
            def goodsTo = asTO(goods)
            def request = new ObjectMapper().writeValueAsString(goodsTo)

        when:
            mockMvc.perform(put("/goods/{goodsIs}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isNoContent())

        then:
            1 *  goodsService.save({it.id == 1L && it.name == goods.getName()})
    }
    def "update() should return 400 if request is not valid"() {
        given:
            def request = new ObjectMapper().writeValueAsString("InvalidRequest")

        expect:
            mockMvc.perform(put("/goods/{goodsIs}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
    }

    def "create() should create room"(){
        given:
            def goods = Goods.builder().id(1L).name("goods").build()
            def goodsTo = asTO(goods)
            def request = new ObjectMapper().writeValueAsString(goodsTo)
            def expected = new ObjectMapper().writeValueAsString(goodsTo)

        when:
            mockMvc.perform(post("/goods")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expected))

        then:
            1 * goodsService.save({it.id == null && it.name == goods.getName()}) >> goods
    }
    def "create() should return 400 if request is not valid"(){
        given:
            def request = new ObjectMapper().writeValueAsString("InvalidRequest")

        expect:
             mockMvc.perform(post("/goods")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
    }
}
