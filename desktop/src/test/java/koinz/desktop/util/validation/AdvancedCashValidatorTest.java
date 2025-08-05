package koinz.desktop.util.validation;

import koinz.core.locale.CurrencyUtil;
import koinz.core.locale.Res;
import koinz.core.util.validation.RegexValidator;

import koinz.common.config.BaseCurrencyNetwork;
import koinz.common.config.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedCashValidatorTest {
    @BeforeEach
    public void setup() {
        final BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        final String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);
    }

    @Test
    public void validate(){
        AdvancedCashValidator validator = new AdvancedCashValidator(
                new EmailValidator(),
                new RegexValidator()
        );

        assertTrue(validator.validate("U123456789012").isValid);
        assertTrue(validator.validate("test@user.com").isValid);

        assertFalse(validator.validate("").isValid);
        assertFalse(validator.validate(null).isValid);
        assertFalse(validator.validate("123456789012").isValid);
    }
}
