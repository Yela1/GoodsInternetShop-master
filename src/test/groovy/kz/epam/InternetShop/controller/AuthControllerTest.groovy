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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
            def loginRequest = createLoginRequest()
            def requestJson = new ObjectMapper().writeValueAsString(loginRequest)
            def token = "token"
            def expectedJson = new ObjectMapper().writeValueAsString(new AuthResponse(token))
            def auth = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())

        when:
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(expectedJson))


        then:
            1 * authenticationManager.authenticate({ it.principal == loginRequest.getEmail()}) >> auth
            1 * tokenProvider.createToken({it.principal == loginRequest.getEmail()}) >> token
    }

    def "authenticateUser() should return 400 if loginRequest is not valid"(){
        given:
            def loginRequest = createLoginRequest()
            loginRequest.setEmail("notValidEmail")
            def requestJson = new ObjectMapper().writeValueAsString(loginRequest)

        expect:
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest())
    }


    def "registerUser() should create new User"(){
        given:
            def signUpRequest = createSignUpRequest()
            def requestJson = new ObjectMapper().writeValueAsString(signUpRequest)
            User user = Mock()
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
            1 * userService.findByUsername({ it == signUpRequest.getEmail() }) >> Optional.empty()
            1 * userService.save({ it.username == signUpRequest.getEmail() }) >> user
    }

    def "registerUser() should return 400 if signUpRequest is not valid"(){
        given:
            def signUpRequest = createSignUpRequest()
            signUpRequest.setEmail("NotValidEmail")
            def requestJson = new ObjectMapper().writeValueAsString(signUpRequest)

        expect:
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
    }

    def "registerUser() should throw BadCredentialsException() if user exist"(){
        given:
            def signUpRequest = createSignUpRequest()
            def json = new ObjectMapper().writeValueAsString(signUpRequest)
            def user = User.builder().id(1L).username("username").build()

        when:
            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isUnauthorized())

        then:
            1 * userService.findByUsername({ it == signUpRequest.getEmail() }) >> Optional.of(user)

        and:
            thrown(NestedServletException)

    }


    static LoginRequest createLoginRequest(){
        LoginRequest loginRequest = new LoginRequest()
        loginRequest.setEmail("email@gmail.com")
        loginRequest.setPassword("password@gmail.com")
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



}