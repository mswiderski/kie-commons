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
import org.kie.commons.admin.ConfigType;
import org.kie.commons.admin.ConfigurationFactory;
import org.kie.commons.admin.Repository;
import org.kie.commons.admin.RepositoryService;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class RepositoryServiceImplTest {

    @Inject
    private RepositoryService repoService;
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
    public void testCreateAndSaveRepository() {
        ConfigGroup repositoryConfig = factory.newConfigGroup(ConfigType.REPOSITORY, "default", "My defualt repository");
        repositoryConfig.addConfigItem(factory.newConfigItem("scheme", "git"));
        repositoryConfig.addConfigItem(factory.newConfigItem("origin", "http://test-url.com"));
        repositoryConfig.addConfigItem(factory.newConfigItem("username", "testuser"));
        repositoryConfig.addConfigItem(factory.newSecuredConfigItem("password", "testpassword"));

        repoService.addRepository(repositoryConfig);

        Repository repository = repoService.getRepository("default");
        assertNotNull(repository);
        assertTrue(repository instanceof GitRepository);
    }
}
