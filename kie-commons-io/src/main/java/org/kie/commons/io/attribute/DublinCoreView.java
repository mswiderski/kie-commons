package org.kie.commons.io.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.commons.data.Pair;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.AbstractBasicFileAttributeView;
import org.kie.commons.java.nio.base.AbstractPath;
import org.kie.commons.java.nio.base.NotImplementedException;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributeView;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.kie.commons.java.nio.file.attribute.FileTime;

import static org.kie.commons.data.Pair.*;
import static org.kie.commons.validation.PortablePreconditions.*;

/**
 *
 */
public class DublinCoreView extends AbstractBasicFileAttributeView<AbstractPath> {

    private static final String TITLE              = "dcore.title";
    private static final String CREATOR            = "dcore.creator";
    private static final String SUBJECT            = "dcore.subject";
    private static final String DESCRIPTION        = "dcore.description";
    private static final String PUBLISHER          = "dcore.publisher";
    private static final String CONTRIBUTOR        = "dcore.contributor";
    private static final String TYPE               = "dcore.type";
    private static final String FORMAT             = "dcore.format";
    private static final String IDENTIFIER         = "dcore.identifier";
    private static final String SOURCE             = "dcore.source";
    private static final String LANGUAGE           = "dcore.language";
    private static final String RELATION           = "dcore.relation";
    private static final String COVERAGE           = "dcore.coverage";
    private static final String RIGHTS             = "dcore.rights";
    private static final String LAST_MODIFIED_TIME = "lastModifiedTime";
    private static final String LAST_ACCESS_TIME   = "lastAccessTime";
    private static final String CREATION_TIME      = "creationTime";

    private static final Set<String> PROPERTIES = new HashSet<String>() {{
        add( TITLE );
        add( CREATOR );
        add( SUBJECT );
        add( DESCRIPTION );
        add( PUBLISHER );
        add( CONTRIBUTOR );
        add( TYPE );
        add( FORMAT );
        add( IDENTIFIER );
        add( SOURCE );
        add( LANGUAGE );
        add( RELATION );
        add( COVERAGE );
        add( RIGHTS );
    }};

    private final DublinCoreAttributes attrs;

    public DublinCoreView( final AbstractPath path ) {
        super( path );
        final Map<String, Object> content = path.getAttrStorage().getContent();

        final BasicFileAttributes fileAttrs = path.getFileSystem().provider().getFileAttributeView( path, BasicFileAttributeView.class ).readAttributes();

        final Map<String, List<String>> dcore = new HashMap<String, List<String>>() {{
            for ( final String property : PROPERTIES ) {
                put( property, new ArrayList<String>() );
            }
        }};

        for ( final Map.Entry<String, Object> entry : content.entrySet() ) {
            if ( entry.getKey().startsWith( TITLE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( TITLE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( CREATOR ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( CREATOR ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( SUBJECT ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( SUBJECT ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( DESCRIPTION ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( DESCRIPTION ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( PUBLISHER ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( PUBLISHER ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( CONTRIBUTOR ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( CONTRIBUTOR ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( TYPE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( TYPE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( FORMAT ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( FORMAT ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( IDENTIFIER ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( IDENTIFIER ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( SOURCE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( SOURCE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( LANGUAGE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( LANGUAGE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( RELATION ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( RELATION ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( COVERAGE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( COVERAGE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( RIGHTS ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( RIGHTS ).add( result.getK1(), result.getK2() );
            }
        }

        this.attrs = new DublinCoreAttributes() {
            @Override
            public List<String> titles() {
                return dcore.get( TITLE );
            }

            @Override
            public List<String> creators() {
                return dcore.get( CREATOR );
            }

            @Override
            public List<String> subjects() {
                return dcore.get( SUBJECT );
            }

            @Override
            public List<String> descriptions() {
                return dcore.get( DESCRIPTION );
            }

            @Override
            public List<String> publishers() {
                return dcore.get( PUBLISHER );
            }

            @Override
            public List<String> contributors() {
                return dcore.get( CONTRIBUTOR );
            }

            @Override
            public List<String> types() {
                return dcore.get( TYPE );
            }

            @Override
            public List<String> formats() {
                return dcore.get( FORMAT );
            }

            @Override
            public List<String> identifiers() {
                return dcore.get( IDENTIFIER );
            }

            @Override
            public List<String> sources() {
                return dcore.get( SOURCE );
            }

            @Override
            public List<String> languages() {
                return dcore.get( LANGUAGE );
            }

            @Override
            public List<String> relations() {
                return dcore.get( RELATION );
            }

            @Override
            public List<String> coverages() {
                return dcore.get( COVERAGE );
            }

            @Override
            public List<String> rights() {
                return dcore.get( RIGHTS );
            }

            @Override
            public FileTime lastModifiedTime() {
                return fileAttrs.lastModifiedTime();
            }

            @Override
            public FileTime lastAccessTime() {
                return fileAttrs.lastAccessTime();
            }

            @Override
            public FileTime creationTime() {
                return fileAttrs.creationTime();
            }

            @Override
            public boolean isRegularFile() {
                return fileAttrs.isRegularFile();
            }

            @Override
            public boolean isDirectory() {
                return fileAttrs.isDirectory();
            }

            @Override
            public boolean isSymbolicLink() {
                return fileAttrs.isSymbolicLink();
            }

            @Override
            public boolean isOther() {
                return fileAttrs.isOther();
            }

            @Override
            public long size() {
                return fileAttrs.size();
            }

            @Override
            public Object fileKey() {
                return fileAttrs.fileKey();
            }
        };
    }

    private Pair<Integer, String> extractValue( final Map.Entry<String, Object> entry ) {
        int start = entry.getKey().indexOf( '[' );
        if ( start < 0 ) {
            return newPair( 0, entry.getValue().toString() );
        }
        int end = entry.getKey().indexOf( ']' );

        return newPair( Integer.valueOf( entry.getKey().substring( start + 1, end ) ), entry.getValue().toString() );
    }

    @Override
    public String name() {
        return "dcore";
    }

    @Override
    public DublinCoreAttributes readAttributes() throws IOException {
        return attrs;
    }

    @Override
    public Map<String, Object> readAttributes( final String... attributes ) {
        final DublinCoreAttributes attrs = readAttributes();

        return new HashMap<String, Object>() {{
            for ( final String attribute : attributes ) {
                checkNotEmpty( "attribute", attribute );

                if ( attribute.equals( "*" ) || attribute.equals( TITLE ) ) {
                    for ( int i = 0; i < attrs.titles().size(); i++ ) {
                        final String content = attrs.titles().get( i );
                        put( buildAttrName( TITLE, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( CREATOR ) ) {
                    for ( int i = 0; i < attrs.creators().size(); i++ ) {
                        final String content = attrs.creators().get( i );
                        put( buildAttrName( CREATOR, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( SUBJECT ) ) {
                    for ( int i = 0; i < attrs.subjects().size(); i++ ) {
                        final String content = attrs.subjects().get( i );
                        put( buildAttrName( SUBJECT, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( DESCRIPTION ) ) {
                    for ( int i = 0; i < attrs.descriptions().size(); i++ ) {
                        final String content = attrs.descriptions().get( i );
                        put( buildAttrName( DESCRIPTION, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( PUBLISHER ) ) {
                    for ( int i = 0; i < attrs.publishers().size(); i++ ) {
                        final String content = attrs.publishers().get( i );
                        put( buildAttrName( PUBLISHER, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( CONTRIBUTOR ) ) {
                    for ( int i = 0; i < attrs.contributors().size(); i++ ) {
                        final String content = attrs.contributors().get( i );
                        put( buildAttrName( CONTRIBUTOR, i ), content );
                    }
                }

                if ( attribute.equals( "*" ) || attribute.equals( LAST_MODIFIED_TIME ) ) {
                    put( LAST_MODIFIED_TIME, null );
                }
                if ( attribute.equals( "*" ) || attribute.equals( LAST_ACCESS_TIME ) ) {
                    put( LAST_ACCESS_TIME, null );
                }
                if ( attribute.equals( "*" ) || attribute.equals( CREATION_TIME ) ) {
                    put( CREATION_TIME, null );
                }

                if ( attribute.equals( "*" ) || attribute.equals( TYPE ) ) {
                    for ( int i = 0; i < attrs.types().size(); i++ ) {
                        final String content = attrs.types().get( i );
                        put( buildAttrName( TYPE, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( FORMAT ) ) {
                    for ( int i = 0; i < attrs.formats().size(); i++ ) {
                        final String content = attrs.formats().get( i );
                        put( buildAttrName( FORMAT, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( IDENTIFIER ) ) {
                    for ( int i = 0; i < attrs.identifiers().size(); i++ ) {
                        final String content = attrs.identifiers().get( i );
                        put( buildAttrName( IDENTIFIER, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( SOURCE ) ) {
                    for ( int i = 0; i < attrs.sources().size(); i++ ) {
                        final String content = attrs.sources().get( i );
                        put( buildAttrName( SOURCE, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( LANGUAGE ) ) {
                    for ( int i = 0; i < attrs.languages().size(); i++ ) {
                        final String content = attrs.languages().get( i );
                        put( buildAttrName( LANGUAGE, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( RELATION ) ) {
                    for ( int i = 0; i < attrs.relations().size(); i++ ) {
                        final String content = attrs.relations().get( i );
                        put( buildAttrName( RELATION, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( COVERAGE ) ) {
                    for ( int i = 0; i < attrs.coverages().size(); i++ ) {
                        final String content = attrs.coverages().get( i );
                        put( buildAttrName( COVERAGE, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) || attribute.equals( RIGHTS ) ) {
                    for ( int i = 0; i < attrs.rights().size(); i++ ) {
                        final String content = attrs.rights().get( i );
                        put( buildAttrName( RIGHTS, i ), content );
                    }
                }
                if ( attribute.equals( "*" ) ) {
                    break;
                }
            }
        }};
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{ DublinCoreView.class };
    }

    private String buildAttrName( final String title,
                                  final int i ) {
        return title + "[" + i + "]";
    }

    @Override
    public void setAttribute( final String attribute,
                              final Object value ) throws IOException {
        checkNotEmpty( "attribute", attribute );
        checkCondition( "invalid attribute", PROPERTIES.contains( attribute ) );

        throw new NotImplementedException();
    }

}
