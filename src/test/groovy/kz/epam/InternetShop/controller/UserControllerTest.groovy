package kz.epam.InternetShop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.epam.InternetShop.payload.UpdateRequest
import kz.epam.InternetShop.service.interfaces.UserService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static kz.epam.InternetShop.ObjectCreator.*

class UserControllerTest extends Specification{
    UserService userService = Mock()
    UserController userController = new UserController()

    MockMvc mockMvc = standaloneSetup(userController).build()

    def setup(){
        userController.setUserService(userService)
    }

    def "getAllUsers() should return all users"(){
        given:
            def user = createUser()
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
            def user = createUser()
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
            def user = createUser()

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
            UpdateRequest updateRequest = createUpdateRequest("newAddress","newUsername")
            def user = createUser("oldUsername", 1L,"password","oldAddress")
            def expectedUser = createUser("newUsername", 1L,"password","newAddress")
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
            1 * userService.save({ it.fullName == expectedUser.getFullName() && it.address == expectedUser.getAddress() }) >> expectedUser

    }

    def "updateUser() should return 400 if loginRequest is not valid"() {
        given:
            def request = "notValidRequest"
            def requestJson = new ObjectMapper().writeValueAsString(request)

        expect:
            mockMvc.perform(put("/user/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest())
    }

    def "getCurrentUser() should return user"(){
        given:
            def user = createUser()
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
