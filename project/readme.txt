mvn archetype:create-from-project
mvn install -f target\generated-sources\archetype
根据现有项目创建骨架，groupId=com.qingbo.project，artifactId=project-parent，version=1.0.0

mvn archetype:create-from-project -f project-service\pom.xml
mvn install -f project-serverice\target\generated-sources\archetype
还可以进入各个模块创建各模块项目的骨架，project-service等

mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=com.qingbo.project -DarchetypeArtifactId=project-parent
根据骨架创建项目，需要交互式输入groupId+artifactId+version等信息

mvn archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=com.qingbo.project -DarchetypeArtifactId=project-service -DarchetypeVersion=1.0.0 -DgroupId=com.qingbo.test -DartifactId=test-util -Dversion=1.0.0
