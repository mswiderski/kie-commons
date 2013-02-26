package org.kie.commons.admin;

import java.util.List;

public interface ConfigurationService {

    List<ConfigGroup> getConfiguration(ConfigType type);

    boolean addConfiguration(ConfigGroup configGroup);

    boolean removeConfiguration(ConfigGroup configGroup);
}
