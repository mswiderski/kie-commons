package org.kie.commons.java.nio.fs.jgit;

import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.commons.java.nio.base.version.VersionAttributes;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributeView;
import org.kie.commons.java.nio.fs.jgit.util.JGitUtil;

/**
 *
 */
public class JGitVersionAttributeView extends VersionAttributeView<JGitPathImpl> {

    private VersionAttributes attrs = null;

    public JGitVersionAttributeView( final JGitPathImpl path ) {
        super( path );
    }

    @Override
    public VersionAttributes readAttributes() throws IOException {
        if ( attrs == null ) {
            attrs = JGitUtil.buildVersionAttributes( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
        }
        return attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{ BasicFileAttributeView.class, VersionAttributeView.class, JGitVersionAttributeView.class };
    }

}
