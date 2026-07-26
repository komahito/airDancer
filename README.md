# airdancer
An “air dancer” is a human-shaped balloon that looks like it's dancing when air is pumped into it, but this app has absolutely nothing to do with that :\)

The sound spectrum is to be analyzed at ten points across the range of 0 to 1024 Hz at 30 frames per second.
The amplitude at each Hz is reflected as an acceleration of the corresponding bar.

## Run the app using runtime image
Windows is not supported.
```bash
$ unzip -d <destination> airdancer.zip
$ cd <destination>
$ airdancer/bin/launcher
```

## Change the music
Changing the music via the UI is not yet supported, so if you'd like to change it, please clone the [repository](https://github.com/komahito/airDancer.git) to your environment and then follow the steps below.
1. Put any wav file to directly under the sounds directory `airdancer/src/main/resources/assets/sounds/.`
2. Edit the path in `fileNameWav` variable defined in `CalcSpecComponent.java` file. 

On the airdancer directory containing the `pom.xml`, you can run the app from commandline using Maven:
```bash
mvn clean javafx:run
```

