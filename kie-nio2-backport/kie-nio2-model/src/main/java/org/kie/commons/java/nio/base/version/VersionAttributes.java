package org.kie.commons.java.nio.base.version;

import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public interface VersionAttributes extends BasicFileAttributes {

    VersionHistory history();

}
