/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kieora.backend.lucene;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;
import org.kie.kieora.backend.lucene.fields.SimpleFieldFactory;
import org.kie.kieora.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.kie.kieora.backend.lucene.setups.RAMLuceneSetup;
import org.kie.kieora.engine.MetaIndexEngine;
import org.kie.kieora.model.KObject;
import org.kie.kieora.model.KProperty;
import org.kie.kieora.model.schema.MetaType;

import static org.junit.Assert.*;

/**
 *
 */
public class IndexEngineMetaModelTest {

    @Test
    public void testSimpleIndex() throws IOException {

        final InMemoryMetaModelStore metaModel = new InMemoryMetaModelStore();
        final LuceneSetup luceneSetup = new RAMLuceneSetup();
        final FieldFactory factory = new SimpleFieldFactory();

        final MetaIndexEngine engine = new LuceneIndexEngine( metaModel, luceneSetup, factory );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "unique.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "Path";
                    }
                };
            }

            @Override
            public String getKey() {
                return "unique.id.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.comment";
                        }

                        @Override
                        public String getValue() {
                            return "My comment here that has some content that is important to my users.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                }};
            }
        } );

        assertNotNull( metaModel.getMetaObject( "Path" ) );

        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "unique.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "Path";
                    }
                };
            }

            @Override
            public String getKey() {
                return "unique.id.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.comment";
                        }

                        @Override
                        public String getValue() {
                            return "My comment here that has some content that is important to my users.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Integer>() {
                        @Override
                        public String getName() {
                            return "dcore.review";
                        }

                        @Override
                        public Integer getValue() {
                            return 10;
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Date>() {
                        @Override
                        public String getName() {
                            return "dcore.lastModifiedTime";
                        }

                        @Override
                        public Date getValue() {
                            return new Date();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                }};
            }
        } );

        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "some.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "PathX";
                    }
                };
            }

            @Override
            public String getKey() {
                return "some.id.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Date>() {
                        @Override
                        public String getName() {
                            return "dcore.lastModifiedTime";
                        }

                        @Override
                        public Date getValue() {
                            return new Date();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                }};
            }
        } );

        assertNotNull( metaModel.getMetaObject( "Path" ) );

        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNotNull( metaModel.getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        assertNotNull( metaModel.getMetaObject( "PathX" ) );

        assertNotNull( metaModel.getMetaObject( "PathX" ).getProperty( "dcore.author" ) );
        assertNotNull( metaModel.getMetaObject( "PathX" ).getProperty( "dcore.lastModifiedTime" ) );
    }

}
