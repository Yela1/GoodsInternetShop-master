package kz.epam.InternetShop.util

import spock.lang.Specification

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CookieUtilTest extends Specification{

    def cookieOne = new Cookie("name_one", "one")

    def cookieTwo = new Cookie("name_two", "two")

    HttpServletRequest request = Mock(){
        getCookies() >> [cookieOne, cookieTwo]
    }

    HttpServletResponse response = Mock()

    def "getCookie() should return cookies if found"(){
        when:
            def result = CookieUtil.getCookie(request,"name_one")

        then:
            Optional.of(cookieOne) == result

    }

    def "getCookie() should return empty if not found"(){
        when:
            def result = CookieUtil.getCookie(request,"name_three")

        then:
            Optional.empty() == result
    }

    def "addCookie() should add cookie to response"(){
        when:
            CookieUtil.addCookie(response,"name_four", "four", 50)

        then:
            1 * response.addCookie(_)
    }

    def "deleteCookie() should delete cookie"(){
        when:
            CookieUtil.deleteCookie(request, response, "name_one")

        then:
            1 * response.addCookie(_)
    }
}
