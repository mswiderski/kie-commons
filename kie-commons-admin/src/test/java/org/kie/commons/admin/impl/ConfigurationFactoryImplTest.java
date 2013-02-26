package org.kie.commons.admin.impl;

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

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ConfigurationFactoryImplTest {

    @Inject
    private ConfigurationFactory factory;

    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "kie-commons-admin.jar")
                .addPackage("org.kie.commons.admin")
                .addPackage("org.kie.commons.admin.impl")
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));
    }

    @Test
    public void testCreateConfigItem() {
        ConfigItem config = factory.newConfigItem("simple.property", "my custom value");
        assertNotNull(config);
        assertEquals("simple.property", config.getName());
        assertEquals("my custom value", config.getValue());
    }

    @Test
    public void testCreateSecuredConfigItem() {
        ConfigItem config = factory.newSecuredConfigItem("simple.property", "my custom value");
        assertNotNull(config);
        assertEquals("simple.property", config.getName());
        assertNotSame("my custom value", config.getValue());
    }

    @Test
    public void testCreateConfigGroup() {
        ConfigGroup configGroup = factory.newConfigGroup(ConfigType.GLOBAL, "default", "This is default repository");
        assertNotNull(configGroup);
        assertEquals("default", configGroup.getName());
        assertEquals("This is default repository", configGroup.getDescription());
        assertEquals(true, configGroup.isEnabled());
        assertEquals(ConfigType.GLOBAL, configGroup.getType());
        assertEquals(0, configGroup.getItems().size());
    }
}
