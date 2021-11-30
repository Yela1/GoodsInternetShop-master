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



class GoodServiceTest extends Specification{

    GoodsRepository goodsRepository = Mock()

    GoodsService goodsService = new GoodsServiceImpl(goodsRepository)

    Goods goods
    Goods goods1
    GoodsFilter goodsFilter


    def setup() {
        goods = Goods.builder()
                .id(1L)
                .name("Good_one")
                .build()
        goods1 = Goods.builder()
                .id(2L)
                .name("Good_two")
                .build()

        goodsFilter = new NameLikeGoodsFilterImpl(true, "two")
    }

    def "findAll() should return all goods"(){
        given:
            def list = [goods, goods1]

        when:
            def result = goodsService.findAll([goodsFilter])

        then:
            1 * goodsRepository.findAll() >> list

        and:
            list != result
            list[1] == result[0]
    }

    def "findAllByGoodsCategory() should return all goods by category"(){
        given:
            def list = [goods, goods1]
            def goodsCategory = GoodsCategory.builder().name("Category").build()

        when:
            def result = goodsService.findAllByGoodsCategory(goodsCategory, [])

        then:
            1 * goodsRepository.findAllByGoodsCategory(goodsCategory) >> list

        and:
            list == result

    }

    def "save() should save goods to db"(){
        when:
            def result = goodsService.save(goods)

        then:
            1 * goodsRepository.existsById(1L) >> true
            1 * goodsRepository.save(goods) >> goods

        and:
            goods == result

    }

    def "save() should throw notFoundException when goods does not exist"(){
        when:
            goodsService.save(goods)

        then:
            1 * goodsRepository.existsById(1L) >> false

        and:
            thrown(NotFoundException)
    }

    def "delete() should delete goods if found"(){
        when:
            goodsService.delete(goods)

        then:
            1 * goodsRepository.existsById(1L) >> true
            1 * goodsRepository.delete(goods)
    }

    def "delete() should throw NotFoundException if goods does not exist"(){
        when:
            goodsService.delete(goods)

        then:
            1 * goodsRepository.existsById(1L) >> false

        and:
            thrown(NotFoundException)

    }

    def "get() should return goods if exist"(){
        when:
            def result = goodsService.get(1L)

        then:
            1 * goodsRepository.findById(1L) >> Optional.of(goods)

        and:
            goods == result
    }

    def "get() should throw NotFoundException if goods does not exist"(){
        when:
            goodsService.get(1L)

        then:
            1 * goodsRepository.findById(1L) >> Optional.empty()

        and:
            thrown(NotFoundException)
    }

}
