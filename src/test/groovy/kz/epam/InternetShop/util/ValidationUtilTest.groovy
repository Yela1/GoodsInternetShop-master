package kz.epam.InternetShop.util

import kz.epam.InternetShop.util.exception.NotFoundException
import spock.lang.Specification

class ValidationUtilTest extends Specification{

    def "checkNotFound() should throw NotFoundException"(){
        when:
            ValidationUtil.checkNotFound(false, "STRING")
        then:
            thrown(NotFoundException)
    }
}
