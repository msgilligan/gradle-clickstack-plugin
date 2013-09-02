/*
 * Copyright 2010-2013, the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudbees.clickstack.component

import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.Usage

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class ClickStack implements SoftwareComponentInternal {
    private final Usage clickStackUsage = new ClickStackUsage();
    private final PublishArtifact clickStackArtifact;

    public ClickStack(PublishArtifact clickStackArtifact) {
        this.clickStackArtifact = clickStackArtifact;
    }

    public String getName() {
        return "clickstack";
    }

    public Set<Usage> getUsages() {
        return Collections.singleton(clickStackUsage);
    }

    private class ClickStackUsage implements Usage {
        public String getName() {
            return "clickstack";
        }

        public Set<PublishArtifact> getArtifacts() {
            return Collections.singleton(clickStackArtifact);
        }

        public Set<ModuleDependency> getDependencies() {
            return Collections.emptySet();
        }
    }
}
