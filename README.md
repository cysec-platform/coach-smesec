# coach_cruiser

This is the default repository for all oaches integrated into the CI process of CySeC

## Prerequisits
Check out the flatifier repository and build the jar file
https://gitlab.fhnw.ch/SMESEC/cysec-flatifier

Save the jar file somewhere on your computer but don't put it into the repository.

## Build backup coach
The build process for the backup coach consists of a couple of manual steps.

1. build jar artifact within IDE

2. Flatify coach with flatifier jar
```
java -jar <jar location> <source folder> <destination file>
java -jar C:\Users\nicolas.odermatt\IdeaProjects\cysec-flatifier\target\flatifier.jar C:\Users\nicolas.odermatt\IdeaProjects\coach_cruiser\cysec_subcoach_backup\src\main\resources C:\Users\nicolas.odermatt\IdeaProjects\coach_cruiser\cysec_subcoach_backup\src\main\backup.xml
```
Note that the source folder should contain all pictures that the coach needs, the coach XML file and the jar file containing the library. The file name must be equal to the last part of the ID used in the coach. For example, if the id in the coach is referred to as "eu.smesec.UserTraining" then the file name must be UserTraining.jar.
3. Deploy coach in platform
Put the _flatified coach_ (from the destination file) into the src/main/resources/coaches/ folder and push the change to the repository. The build process will automatically deploy on the stage environment (wwwtest.smesec.eu/stage).

