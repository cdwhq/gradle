/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.resource.cached;

import org.gradle.internal.resource.ExternalResourceName;
import org.gradle.internal.resource.ExternalResourceRepository;
import org.gradle.internal.resource.metadata.ExternalResourceMetaData;
import org.gradle.internal.resource.metadata.ExternalResourceMetaDataCompare;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DefaultCachedExternalResourceChecker implements CachedExternalResourceChecker {

    private final Map<ExternalResourceName, ExternalResourceRepository> resourcesToRepos = new HashMap<>();
    public void add(ExternalResourceName location, ExternalResourceRepository repository) {
        resourcesToRepos.put(location, repository);
    }

    @Override
    public ExternalResourceRemoteMetaData check(ExternalResourceName location, @Nullable ExternalResourceMetaData localMetaData) {
        ExternalResourceRepository repository = resourcesToRepos.get(location);

        if (repository == null) {
            return null;
        }

        ExternalResourceMetaData remoteMetaData = repository.resource(location, true).getMetaData();

        if (remoteMetaData == null) {
            return null;
        }

        boolean isUpToDate = ExternalResourceMetaDataCompare.isDefinitelyUnchanged(localMetaData, () -> remoteMetaData);

        return new ExternalResourceRemoteMetaData(remoteMetaData, isUpToDate);
    }
}
