package kz.epam.InternetShop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.payload.ApiResponse
import kz.epam.InternetShop.payload.AuthResponse
import kz.epam.InternetShop.payload.LoginRequest
import kz.epam.InternetShop.payload.SignUpRequest
import kz.epam.InternetShop.security.TokenProvider
import kz.epam.InternetShop.service.interfaces.UserService
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.util.NestedServletException
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class AuthControllerTest extends Specification{
    AuthenticationManager authenticationManager = Mock()
    UserService userService = Mock()
    TokenProvider tokenProvider = Mock()
    AuthController authController = new AuthController(authenticationManager, userService, tokenProvider)
    MockMvc mockMvc = standaloneSetup(authController).build()

    def "authenticateUser() should return token"(){
        given:
            def loginRequest = new LoginRequest()
            loginRequest.setEmail("email@gmail.com")
            loginRequest.setPassword("password")
            def requestJson = new ObjectMapper().writeValueAsString(loginRequest)

            def token = "TOKEN"
            def expectedJson = new ObjectMapper().writeValueAsString(new AuthResponse(token))

        when:
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedJson))


        then:
            1 * authenticationManager.authenticate(_) >> null
            1 * tokenProvider.createToken(_) >> token
    }

    def "authenticateUser() should return 400 if loginRequest is not valid"(){
        given:
            def loginRequest = new LoginRequest()
            loginRequest.setEmail("notValidEmail")
            loginRequest.setPassword("password")
            def requestJson = new ObjectMapper().writeValueAsString(loginRequest)

        expect:
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest())
    }


    def "registerUser() should create new User"(){
        given:
            def sign = new SignUpRequest()
            sign.setPassword("password")
            sign.setEmail("elaman@gmail.com")
            sign.setAddress("address")
            sign.setName("name")
            def requestJson = new ObjectMapper().writeValueAsString(sign)
            def user = new User()
            def apiResponse = new ApiResponse(true, "User registered successfully@")
            def expectedJson = new ObjectMapper().writeValueAsString(apiResponse)

        when:
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedJson))

        then:
            1 * userService.findByUsername(sign.getEmail()) >> Optional.empty()
            1 * userService.save(_) >> user
    }

    def "registerUser() should return 400 if signUpRequest is not valid"(){
        given:
            def sign = new SignUpRequest()
            sign.setPassword("password")
            sign.setEmail("notValidEmail")
            sign.setAddress("address")
            sign.setName("name")
            def requestJson = new ObjectMapper().writeValueAsString(sign)

        expect:
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
    }

    def "registerUser() should throw BadCredentialsException() if user exist"(){
        given:
            def sign = new SignUpRequest()
            sign.setPassword("password")
            sign.setEmail("elaman@gmail.com")
            sign.setAddress("address")
            sign.setName("name")
            def json = new ObjectMapper().writeValueAsString(sign)
            def msg = "Username is already exists"
            User user = User.builder().id(1L).username("username").build()

        when:
            def result = mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isUnauthorized())
//                    .andExpect({ result -> result.getResolvedException() instanceof BadCredentialsException })
//                    .andExpect({ result -> result.getResolvedException().getMessage() == msg })
                    .andReturn().getResolvedException()

        then:
            1 * userService.findByUsername(_) >> Optional.of(user)

        and:
            thrown(NestedServletException)
//            thrown(BadCredentialsException)
//            result instanceof BadCredentialsException
//            msg == result.getResolvedException().getMessage()


    }

}
