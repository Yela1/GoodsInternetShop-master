package kz.epam.InternetShop.security

import io.jsonwebtoken.Jwts
import kz.epam.InternetShop.configuration.AppProperties
import kz.epam.InternetShop.model.Role
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

import static kz.epam.InternetShop.ObjectCreator.*


import javax.servlet.http.HttpServletRequest

class TokenProviderTest extends Specification{

    AppProperties appProperties = Mock()
    CustomUserDetailsService userDetailsService = Mock()
    TokenProvider tokenProvider = new TokenProvider(appProperties, userDetailsService)
    AppProperties.Auth auth = new AppProperties.Auth()

    def setup(){
        auth.setTokenExpirationMsec(31556952000L)
        auth.setTokenSecret("secret")
    }

    def "createToken() from authentication should return token"(){
        given:
            def expectedId = 15L
            def userPrincipal = new UserPrincipal(15L, "username", "password", "fullName","address",1, Collections.singleton(Role.ROLE_USER))
            Authentication authentication = Mock(){
                getPrincipal() >> userPrincipal
            }

        when:
            def result = tokenProvider.createToken(authentication)

        then:
            2 * appProperties.getAuth() >> auth

        and:
            result
            expectedId == Long.parseLong(Jwts.parser()
                    .setSigningKey(auth.getTokenSecret())
                    .parseClaimsJws(result).getBody().getSubject())

    }

    def "createToken() from user should return token"(){
        given:
            def expectedId = 1L
            def user = createUser(expectedId)

        when:
            def result =tokenProvider.createToken(user)

        then:
            2 * appProperties.getAuth() >> auth

        and:
            result
            expectedId == Long.parseLong(Jwts.parser()
                    .setSigningKey(auth.getTokenSecret())
                    .parseClaimsJws(result).getBody().getSubject())


    }

    def "getUserIdFromToken() should return id from token"(){
        given:
            def token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNSIsImlhdCI6MTYzODg5MDczNywiZXhwIjoxNjcwNDQ3Njg5fQ.GoUfmZv6Wxl2TJDWNnmF6ZB72wMOrm7kl1kuL8E7mFV_9QFCCQy3FkrIUdkyuh41cRZtDUPQ-7bjY1-LxXejMQ"
            def user = createUser(15L)
            def expectedId = user.getId()

        when:
            def result = tokenProvider.getUserIdFromToken(token)

        then:
            1 * appProperties.getAuth() >> auth

        and:
            expectedId == result

    }


    def "getAuthenticationByUserFromDbWithId() should return UsernamePasswordAuthenticationToken"(){
        given:
            def token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNSIsImlhdCI6MTYzODg5MDczNywiZXhwIjoxNjcwNDQ3Njg5fQ.GoUfmZv6Wxl2TJDWNnmF6ZB72wMOrm7kl1kuL8E7mFV_9QFCCQy3FkrIUdkyuh41cRZtDUPQ-7bjY1-LxXejMQ"
            def user = createUser(1L, Collections.singleton(Role.ROLE_USER))
            UserDetails userDetails = Mock(){
                getAuthorities() >> user.getAuthority()
            }
            def expected = new UsernamePasswordAuthenticationToken(userDetails, "", user.getAuthority())

        when:
            def result = tokenProvider.getAuthenticationByUserFromDbWithId(token)

        then:
            1 * appProperties.getAuth() >> auth
            1 * userDetailsService.loadUserById({it == 15L}) >> userDetails

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
