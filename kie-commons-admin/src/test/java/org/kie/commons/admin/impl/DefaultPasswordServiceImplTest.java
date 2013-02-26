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
import org.kie.commons.admin.ConfigurationFactory;
import org.kie.commons.admin.ConfigurationService;
import org.kie.commons.admin.PasswordService;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DefaultPasswordServiceImplTest {

    @Inject
    private PasswordService passwordService;
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "kie-commons-admin.jar")
                .addPackage("org.kie.commons.admin")
                .addPackage("org.kie.commons.admin.impl")
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"))
                .addAsManifestResource("META-INF/services/org.kie.commons.java.nio.file.spi.FileSystemProvider", ArchivePaths.create("org.kie.commons.java.nio.file.spi.FileSystemProvider"));
    }

    @Test
    public void testPasswordSecureRoundTrip() {

        String plainPassword = "testpwd1";

        String encrypted = passwordService.encrypt(plainPassword);

        assertNotNull(encrypted);
        assertNotSame(plainPassword, encrypted);

        String decrypted = passwordService.decrypt(encrypted);
        assertNotNull(decrypted);
        assertEquals(plainPassword, decrypted);
    }
}
