package org.kie.commons.admin.impl;

import java.util.List;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.commons.admin.ConfigGroup;
import org.kie.commons.admin.ConfigItem;
import org.kie.commons.admin.ConfigType;
import org.kie.commons.admin.ConfigurationFactory;
import org.kie.commons.admin.ConfigurationService;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ConfigurationServiceImplTest {

    @Inject
    private ConfigurationFactory factory;
    @Inject
    private ConfigurationService configurationService;

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "kie-commons-admin.jar")
                .addPackage("org.kie.commons.admin")
                .addPackage("org.kie.commons.admin.impl")
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));
    }

    @Test
    public void testAddConfiguration() {
        ConfigItem config = factory.newConfigItem("simple.property", "my custom value");
        assertNotNull(config);
        assertEquals("simple.property", config.getName());
        assertEquals("my custom value", config.getValue());

        ConfigItem securedConfig = factory.newSecuredConfigItem("secured.property", "my custom value");
        assertNotNull(securedConfig);
        assertEquals("secured.property", securedConfig.getName());
        assertNotSame("my custom value", securedConfig.getValue());

        ConfigGroup configGroup = factory.newConfigGroup(ConfigType.GLOBAL, "default", "This is default repository");
        assertNotNull(configGroup);
        assertEquals("default", configGroup.getName());
        assertEquals("This is default repository", configGroup.getDescription());

        configGroup.addConfigItem(config);
        configGroup.addConfigItem(securedConfig);

        boolean saved = configurationService.addConfiguration(configGroup);
        assertTrue(saved);

        List<ConfigGroup> repoConfigs = configurationService.getConfiguration(ConfigType.GLOBAL);
        assertNotNull(repoConfigs);
        assertEquals(1, repoConfigs.size());

        ConfigGroup savedConfigGroup = repoConfigs.get(0);
        assertNotNull(savedConfigGroup);
        assertEquals(2, savedConfigGroup.getItems().size());

    }
}
