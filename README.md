# Mock REST API

This project has been created to create Mocks of REST APIs.

### Pre-requisites

- Java 11
- GraalVM 22.3.r17-grl
- Docker

### Install tools

We recommend to use SDKman for the installation of Java versions

```shell
sdk install java 22.3.r17-grl
```

You can list the available candidates for GraalVM with the command:

```shell
> sdk list java | grep grl

 GraalVM       |     | 22.3.r19     | grl     |            | 22.3.r19-grl        
               | >>> | 22.3.r17     | grl     | installed  | 22.3.r17-grl        
               |     | 22.3.r11     | grl     |            | 22.3.r11-grl 
```

### GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

#### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ ./gradlew bootBuildImage
```

Then, you can run the app like any other container:

```
$ docker run --rm -p 8080:8080 mock-api:0.0.1-SNAPSHOT
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./gradlew nativeCompile
```

Then, you can run the app as follows:
```
$ build/native/nativeCompile/mock-api
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./gradlew nativeTest
```

