package org.kie.commons.admin;

import java.util.*;

import com.thoughtworks.xstream.XStream;

public class ConfigGroup {

    private String name;
    private String description;
    private ConfigType type;
    private boolean enabled;

    private Map<String, ConfigItem> items = new HashMap<String, ConfigItem>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    public Collection<ConfigItem> getItems() {
        return items.values();
    }

    public void addConfigItem(ConfigItem configItem) {
        if (this.items.containsKey(configItem.getName())) {
            throw new IllegalArgumentException("ConfigItem with name " + configItem.getName() + " already exists");
        }
        this.items.put(configItem.getName(), configItem);
    }

    public ConfigItem getConfigItem(String name) {
        return this.items.get(name);
    }

    public String getConfigItemValue(String name) {
        ConfigItem<String> configItem = this.items.get(name);
        if (configItem == null) {
            return null;
        } else {
            return configItem.getValue();
        }

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        XStream xstream = new XStream();
        return xstream.toXML(this);
    }
}
