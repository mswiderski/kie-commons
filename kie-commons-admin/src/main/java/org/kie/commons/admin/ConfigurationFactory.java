package org.kie.commons.admin;

public interface ConfigurationFactory {

    ConfigGroup newConfigGroup(ConfigType type, String name, String description);

    ConfigItem<String> newConfigItem(String name, String valueType);

    ConfigItem<String> newSecuredConfigItem(String name, String valueType);
}
