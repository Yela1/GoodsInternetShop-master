package kz.epam.InternetShop.security

import kz.epam.InternetShop.model.Role
import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.repository.UserRepository
import kz.epam.InternetShop.util.exception.NotFoundException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

class CustomUserDetailServiceTest extends Specification{

    UserRepository userRepository = Mock()
    CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService()

    def setup(){
        customUserDetailsService.setUserRepository(userRepository)
    }

    def "loadUserByUsername() should return UserDetails"(){
        given:
            def user = User.builder()
                    .id(1L)
                    .username("user")
                    .password("pass")
                    .fullName("name")
                    .address("address")
                    .enabled(1)
                    .authority(Collections.singleton(Role.ROLE_USER)).build()

        when:
            def result = customUserDetailsService.loadUserByUsername(_ as String)

        then:
            1 * userRepository.findByUsername(_) >> Optional.of(user)

        and:
            user.getUsername() == result.getUsername()
    }

    def "loadUserByUsername should throw UsernameNotFoundException if user does not exist"(){
        given:
            def msg = "Username not found"

        when:
            customUserDetailsService.loadUserByUsername(_ as String)

        then:
            1 * userRepository.findByUsername(_) >> Optional.empty()

        and:
            def e = thrown(UsernameNotFoundException)
            msg == e.getMessage()
    }

    def "loadUserById should return UserDetails"(){
        given:
            def user = User.builder()
                    .id(1L)
                    .username("user")
                    .password("pass")
                    .fullName("name")
                    .address("address")
                    .enabled(1)
                    .authority(Collections.singleton(Role.ROLE_USER)).build()

        when:
            def result = customUserDetailsService.loadUserById(1L)

        then:
            1 * userRepository.findById(1L) >> Optional.of(user)

        and:
            user.getUsername() == result.getUsername()
    }

    def "loadUserById should throw NotFoundException if not found"(){
        given:
            def msg = "Can't find user by ID"

        when:
            customUserDetailsService.loadUserById(1L)

        then:
            1 * userRepository.findById(1L) >> Optional.empty()

        and:
            def e = thrown(NotFoundException)
            msg == e.getMessage()
    }

}

