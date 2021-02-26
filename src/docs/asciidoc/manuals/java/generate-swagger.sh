# wget http://central.maven.org/maven2/io/swagger/swagger-codegen-cli/2.4.2/swagger-codegen-cli-2.4.2.jar -O swagger-codegen-cli.jar
java -jar swagger-codegen-cli.jar generate \
  -i http://localhost:8080/document-service/v2/api-docs \
  --api-package de.woody64k.project.adapter.restapi \
  --model-package de.woody64k.project.adapter.restapi.model \
  --invoker-package de.woody64k.project.adapter.restapi.invoker \
  --group-id de.woody64k.project \
  --artifact-id project-rest-api \
  --artifact-version 1.0.0-SNAPSHOT \
  -l java \
  -o project