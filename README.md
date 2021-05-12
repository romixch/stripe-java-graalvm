# stripe-java-graalvm

GraalVM configuration files for using [stripe-java](https://github.com/stripe/stripe-java) in
Quarkus or any oder App compiled with native-image.

This project generates GraalVM configuration files you need to
use [stripe-java](https://github.com/stripe/stripe-java) in an native image.

## How to use it in your project

Copy the whole directory `native-image`
in [META-INF](https://github.com/romixch/stripe-java-graalvm/tree/main/src/main/resources/META-INF)
to your own `META-INF` directory.

## Known issues

Your binary may grow significantly. This is because stripe-java uses a lot of serialization. 