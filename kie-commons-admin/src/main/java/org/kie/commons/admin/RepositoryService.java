package org.kie.commons.admin;

import java.util.Collection;

public interface RepositoryService {

    Repository getRepository(String alias);

    Collection<Repository> getRepositories();

    void addRepository(ConfigGroup repositoryConfig);
}
