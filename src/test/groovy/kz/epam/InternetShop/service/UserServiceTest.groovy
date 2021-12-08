package kz.epam.InternetShop.service

import kz.epam.InternetShop.model.User
import kz.epam.InternetShop.repository.UserRepository
import kz.epam.InternetShop.service.impl.UserServiceImpl
import kz.epam.InternetShop.service.interfaces.UserService
import kz.epam.InternetShop.util.exception.NotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import static kz.epam.InternetShop.ObjectCreator.*

class UserServiceTest extends Specification{

    UserRepository userRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()


    UserService userService = new UserServiceImpl(userRepository, passwordEncoder)




    def "deleteAll() should delete all users"(){

        when:
            userService.deleteAll()

        then:
            1 * userRepository.deleteAll()
    }

    def "findById() should return user when its found"(){
        given:
            def user = createUser("Name", 1L,"Password", "Address")

        when:
            def result = userService.findById(1L)

        then:
            1 * userRepository.findById({ it == 1L }) >> Optional.of(user)

        and:
            user == result
    }

    def "findById() should throw NotFoundException if not exist"(){
        given:
            def msg = "User not found"

        when:
            userService.findById(1L)

        then:
            1 * userRepository.findById({ it == 1L  }) >> Optional.empty()

        and:
            def e = thrown(NotFoundException)
            msg == e.getMessage()

    }

    def "save() should save user to db"(){
        given:
            def user = createUser("Name", 1L,"Password", "Address")
            def expectedUser = createUser("Name", 1L,"PasswordEncoded", "Address")

        when:
            def result = userService.save(user)

        then:
            1 * passwordEncoder.encode({it == user.getPassword()}) >> "PasswordEncoded"
            1 * userRepository.save({ it.password == "PasswordEncoded" }) >> user

        and:
            expectedUser == result

    }

    def "delete() should delete user"(){
        given:
            def user = createUser("Name", 1L,"Password", "Address")

        when:
            userService.delete(user)

        then:
            1 * userRepository.delete({ it.username == user.getUsername() })

    }

    def "findByUsernameLike() should return list of users"(){
        given:
            def user = createUser("Name", 1L,"Password", "Address")

        when:
             userService.findByUsernameLike( "ame")

        then:
            1 * userRepository.findByUsernameLike( {it == "%ame%" }) >> [user]

    }

    def "findByAddressLike() should return list of users"(){
        given:
            def user = createUser("Name", 1L,"Password", "Kazakhstan")

        when:
           userService.findByAddressLike( "stan")

        then:
            1 * userRepository.findByAddressLike({it == "%stan%"}) >> [user]

    }

    def "findByFullNameLike() should return list of users"(){
        given:
            def user = createUser("Yelaman", 1L,"Password", "Kazakhstan")

        when:
           userService.findByFullNameLike("aman")

        then:
            1 * userRepository.findByFullNameLike({it == "%aman%"}) >> [user]

    }

    def "findAll() should return all users"(){
        given:
            def userOne = createUser("First", 1L,"Password", "Kazakhstan")
            def userTwo = createUser("Second", 1L,"Password", "Kazakhstan")
            def expected = [userOne, userTwo]

        when:
            def result = userService.findAll()

        then:
            1 * userRepository.findAll() >> expected

        and:
            expected == result
    }

    def "deleteByUsername() should delete user"(){
        given:
            def username = "username"

        when:
            userService.deleteByUsername(username)

        then:
            1 * userRepository.deleteByUsername( {it == username})

    }

    def "findByUsername() should return user"(){
        given:
            def username = "username"
            def user = createUser("Username", 1L, "password","address")

        when:
            def result = userService.findByUsername(username)

        then:
            1 * userRepository.findByUsername( {it == username}) >> Optional.of(user)

        and:
            Optional.of(user) == result
    }



}
