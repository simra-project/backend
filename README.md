# SimRa Backend

This project is part of the SimRa research project which includes the following subprojects:

- [simra-android](https://github.com/simra-project/simra-android/): The SimRa app for Android.
- [simra-ios](https://github.com/simra-project/simra-ios): The SimRa app for iOS.
- [backend](https://github.com/simra-project/backend): The SimRa backend software.
- [dataset](https://github.com/simra-project/dataset): Result data from the SimRa project.
- [screenshots](https://github.com/simra-project/screenshots): Screenshots of both the iOS and Android app.
- [SimRa-Visualization](https://github.com/simra-project/SimRa-Visualization): Web application for visualizing the dataset.

In this project, we collect – with a strong focus on data protection and privacy – data on such near crashes to identify when and where bicyclists are especially at risk. We also aim to identify the main routes of bicycle traffic in Berlin. To obtain such data, we have developed a smartphone app that uses GPS information to track routes of bicyclists and the built-in acceleration sensors to pre-categorize near crashes. After their trip, users are asked to annotate and upload the collected data, pseudonymized per trip.
For more information see [our website](https://www.digital-future.berlin/en/research/projects/simra/).

## Development

You need Java and Maven installed on you machine. (You can use the [devcontainer](/.devcontainer/devcontainer.json) as reference)

1. Copy  `classifier.jar` and `preprocessing_android.jar` into the `lib` folder.
> You can get them from our CI pipeline: https://dev.azure.com/DSP-SS20/DSP-SS20/_build?definitionId=5&_a=summary 

2. Run `mvn initialize` to install the custom libraries.
3. Run `mvn install` to build the project
4. Copy and run the `jar` from the `/out` folder.


## Usage

In order to make it harder abuse the public API we are using a token which is shared with the app. It should be added to each request, e.g. `https://example.com/10/ride?clientHash=<CLIENTHASH>`. The `CLIENTHASH` is configured in the [`simRa_security.config`](./simRa_security.config.example).

## Deployment

After building, just execute `java -jar app.jar`. Make sure to provide the `simRa_backend.config`, `simRa_security.config`, `simRa_regions.config` in the same folder as the `app.jar`.

## Installation

Prerequisites:

- Ubuntu 18.04.4 LTS
- Port 80 and 443 accessible

1. Run `sudo apt-get install default-jdk`
2. [Follow this guide for SSL certificates](https://medium.com/@mightywomble/how-to-set-up-nginx-reverse-proxy-with-lets-encrypt-8ef3fd6b79e5)
3. [Create service to execute](https://dzone.com/articles/run-your-java-application-as-a-service-on-ubuntu) `java -jar app.jar`
