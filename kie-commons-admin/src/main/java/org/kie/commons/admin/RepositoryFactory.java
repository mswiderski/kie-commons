package org.kie.commons.admin;

public interface RepositoryFactory {

    Repository newRepository(ConfigGroup repoConfig);
}
