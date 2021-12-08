package kz.epam.InternetShop.service

import kz.epam.InternetShop.model.Goods
import kz.epam.InternetShop.model.GoodsCategory
import kz.epam.InternetShop.model.filter.GoodsFilter
import kz.epam.InternetShop.model.filter.NameLikeGoodsFilterImpl
import kz.epam.InternetShop.repository.GoodsRepository
import kz.epam.InternetShop.service.impl.GoodsServiceImpl
import kz.epam.InternetShop.service.interfaces.GoodsService
import kz.epam.InternetShop.util.exception.NotFoundException
import spock.lang.Specification


import static kz.epam.InternetShop.ObjectCreator.*

class GoodServiceTest extends Specification{

    GoodsRepository goodsRepository = Mock()

    GoodsService goodsService = new GoodsServiceImpl(goodsRepository)


    def "findAll() should return all goods"(){
        given:
            def goodsOne = createGoods(1L, "first")
            def goodsTwo = createGoods(2L, "second")
            def goodsFilter = createGoodsFilter(true, "rst")
            def list = [goodsOne, goodsTwo]
            def expected = [goodsOne]

        when:
            def result = goodsService.findAll([goodsFilter])

        then:
            1 * goodsRepository.findAll() >> list

        and:
            expected == result
    }

    def "findAllByGoodsCategory() should return all goods by category"(){
        given:
            def goodsOne = createGoods(1L, "Mercedes")
            def goodsTwo = createGoods(2L, "BMW")
            def goodsFilter = createGoodsFilter(true, "cedes")
            def goodsCategory = createGoodsCategory("cars")
            def expected = [goodsOne]

        when:
            def result = goodsService.findAllByGoodsCategory(goodsCategory, [goodsFilter])

        then:
            1 * goodsRepository.findAllByGoodsCategory(goodsCategory) >> [goodsOne, goodsTwo]

        and:
            expected == result

    }

    def "save() should save goods to db"(){
        given:
            def goods = createGoods(1L, "GOODS")

        when:
            def result = goodsService.save(goods)

        then:
            1 * goodsRepository.existsById({ it == goods.getId() }) >> true
            1 * goodsRepository.save({ it.id == goods.getId() && it.name == goods.getName() }) >> goods

        and:
            goods == result

    }

    def "save() should throw notFoundException when goods does not exist"(){
        given:
            def goods = createGoods(1L, "GOODS")
            def msg = "Item not found"

        when:
            goodsService.save(goods)

        then:
            1 * goodsRepository.existsById({it == goods.getId() }) >> false

        and:
            def er = thrown(NotFoundException)
            msg == er.getMessage()
    }

    def "delete() should delete goods if found"(){
        given:
            def goods = createGoods(1L, "GOODS")

        when:
            goodsService.delete(goods)

        then:
            1 * goodsRepository.existsById({ it == goods.getId() }) >> true
            1 * goodsRepository.delete({ it.id == goods.getId() && it.name == goods.getName() })
    }

    def "delete() should throw NotFoundException if goods does not exist"(){
        given:
            def goods = createGoods(1L, "GOODS")
            def msg = "Item not found"

        when:
            goodsService.delete(goods)

        then:
            1 * goodsRepository.existsById({it == goods.getId()}) >> false

        and:
            def er = thrown(NotFoundException)
            msg == er.getMessage()

    }

    def "get() should return goods if exist"(){
        given:
            def goods = createGoods(1L, "GOODS")

        when:
            def result = goodsService.get(1L)

        then:
            1 * goodsRepository.findById({ it == 1L }) >> Optional.of(goods)

        and:
            goods == result
    }

    def "get() should throw NotFoundException if goods does not exist"(){
        given:
            def msg = "Item not found"

        when:
            goodsService.get(1L)

        then:
            1 * goodsRepository.findById({ it == 1L }) >> Optional.empty()

        and:
            def er = thrown(NotFoundException)
            msg == er.getMessage()
    }




}
