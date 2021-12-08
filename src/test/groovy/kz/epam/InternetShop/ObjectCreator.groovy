package kz.epam.InternetShop

import kz.epam.InternetShop.model.Goods
import kz.epam.InternetShop.model.GoodsCategory
import kz.epam.InternetShop.model.Order
import kz.epam.InternetShop.model.OrderDetails
import kz.epam.InternetShop.model.Role
import kz.epam.InternetShop.model.TO.GoodsCategoryTO
import kz.epam.InternetShop.model.TO.GoodsTO
import kz.epam.InternetShop.model.TO.OrderDetailsTO
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.model.filter.GoodsFilter
import kz.epam.InternetShop.model.filter.NameLikeGoodsFilterImpl
import kz.epam.InternetShop.payload.ApiResponse
import kz.epam.InternetShop.payload.LoginRequest
import kz.epam.InternetShop.payload.SignUpRequest
import kz.epam.InternetShop.payload.UpdateRequest
import org.springframework.security.core.GrantedAuthority

class ObjectCreator {

    static Goods createGoods(){
        return Goods.builder()
                .id(1L)
                .name("name")
                .cost(1)
                .count(5)
                .description("description")
                .photos(["photos"])
                .build()
    }

    static GoodsTO createGoodsTo(){
        return GoodsTO.builder()
                .id(1L)
                .name("name")
                .cost(1)
                .count(1)
                .description("description")
                .photos(["photos"])
                .build()
    }

    static Goods createGoods(Long id, String name){
        return Goods.builder()
                .id(id)
                .name(name)
                .build()
    }

    static OrderDetails createOrderDetails(Order order){
        return OrderDetails.builder()
                .id(1L)
                .available(true)
                .goods(createGoods())
                .count(5)
                .order(order)
                .build()
    }

    static OrderDetails createOrderDetails(Order order, Goods goods){
        return OrderDetails.builder()
                .id(1L)
                .available(true)
                .goods(goods)
                .count(5)
                .order(order)
                .build()
    }

    static OrderDetailsTO createOrderDetailsTo(){
        return OrderDetailsTO.builder()
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
    static OrderDetails createOrderDetails(){
        return OrderDetails.builder()
                .id(1L)
                .available(true)
                .goods(createGoods())
                .count(5)
                .build()
    }

    static Order createOrder(){
        return Order.builder()
                .id(1L)
                .orderDetails([createOrderDetails()])
                .status(0)
                .build()
    }

    static Order createOrder(Boolean available, Integer count){
        def orderDetails = createOrderDetails()
        orderDetails.setAvailable(available)
        orderDetails.setCount(count)
        return Order.builder()
                .id(1L)
                .orderDetails([orderDetails])
                .status(0)
                .build()
    }

    static User createUser(){
        return User.builder()
                .orders([createOrder(),createOrder()])
                .build()
    }

    static User createUser(Long id){
        return User.builder()
                .id(id)
                .build()
    }

    static User createUser(Long id, Set<GrantedAuthority> authority){
        return User.builder().id(id).authority(authority).build()
    }

    static User createUser(String name, Long id, String password, String address){
        return User.builder()
                .fullName(name)
                .username(name)
                .id(id)
                .password(password)
                .address(address)
                .build()
    }

    static User createUser(Integer enabled){
        return User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .fullName("fullName")
                .address("address")
                .enabled(enabled)
                .authority(Collections.singleton(Role.ROLE_USER)).build()
    }

    static GoodsCategory createGoodsCategory(Long id, String name){
        return GoodsCategory.builder()
                .id(id)
                .name(name)
                .build()
    }
    static GoodsCategoryTO createGoodsCategoryTO(Long id, String name){
        return GoodsCategoryTO.builder()
                .id(id)
                .name(name)
                .build()
    }


    static GoodsFilter createGoodsFilter(Boolean status, String nameLike){
        return new NameLikeGoodsFilterImpl(status, nameLike)
    }

    static GoodsCategory createGoodsCategory(String categoryName) {
        return GoodsCategory.builder().name(categoryName).build()
    }
    static LoginRequest createLoginRequest(){
        LoginRequest loginRequest = new LoginRequest()
        loginRequest.setEmail("email@gmail.com")
        loginRequest.setPassword("password")
        return loginRequest
    }

    static LoginRequest createLoginRequest(String email, String password){
        LoginRequest loginRequest = new LoginRequest()
        loginRequest.setEmail(email)
        loginRequest.setPassword(password)
        return loginRequest
    }

    static SignUpRequest createSignUpRequest(){
        SignUpRequest sign = new SignUpRequest()
        sign.setEmail("email@gmail.com")
        sign.setPassword("password")
        sign.setName("name")
        sign.setAddress("address")
        return sign
    }

    static SignUpRequest createSignUpRequest(String email){
        SignUpRequest sign = new SignUpRequest()
        sign.setEmail(email)
        sign.setPassword("password")
        sign.setName("name")
        sign.setAddress("address")
        return sign
    }

    static ApiResponse createApiResponse(Boolean status, String msg){
        return new ApiResponse(status, msg)
    }


    static UpdateRequest createUpdateRequest(String address, String fullName){
        UpdateRequest updateRequest = new UpdateRequest()
        updateRequest.setAddress(address)
        updateRequest.setFullName(fullName)
        return updateRequest
    }

}
