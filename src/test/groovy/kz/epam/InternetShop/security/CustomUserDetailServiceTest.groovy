package kz.epam.InternetShop.security

import kz.epam.InternetShop.repository.UserRepository
import kz.epam.InternetShop.util.exception.NotFoundException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification


import static kz.epam.InternetShop.ObjectCreator.*

class CustomUserDetailServiceTest extends Specification{

    UserRepository userRepository = Mock()
    CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService()

    def setup(){
        customUserDetailsService.setUserRepository(userRepository)
    }

    def "loadUserByUsername() should return UserDetails"(){
        given:
            def enabled = 1
            def user = createUser(enabled)

        when:
            def result = customUserDetailsService.loadUserByUsername("username")

        then:
            1 * userRepository.findByUsername({it == "username"}) >> Optional.of(user)

        and:
            user.getUsername() == result.getUsername()
    }

    def "loadUserByUsername should throw UsernameNotFoundException if user does not exist"(){
        given:
            def msg = "Username not found"

        when:
            customUserDetailsService.loadUserByUsername("username")

        then:
            1 * userRepository.findByUsername("username") >> Optional.empty()

        and:
            def e = thrown(UsernameNotFoundException)
            msg == e.getMessage()
    }

    def "loadUserById should return UserDetails"(){
        given:
            def enabled = 1
            def user = createUser(enabled)

        when:
            def result = customUserDetailsService.loadUserById(1L)

        then:
            1 * userRepository.findById({ it == 1L }) >> Optional.of(user)

        and:
            user.getUsername() == result.getUsername()
    }

    def "loadUserById should throw NotFoundException if not found"(){
        given:
            def msg = "Can't find user by ID"

        when:
            customUserDetailsService.loadUserById(1L)

        then:
            1 * userRepository.findById({ it == 1L }) >> Optional.empty()

        and:
            def e = thrown(NotFoundException)
            msg == e.getMessage()
    }

}

