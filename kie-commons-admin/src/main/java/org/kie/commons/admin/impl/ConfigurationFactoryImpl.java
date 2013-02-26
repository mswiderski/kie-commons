package org.kie.commons.admin.impl;

import org.kie.commons.admin.*;

import javax.inject.Inject;

public class ConfigurationFactoryImpl implements ConfigurationFactory {

    @Inject
    private PasswordService secureService;

    @Override
    public ConfigGroup newConfigGroup(ConfigType type, String name, String description) {
        ConfigGroup configGroup = new ConfigGroup();
        configGroup.setDescription(description);
        configGroup.setName(name);
        configGroup.setType(type);
        configGroup.setEnabled(true);
        return configGroup;
    }

    @Override
    public ConfigItem<String> newConfigItem(String name, String valueType) {
        ConfigItem<String> stringConfigItem = new ConfigItem<String>();
        stringConfigItem.setName(name);
        stringConfigItem.setValue(valueType);
        return stringConfigItem;
    }

    @Override
    public ConfigItem<String> newSecuredConfigItem(String name, String valueType) {
        ConfigItem<String> stringConfigItem = new ConfigItem<String>();
        stringConfigItem.setName(name);
        stringConfigItem.setValue(secureService.encrypt(valueType));
        return stringConfigItem;
    }
}
