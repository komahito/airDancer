# airdancer
An “air dancer” is a human-shaped balloon that looks like it's dancing when air is pumped into it, but there is no relationship with this app :\)

## Run the app using runtime image
```
$ target/airdancer/bin/launcher
```

## Change the music
This app has not supported changing the music yet, so if you'd like to change it, please clone the [repository](https://github.com/komahito/airDancer.git) to your environment and then follow the steps below.
1. Put any wav file to directly under the sounds directory `airdancer/src/main/resources/assets/sounds/.`
2. Edit the path in `fileNameWav` variable defined in `CalcSpecComponent.java` file. 

On the airdancer directory containing the `pom.xml`, you can run app from commandline using Maven:
```
mvn clean javafx:run
```

