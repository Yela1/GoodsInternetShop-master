package kz.epam.InternetShop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.payload.UpdateRequest
import kz.epam.InternetShop.service.interfaces.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class UserControllerTest extends Specification{
    UserService userService = Mock()
    UserController userController = new UserController()

    MockMvc mockMvc = standaloneSetup(userController).build()

    def setup(){
        userController.setUserService(userService)
    }

    def "getAllUsers() should return all users"(){
        given:
            def user = User.builder()
                    .id(1L)
                    .username("yelaman")
                    .build()
            def json = new ObjectMapper().writeValueAsString([user])

        when:
            mockMvc.perform(get("/user/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(json))

        then:
            1 * userService.findAll() >>[user]
    }

    def "getUserById() should return specific user"(){
        given:
            def user = User.builder()
                    .id(1L)
                    .build()
            def json = new ObjectMapper().writeValueAsString(user)

        when:
            mockMvc.perform(get("/user/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(json))

        then:
            1 * userService.findById(1L) >> user
    }

    def "deleteUser() should delete user"(){
        given:
            def user = User.builder()
                    .id(1L)
                    .build()

        when:
            mockMvc.perform(delete("/user/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().string("User deleted successfully"))

        then:
            1 * userService.findById(1L) >> user
            1 * userService.delete(user)
    }

    def "updateUser() should update user"(){
        given:
            UpdateRequest updateRequest = new UpdateRequest()
            updateRequest.setAddress("address")
            updateRequest.setFullName("fullName")
            def user = User.builder().id(1L).username("ela").build()
            def expectedUser = User.builder().id(1L).username("ela").address("address").fullName("fullName").build()
            def json = new ObjectMapper().writeValueAsString(updateRequest)
            def expectedJson = new ObjectMapper().writeValueAsString(expectedUser)

        when:
            mockMvc.perform(put("/user/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedJson))

        then:
            1 * userService.findById(_) >> user
            1 * userService.save(_) >> expectedUser

    }

    def "getCurrentUser() should return user"(){
        given:
            def user = new User()
            def expectedJson =  new ObjectMapper().writeValueAsString(user)

        when:
            mockMvc.perform(get("/user/me"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedJson))

        then:
            1 * userService.findById(_) >> user
    }


}
