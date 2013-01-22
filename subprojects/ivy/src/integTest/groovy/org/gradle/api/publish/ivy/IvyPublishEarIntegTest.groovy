/*
 * Copyright 2012 the original author or authors.
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




package org.gradle.api.publish.ivy

import org.gradle.integtests.fixtures.AbstractIntegrationSpec

class IvyPublishEarIntegTest extends AbstractIntegrationSpec {
    public void "can publish EAR only for mixed java and WAR and EAR project"() {
        given:
        file("settings.gradle") << "rootProject.name = 'publishEar' "

        and:
        buildFile << """
            apply plugin: 'java'
            apply plugin: 'war'
            apply plugin: 'ear'
            apply plugin: 'ivy-publish'

            group = 'org.gradle.test'
            version = '1.9'

            repositories {
                mavenCentral()
            }

            dependencies {
                compile "commons-collections:commons-collections:3.2.1"
                runtime "commons-io:commons-io:1.4"
            }

            publishing {
                repositories {
                    ivy {
                        url '${ivyRepo.uri}'
                    }
                }
                publications {
                    ivyEar(IvyPublication) {
                        artifact ear
                    }
                }
            }
        """

        when:
        run "publish"

        then:
        def ivyModule = ivyRepo.module("org.gradle.test", "publishEar", "1.9")
        ivyModule.assertPublished()
        ivyModule.assertArtifactsPublished("ivy-1.9.xml", "publishEar-1.9.ear")
        ivyModule.ivy.dependencies.isEmpty()
        // TODO:DAZ Validate configurations and artifacts in ivy.xml
    }
}
