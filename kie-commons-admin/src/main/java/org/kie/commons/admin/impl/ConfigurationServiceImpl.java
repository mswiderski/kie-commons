package org.kie.commons.admin.impl;

import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.thoughtworks.xstream.XStream;
import org.kie.commons.admin.ConfigGroup;
import org.kie.commons.admin.ConfigType;
import org.kie.commons.admin.ConfigurationService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.StandardOpenOption;

@ApplicationScoped
public class ConfigurationServiceImpl implements ConfigurationService {

    private static final String REPOSITORY_ROOT = "git://kie-commons-admin/";

    private IOService ioService = new IOServiceNio2WrapperImpl();

    private FileSystem fileSystem;
    private Map<ConfigType, List<ConfigGroup>> configuration = new HashMap<ConfigType, List<ConfigGroup>>();

    @PostConstruct
    public void setup() {
        try {
            fileSystem = ioService.newFileSystem(URI.create(REPOSITORY_ROOT), new HashMap<String, Object>());
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(URI.create(REPOSITORY_ROOT));
        }
    }

    @Override
    public List<ConfigGroup> getConfiguration(final ConfigType type) {
        if (configuration.containsKey(type)) {
            return configuration.get(type);
        }
        List<ConfigGroup> configGroups = new ArrayList<ConfigGroup>();
        DirectoryStream<Path> foundConfigs = ioService.newDirectoryStream( ioService.get(REPOSITORY_ROOT), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( !org.kie.commons.java.nio.file.Files.isDirectory(entry) &&
                        entry.getFileName().toString().endsWith(type.getExt())) {
                    return true;
                }
                return false;
            }
        } );
        Iterator<Path> it = foundConfigs.iterator();
        XStream xstream = new XStream();
        while (it.hasNext()) {
            String content = ioService.readAllString(it.next());
            ConfigGroup configGroup = (ConfigGroup) xstream.fromXML(content);
            configGroups.add(configGroup);
        }
        configuration.put(type, configGroups);
        return configGroups;
    }

    @Override
    public boolean addConfiguration(ConfigGroup configGroup) {
        try {
            Path filePath = ioService.get(REPOSITORY_ROOT + configGroup.getName()+configGroup.getType().getExt());
            CommentedOption commentedOption = new CommentedOption("admin", "Created config " + filePath.getFileName());
            OutputStream outputStream = fileSystem.provider().newOutputStream(filePath, StandardOpenOption.TRUNCATE_EXISTING, commentedOption);
            System.out.println(configGroup.toString());
            outputStream.write(configGroup.toString().getBytes("UTF-8"));
            outputStream.close();

            return true;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error when creating asset", e);
        }
    }

    @Override
    public boolean removeConfiguration(ConfigGroup configGroup) {
        return ioService.deleteIfExists(ioService.get(REPOSITORY_ROOT + configGroup.getName()+configGroup.getType().getExt()));
    }
}
