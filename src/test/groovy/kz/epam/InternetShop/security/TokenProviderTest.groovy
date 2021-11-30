package kz.epam.InternetShop.security

import kz.epam.InternetShop.configuration.AppProperties
import kz.epam.InternetShop.model.Role
import kz.epam.InternetShop.model.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification


import javax.servlet.http.HttpServletRequest

class TokenProviderTest extends Specification{

    AppProperties appProperties = Mock()
    CustomUserDetailsService userDetailsService = Mock()
    TokenProvider tokenProvider = new TokenProvider(appProperties, userDetailsService)
    AppProperties.Auth auth = new AppProperties.Auth()

    def setup(){
        auth.setTokenExpirationMsec(600000L)
        auth.setTokenSecret("secret")
    }

    def "createToken() from authentication should return token"(){
        given:
            UserPrincipal userPrincipal = new UserPrincipal(1L, "username", "password", "fullName","address",1, Collections.singleton(Role.ROLE_USER))
            Authentication authentication = Mock(){
                getPrincipal() >> userPrincipal
            }

        when:
            tokenProvider.createToken(authentication)

        then:
            2 * appProperties.getAuth() >> auth

    }

    def "createToken() from user should return token"(){
        given:
            User user = User.builder().id(1L).build()

        when:
            tokenProvider.createToken(user)

        then:
            2 * appProperties.getAuth() >> auth

    }

    def "getUserIdFromToken() should return id from token"(){
        given:
            User user = User.builder().id(1L).build()
            def expectedId = user.getId()

        when:
            def token = tokenProvider.createToken(user)

        then:
            2 * appProperties.getAuth() >> auth

        when:
            def result = tokenProvider.getUserIdFromToken(token)

        then:
            1 * appProperties.getAuth() >> auth

        and:
            expectedId == result

    }


    def "getAuthenticationByUserFromDbWithId() should return UsernamePasswordAuthenticationToken"(){
        given:
            User user = User.builder().id(1L).authority(Collections.singleton(Role.ROLE_USER)).build()
            UserDetails userDetails = Mock(){
                getAuthorities() >> user.getAuthority()
            }
            def expected = new UsernamePasswordAuthenticationToken(userDetails, "", user.getAuthority())

        when:
            def token = tokenProvider.createToken(user)

        then:
            2 * appProperties.getAuth() >> auth

        when:
            def result = tokenProvider.getAuthenticationByUserFromDbWithId(token)

        then:
            1 * appProperties.getAuth() >> auth
            1 * userDetailsService.loadUserById(_) >> userDetails

        and:
            expected == result

    }

    def "getJwtFromRequest should return token"(){
        given:
            HttpServletRequest request = Mock()
            def token = "Bearer someToken"
            def expected = "someToken"

        when:
            def result = tokenProvider.getJwtFromRequest(request)

        then:
            1 * request.getHeader(_) >> token

        and:
            expected == result
    }

    def "getJwtFromRequest should return null if token is invalid"(){
        given:
            HttpServletRequest request = Mock()
            def token = "InvalidHeader someToken"

        when:
            def result = tokenProvider.getJwtFromRequest(request)

        then:
            1 * request.getHeader(_) >> token

        and:
            null == result
    }


}
