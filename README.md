# A2J ERV wrapper

[![Pipeline](https://github.com/digitalservicebund/a2j-erv-wrapper/actions/workflows/pipeline.yml/badge.svg)](https://github.com/digitalservicebund/a2j-erv-wrapper/actions/workflows/pipeline.yml)
[![Scan](https://github.com/digitalservicebund/a2j-erv-wrapper/actions/workflows/scan.yml/badge.svg)](https://github.com/digitalservicebund/a2j-erv-wrapper/actions/workflows/scan.yml)

Java SpringBoot service that handles erv for a2j.

The erv wrapper connects to two external services in its current state. FitConnect and an EGVP Enterprise instance.
The interface of the EGVP-Enterprise instance is wrapped in a Restful http client.

Currently, the flow of a submission is supposed to flow like the graph describes:

```mermaid
sequenceDiagram
    participant A2J
    participant ErvWrapper
    participant EGVPClient
    participant FitConnect
    A2J->>ErvWrapper: submit
    activate ErvWrapper
    ErvWrapper->>FitConnect: sendSubmission
    deactivate ErvWrapper
    FitConnect->>+ErvWrapper: incomingSubmissions
    ErvWrapper-->>-EGVPClient: sendMessage
    activate EGVPClient
    alt poll until delviery confirmed
    ErvWrapper->>EGVPClient: check Delviery Status
    activate ErvWrapper
    EGVPClient->>ErvWrapper: delivery Pending
    deactivate ErvWrapper
    else
    EGVPClient->>ErvWrapper: Message Delivered
    deactivate EGVPClient
    activate ErvWrapper
    ErvWrapper->>FitConnect: send confirmation to BundId
    deactivate ErvWrapper
    end
```

## Test Deployment

The Erv Wrapper and EGVPClient must share a filesystem to reference files to be sent.
Polling messages is disabled by default. Run the wrapper with `--egvp.pollDeliveryStatus` to enable polling to
retrieve message delivery confirmations.

## Prerequisites

Java 21, Docker for building + running the containerized application:

```bash
brew install openjdk@21
brew install --cask docker # or just `brew install docker` if you don't want the Desktop app
```

For the provided Git hooks you will need:

```bash
brew install lefthook node talisman
```

## Getting started

**To get started with development, make a copy of the local config:**

```bash
cp src/main/resources/application-local.yaml.example src/main/resources/application-local.yaml
```

You'll also need to run spring with the `local` [profile](https://www.baeldung.com/spring-profiles#5-environment-variable) (how to do that depends on your dev setup).

### Replace dummy config

The checked in dummy configuration gets you started. However if you want to use fit-connect properly, you need to register at the [fitko self service portal](https://portal.auth-testing.fit-connect.fitko.dev/login) and create:

- a sender client (client for `Onlinedienst`),
- a subscriber client (client for `Verwaltungssystem`, you can use [this tool](https://git.fitko.de/fit-connect/fit-connect-tools/-/tree/main?ref_type=heads#skript-createselfsignedjwkspy) to create the keys),
- a destination (`Zustellpunkt`).

Use these to replace the ids and secrets in your `application-local.yaml`.

## Tests

The project has distinct unit and integration test sets.

**To run just the unit tests:**

```bash
./gradlew test
```

**To run the integration tests:**

```bash
./gradlew integrationTest
```

**Note:** Running integration tests requires passing unit tests (in Gradle terms: integration tests depend on unit
tests), so unit tests are going to be run first. In case there are failing unit tests we won't attempt to continue
running any integration tests.

**To run integration tests exclusively, without the unit test dependency:**

```bash
./gradlew integrationTest --exclude-task test
```

Denoting an integration test is accomplished by using a JUnit 5 tag annotation: `@Tag("integration")`.

Furthermore, there is another type of test worth mentioning. We're
using [ArchUnit](https://www.archunit.org/getting-started)
for ensuring certain architectural characteristics, for instance making sure that there are no cyclic dependencies.

## Formatting

Java source code formatting must conform to the [Google Java Style](https://google.github.io/styleguide/javaguide.html).
Consistent formatting, for Java as well as various other types of source code, is being enforced
via [Spotless](https://github.com/diffplug/spotless).

**Check formatting:**

```bash
./gradlew spotlessCheck
```

**Autoformat sources:**

```bash
./gradlew spotlessApply
```

## Git hooks

The repo contains a [Lefthook](https://github.com/evilmartians/lefthook/blob/master/docs/full_guide.md) configuration,
providing a Git hooks setup out of the box.

**To install these hooks, run:**

```bash
./run.sh init
```

The hooks are supposed to help you to:

- commit properly formatted source code only (and not break the build otherwise)
- write [conventional commit messages](https://chris.beams.io/posts/git-commit/)
- not accidentally push [secrets and sensitive information](https://thoughtworks.github.io/talisman/)

## Code quality analysis

Continuous code quality analysis is performed in the pipeline upon pushing to trunk.

**To run the analysis locally:**

```bash
SONAR_TOKEN=[sonar-token] ./gradlew sonarqube
```

Go to [https://sonarcloud.io](https://sonarcloud.io/dashboard?id=digitalservicebund_a2j-erv-wrapper)
for the analysis results.

## Container image

Container images running the application are automatically published by the pipeline to
the [GitHub Packages Container registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry).

**To run the latest published image:**

```bash
docker run -p8080:8080 "ghcr.io/digitalservicebund/a2j-erv-wrapper:$(git log -1 origin/main --format='%H')"
```

The service will be accessible at `http://localhost:8080`.

We are using Spring's built-in support for producing an optimized container image:

```bash
./gradlew bootBuildImage
docker run -p8080:8080 ghcr.io/digitalservicebund/a2j-erv-wrapper
```

Container images in the registry are [signed with keyless signatures](https://github.com/sigstore/cosign/blob/main/KEYLESS.md).

**To verify an image**:

```bash
cosign verify "ghcr.io/digitalservicebund/a2j-erv-wrapper:$(git log -1 origin/main --format='%H')" --certificate-identity="https://github.com/digitalservicebund/a2j-erv-wrapper/.github/workflows/pipeline.yml@refs/heads/main" --certificate-oidc-issuer="https://token.actions.githubusercontent.com"
```

If you need to push a new container image to the registry manually there are two ways to do this:

**Via built-in Gradle task:**

```bash
export CONTAINER_REGISTRY=ghcr.io
export CONTAINER_IMAGE_NAME=digitalservicebund/a2j-erv-wrapper
export CONTAINER_IMAGE_VERSION="$(git log -1 --format='%H')"
CONTAINER_REGISTRY_USER=[github-user] CONTAINER_REGISTRY_PASSWORD=[github-token] ./gradlew bootBuildImage --publishImage
```

**Note:** Make sure you're using a GitHub token with the necessary `write:packages` scope for this to work.

**Using Docker:**

```bash
echo [github-token] | docker login ghcr.io -u [github-user] --password-stdin
docker push "ghcr.io/digitalservicebund/a2j-erv-wrapper:$(git log -1 --format='%H')"
```

**Note:** Make sure you're using a GitHub token with the necessary `write:packages` scope for this to work.

## Vulnerability Scanning

Scanning container images for vulnerabilities is performed with [Trivy](https://github.com/aquasecurity/trivy)
as part of the pipeline's `build` job, as well as each night for the latest published image in the container
repository.

**To run a scan locally:**

Install Trivy:

```bash
brew install aquasecurity/trivy/trivy
```

```bash
./gradlew bootBuildImage
trivy image --severity HIGH,CRITICAL ghcr.io/digitalservicebund/a2j-erv-wrapper:latest
```

As part of the automated vulnerability scanning we are generating a Cosign vulnerability scan record using Trivy,
and then use Cosign to attach an attestation of it to the container image, again
[signed with keyless signatures](https://github.com/sigstore/cosign/blob/main/KEYLESS.md) similar to signing the
container image itself. Using a policy engine in a cluster the vulnerability scan can be verified and for instance
running a container rejected if a scan is not current.

## License Scanning

License scanning is performed as part of the pipeline's `build` job. Whenever a production dependency
is being added with a yet unknown license the build is going to fail.

**To run a scan locally:**

```bash
./gradlew checkLicense
```

## Architecture Decision Records

[Architecture decisions](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
are kept in the `docs/adr` directory. For adding new records install the [adr-tools](https://github.com/npryce/adr-tools) package:

```bash
brew install adr-tools
```

See https://github.com/npryce/adr-tools regarding usage.

## Contributing

🇬🇧
Everyone is welcome to contribute the development of the _a2j-erv-wrapper_. You can contribute by opening pull request,
providing documentation or answering questions or giving feedback. Please always follow the guidelines and our
[Code of Conduct](CODE_OF_CONDUCT.md).

🇩🇪
Jede:r ist herzlich eingeladen, die Entwicklung des _a2j-erv-wrapper_ mitzugestalten. Du kannst einen Beitrag leisten,
indem du Pull-Requests eröffnest, die Dokumentation erweiterst, Fragen beantwortest oder Feedback gibst.
Bitte befolge immer die Richtlinien und unseren [Verhaltenskodex](CODE_OF_CONDUCT_DE.md).

### Contributing code

🇬🇧
Open a pull request with your changes and it will be reviewed by someone from the team. When you submit a pull request,
you declare that you have the right to license your contribution to the DigitalService and the community.
By submitting the patch, you agree that your contributions are licensed under the MIT license.

Please make sure that your changes have been tested before submitting a pull request.

🇩🇪
Nach dem Erstellen eines Pull Requests wird dieser von einer Person aus dem Team überprüft. Wenn du einen Pull Request
einreichst, erklärst du dich damit einverstanden, deinen Beitrag an den DigitalService und die Community zu
lizenzieren. Durch das Einreichen des Patches erklärst du dich damit einverstanden, dass deine Beiträge unter der
MIT-Lizenz lizenziert sind.

Bitte stelle sicher, dass deine Änderungen getestet wurden, bevor du einen Pull Request sendest.
