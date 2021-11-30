package kz.epam.InternetShop.service

import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.repository.UserRepository
import kz.epam.InternetShop.service.impl.UserServiceImpl
import kz.epam.InternetShop.service.interfaces.UserService
import kz.epam.InternetShop.util.exception.NotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification{

    UserRepository userRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()


    UserService userService = new UserServiceImpl(userRepository, passwordEncoder)


    User user
    User user1

    def setup(){

        user = User.builder().
                username("yelaman")
                .id(1L)
                .password("Password123")
                .build()

        user1 = User.builder().
                username("ayat")
                .id(2L)
                .password("Password123")
                .build()
    }



    def "deleteAll() should delete all users"(){

        when:
            userService.deleteAll()

        then:
            1 * userRepository.deleteAll()
    }

    def "findById() should return user when its found"(){
        when:
            def result = userService.findById(1L)

        then:
            1 * userRepository.findById(1L) >> Optional.of(user)

        and:
            user.getUsername() == result.getUsername()
    }

    def "findById() should throw NotFoundException if not exist"(){
        given:
            def msg = "User not found"

        when:
            userService.findById(1L)

        then:
            1 * userRepository.findById(1L) >> Optional.empty()

        and:
            def e = thrown(NotFoundException)
            msg == e.getMessage()

    }

    def "save() should save user to db"(){
        when:
            def result = userService.save(user)

        then:
            1 * passwordEncoder.encode("Password123") >> "passwordEncoded"
            1 * userRepository.save(user) >> user

        and:
            user.getUsername() == result.getUsername()
            result.getPassword() == "passwordEncoded"
    }

    def "delete() should delete user"(){

        when:
            userService.delete(user)

        then:
            1 * userRepository.delete(user)

    }

    def "findByUsernameLike() should return list of users"(){
        given:
            def list = [user, user1]

        when:
            def result = userService.findByUsernameLike( _ as String)

        then:
            1 * userRepository.findByUsernameLike( _ as String) >> list

        and:
            list == result
    }

    def "findByAddressLike() should return list of users"(){
        given:
            def list = [user, user1]

        when:
            def result = userService.findByAddressLike( _ as String)

        then:
            1 * userRepository.findByAddressLike( _ as String) >> list

        and:
            list == result
    }

    def "findByFullNameLike() should return list of users"(){
        given:
            def list = [user, user1]

        when:
            def result = userService.findByFullNameLike( _ as String)

        then:
            1 * userRepository.findByFullNameLike( _ as String) >> list

        and:
            list == result
    }

    def "findAll() should return all users"(){
        given:
            def list = [user,user1]

        when:
            def result = userService.findAll()

        then:
            1 * userRepository.findAll() >> list

        and:
            list == result
    }

    def "deleteByUsername() should delete user"(){
        when:
            userService.deleteByUsername( _ as String)

        then:
            1 * userRepository.deleteByUsername( _ as String)

    }

    def "findByUsername() should return user"(){
        when:
            def result = userService.findByUsername("yelaman")

        then:
            1 * userRepository.findByUsername( "yelaman") >> Optional.of(user)

        and:
            Optional.of(user) == result
    }


}
