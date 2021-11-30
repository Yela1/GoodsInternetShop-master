package kz.epam.InternetShop.service

import kz.epam.InternetShop.model.GoodsCategory
import kz.epam.InternetShop.repository.GoodsCategoryRepository
import kz.epam.InternetShop.service.impl.GoodsCategoryServiceImpl
import kz.epam.InternetShop.service.interfaces.GoodsCategoryService
import spock.lang.Specification

class GoodsCategoryServiceTest extends Specification{

    GoodsCategoryRepository goodsCategoryRepository = Mock()
    GoodsCategoryService goodsCategoryService = new GoodsCategoryServiceImpl(goodsCategoryRepository)

    def "getAll() should return list of goodsCategory"(){

        given:
            GoodsCategory goodsCategory = GoodsCategory.builder()
                    .id(1L)
                    .name("goodsCategoryOne")
                    .build()
            GoodsCategory goodsCategory1 = GoodsCategory.builder()
                    .id(2L)
                    .name("goodsCategoryTwo")
                    .build()
            def list = [goodsCategory, goodsCategory1]

        when:
            def result = goodsCategoryService.getAll()

        then:
            1 * goodsCategoryRepository.findAll() >> list

        and:
            result == list

    }
}
