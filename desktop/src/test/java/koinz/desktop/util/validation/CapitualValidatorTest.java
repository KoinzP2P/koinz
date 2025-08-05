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

public class CapitualValidatorTest {
    @BeforeEach
    public void setup() {
        final BaseCurrencyNetwork baseCurrencyNetwork = Config.baseCurrencyNetwork();
        final String currencyCode = baseCurrencyNetwork.getCurrencyCode();
        Res.setBaseCurrencyCode(currencyCode);
        Res.setBaseCurrencyName(baseCurrencyNetwork.getCurrencyName());
        CurrencyUtil.setBaseCurrencyCode(currencyCode);
    }

    @Test
    public void validate() {
        CapitualValidator validator = new CapitualValidator(
                new RegexValidator()
        );

        assertTrue(validator.validate("CAP-123456").isValid);
        assertTrue(validator.validate("CAP-XXXXXX").isValid);
        assertTrue(validator.validate("CAP-123XXX").isValid);

        assertFalse(validator.validate("").isValid);
        assertFalse(validator.validate(null).isValid);
        assertFalse(validator.validate("123456").isValid);
        assertFalse(validator.validate("XXXXXX").isValid);
        assertFalse(validator.validate("123XXX").isValid);
        assertFalse(validator.validate("12XXX").isValid);
        assertFalse(validator.validate("CAP-12XXX").isValid);
        assertFalse(validator.validate("CA-12XXXx").isValid);
    }
}
