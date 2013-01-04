package org.kie.commons.java.nio.base.version;

import java.util.List;

import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public interface VersionAttributes extends BasicFileAttributes {

    List<VersionRecord> history();

}
