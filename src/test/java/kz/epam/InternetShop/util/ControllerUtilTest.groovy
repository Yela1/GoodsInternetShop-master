package kz.epam.InternetShop.util

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import spock.lang.Specification


class ControllerUtilTest extends Specification{

    BindingResult bindingResult = Mock()
    def errorName = "testError"
    FieldError fieldError = new FieldError("errorObject","field",errorName)

    def "getErrors() should return errors"(){

        when:
            def result = ControllerUtil.getErrors(bindingResult)

        then:
            1 * bindingResult.getFieldErrors() >> [fieldError]

        and:
            errorName == result.get("fielderror")
    }

}
