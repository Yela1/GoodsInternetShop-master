package kz.epam.InternetShop.util

import kz.epam.InternetShop.model.GoodsCategory
import kz.epam.InternetShop.model.TO.GoodsCategoryTO
import kz.epam.InternetShop.model.TO.GoodsFiltersTO
import kz.epam.InternetShop.model.filter.AccessibleGoodsFilterImpl
import kz.epam.InternetShop.model.filter.DescriptionLikeGoodsFilterImpl
import kz.epam.InternetShop.model.filter.InRangeOfCostGoodsFilterImpl
import kz.epam.InternetShop.model.filter.NameLikeGoodsFilterImpl
import spock.lang.Specification
import static kz.epam.InternetShop.ObjectCreator.*

class TOUtilTest extends Specification{


    def "asTo() should convert OrderDetails to OrderDetailsTo"(){
        given:
            def order = createOrder()
            def goods = createGoods(5L,"goods")
            def orderDetails = createOrderDetails(order, goods)

        when:
            def result = TOUtil.asTO(orderDetails)

        then:
            order.getId() == result.getOrderId()
            goods.getName() == result.getGoodsName()
            orderDetails.isAvailable() == result.isAvailable()
    }

    def "createFrom() should return orderDetails" (){
        given:
            def orderDetailsTO = createOrderDetailsTo()

        when:
            def result = TOUtil.createFrom(orderDetailsTO)

        then:
            orderDetailsTO.getCount() == result.getCount()

    }

    def "createListFrom() should return orderDetails list"() {
        given:
            def orderDetailsTO = createOrderDetailsTo()

        when:
            def result = TOUtil.createListFrom([orderDetailsTO])

        then:
            orderDetailsTO.getCount() == result[0].getCount()
    }

    def "asTo() should return GoodsTo"(){
        given:
            def goods = createGoods()

        when:
            def result = TOUtil.asTO(goods)

        then:
            goods.getName() == result.getName()

    }

    def "createForm() should return goods"(){
        given:
            def goodsTO = createGoodsTo()

        when:
            def result = TOUtil.createFrom(goodsTO)

        then:
            goodsTO.getName() == result.getName()
    }

    def "asTo() should return GoodsCategoryTo"() {
        given:
            GoodsCategory goodsCategory = createGoodsCategory(1L, 'name')
            GoodsCategoryTO goodsCategoryTO = createGoodsCategoryTO(1L, "name")

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


