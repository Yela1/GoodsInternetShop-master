package kz.epam.InternetShop.service

import kz.epam.InternetShop.repository.GoodsCategoryRepository
import kz.epam.InternetShop.service.impl.GoodsCategoryServiceImpl
import kz.epam.InternetShop.service.interfaces.GoodsCategoryService
import spock.lang.Specification

import static kz.epam.InternetShop.ObjectCreator.*

class GoodsCategoryServiceTest extends Specification{

    GoodsCategoryRepository goodsCategoryRepository = Mock()
    GoodsCategoryService goodsCategoryService = new GoodsCategoryServiceImpl(goodsCategoryRepository)

    def "getAll() should return list of goodsCategory"(){
        given:
            def goodsCategory = createGoodsCategory(1L, "goodsCategoryOne")
            def goodsCategory1 = createGoodsCategory(2L, "goodsCategoryTwo")
            def list = [goodsCategory, goodsCategory1]

        when:
            def result = goodsCategoryService.getAll()

        then:
            1 * goodsCategoryRepository.findAll() >> list

        and:
            result == list

    }
}
