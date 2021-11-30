package kz.epam.InternetShop.util

import kz.epam.InternetShop.model.Goods
import kz.epam.InternetShop.model.GoodsCategory
import kz.epam.InternetShop.model.Order
import kz.epam.InternetShop.model.OrderDetails
import kz.epam.InternetShop.model.TO.GoodsCategoryTO
import kz.epam.InternetShop.model.TO.GoodsFiltersTO
import kz.epam.InternetShop.model.TO.GoodsTO
import kz.epam.InternetShop.model.TO.OrderDetailsTO
import kz.epam.InternetShop.model.filter.AccessibleGoodsFilterImpl
import kz.epam.InternetShop.model.filter.DescriptionLikeGoodsFilterImpl
import kz.epam.InternetShop.model.filter.InRangeOfCostGoodsFilterImpl
import kz.epam.InternetShop.model.filter.NameLikeGoodsFilterImpl
import spock.lang.Specification

class TOUtilTest extends Specification{

    Goods goods
    GoodsTO goodsTO
    Order order
    OrderDetails orderDetails
    OrderDetailsTO orderDetailsTO

    def setup(){

        goods = Goods.builder()
                .id(1L)
                .name("name")
                .cost(1)
                .count(1)
                .description("description")
                .photos(["photos"])
                .build()

        goodsTO = GoodsTO.builder()
                .id(1L)
                .name("name")
                .cost(1)
                .count(1)
                .description("description")
                .photos(["photos"])
                .build()

        order = Order.builder().id(1L).build()

        orderDetails = OrderDetails.builder()
                .id(1L)
                .count(1)
                .count(1)
                .available(true)
                .goods(goods)
                .order(order)
                .build()

        orderDetailsTO = OrderDetailsTO.builder()
                .id(1L)
                .count(1)
                .count(1)
                .available(true)
                .goodsId(1)
                .goodsName("name")
                .goodsPhoto("photo")
                .orderId(1)
                .build()
    }

    def "asTo() should convert OrderDetails to OrderDetailsTo"(){

        when:
            def result = TOUtil.asTO(orderDetails)

        then:
            order.getId() == result.getOrderId()
            goods.getName() == result.getGoodsName()
            orderDetails.isAvailable() == result.isAvailable()
    }

    def "createFrom() should return orderDetails" (){
        when:
            def result = TOUtil.createFrom(orderDetailsTO)

        then:
            orderDetails == result

    }

    def "createListFrom() should return orderDetails list"() {
        given:
            def expected = [orderDetails]

        when:
            def result = TOUtil.createListFrom([orderDetailsTO])

        then:
            expected == result
    }

    def "asTo() should return GoodsTo"(){
        when:
            def result = TOUtil.asTO(goods)

        then:
            goodsTO == result

    }

    def "createForm() should return goods"(){
        when:
            def result = TOUtil.createFrom(goodsTO)

        then:
            goods == result
    }

    def "asTo() should return GoodsCategoryTo"() {
        given:
            GoodsCategory goodsCategory = GoodsCategory.builder()
                    .id(1L)
                    .name("name")
                    .build()
            GoodsCategoryTO goodsCategoryTO = GoodsCategoryTO.builder()
                    .id(1L)
                    .name("name")
                    .build()

        when:
            def result = TOUtil.asTO(goodsCategory)

        then:
            goodsCategoryTO == result

    }

    def "asList() should return list of GoodsFilter"(){
        given:
            def accessibleGoodsFilter = new AccessibleGoodsFilterImpl(true)
            def descriptionLikeFilter = new DescriptionLikeGoodsFilterImpl(true, "like")
            def inRangeOfCostFilter = new InRangeOfCostGoodsFilterImpl(true,0,15)
            def nameLikeGoodsFilter = new NameLikeGoodsFilterImpl(true, "name")
            def expected = [accessibleGoodsFilter, inRangeOfCostFilter, nameLikeGoodsFilter, descriptionLikeFilter,]
            GoodsFiltersTO goodsFiltersTO = GoodsFiltersTO.builder()
                        .accessibleGoodsFilter(accessibleGoodsFilter)
                        .descriptionLikeFilter(descriptionLikeFilter)
                        .inRangeOfCostGoodsFilter(inRangeOfCostFilter)
                        .nameLikeFilter(nameLikeGoodsFilter).build()

        when:
            def result = TOUtil.asList(goodsFiltersTO)

        then:
            expected == result

    }

}


