package koinz.common.config;

import koinz.common.BisqException;

public class ConfigException extends BisqException {

    public ConfigException(String format, Object... args) {
        super(format, args);
    }
}
