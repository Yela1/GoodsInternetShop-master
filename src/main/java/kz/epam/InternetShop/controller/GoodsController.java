package kz.epam.InternetShop.controller;

import kz.epam.InternetShop.model.Goods;
import kz.epam.InternetShop.model.GoodsCategory;
import kz.epam.InternetShop.model.TO.GoodsCategoryTO;
import kz.epam.InternetShop.model.TO.GoodsFiltersTO;
import kz.epam.InternetShop.model.TO.GoodsTO;
import kz.epam.InternetShop.model.filter.GoodsFilter;
import kz.epam.InternetShop.service.annotation.IsAdmin;
import kz.epam.InternetShop.service.interfaces.GoodsCategoryService;
import kz.epam.InternetShop.service.interfaces.GoodsService;
import kz.epam.InternetShop.util.TOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static kz.epam.InternetShop.util.TOUtil.*;

@RestController
@RequestMapping(value = "/goods", produces = MediaType.APPLICATION_JSON_VALUE)
public class GoodsController {

    private final GoodsCategoryService categoryService;

    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsCategoryService categoryService, GoodsService goodsService) {
        this.categoryService = categoryService;
        this.goodsService = goodsService;
    }

    @GetMapping("/categories")
    List<GoodsCategoryTO> getGoodsCategories() {
        return categoryService.getAll()
                .stream()
                .map(TOUtil::asTO)
                .collect(Collectors.toList());
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    List<GoodsTO> findAll(@Valid @RequestBody GoodsFiltersTO goodsFiltersTO) {
        return goodsService.findAll(asList(goodsFiltersTO))
                .stream()
                .map(TOUtil::asTO)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/categories/{categoryId}")
    List<GoodsTO> findAllByGoodsCategory(@PathVariable Long categoryId) {
        List<GoodsFilter> filters = Collections.emptyList();
        GoodsCategory goodsCategory = GoodsCategory.builder().id(categoryId).build();
        return goodsService.findAllByGoodsCategory(goodsCategory, filters)
                .stream()
                .map(TOUtil::asTO)
                .collect(Collectors.toList());
    }


    @PostMapping(value = "/categories/{categoryId}/filter", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<GoodsTO> findAllByGoodsCategory(@PathVariable Long categoryId,
                                         @Valid @RequestBody GoodsFiltersTO goodsFiltersTO) {
        GoodsCategory goodsCategory = GoodsCategory.builder().id(categoryId).build();
        return goodsService.findAllByGoodsCategory(goodsCategory, asList(goodsFiltersTO))
                .stream()
                .map(TOUtil::asTO)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{goodsId}")
    public GoodsTO get(@PathVariable("goodsId") long goodsId) {
        goodsService.get(goodsId);
        return asTO(goodsService.get(goodsId));
    }

    @IsAdmin
    @DeleteMapping(value = "/{goodsId}")
    public ResponseEntity delete(@PathVariable("goodsId") long goodsId) {
        goodsService.delete(Goods.builder().id(goodsId).build());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @IsAdmin
    @PutMapping(value = "/{goodsId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@Valid @RequestBody GoodsTO goodsTO, @PathVariable("goodsId") long goodsId) {
        Goods goods = createFrom(goodsTO);
        goods.setId(goodsId);
        goodsService.save(goods);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @IsAdmin
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GoodsTO create(@Valid @RequestBody GoodsTO goodsTO) {
        Goods goods = createFrom(goodsTO);
        goods.setId(null);
        return asTO(goodsService.save(goods));
    }




}
