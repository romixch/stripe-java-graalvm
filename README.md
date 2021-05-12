# stripe-java-graalvm

GraalVM configuration files for using [stripe-java](https://github.com/stripe/stripe-java) in
Quarkus or any oder App compiled with native-image.

This project generates GraalVM configuration files you need to
use [stripe-java](https://github.com/stripe/stripe-java) in an native image.

Currently, you can only download the configuration for `stripe-java:20.49.0`. However, the project
may evolve to a separate dependency on maven central, if people start to use it.