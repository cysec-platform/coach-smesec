# SMESEC Coaches

This is the repository for the SMESEC coaches integrated into the CI process of CySeC.


### Remarks

Note that the source folder for the *coach-maven-plugin* should contain all pictures
that the coach needs, the coach XML file and the jar file containing the library.
The file name must be equal to the last part of the ID used in the coach.
For example, if the id in the coach is referred to as `eu.smesec.UserTraining` then
the file name must be `UserTraining.jar`.

To deploy the coach on the platform, put the _flatified coach_ (from the destination file)
into the `src/main/resources/coaches/` folder and push the change to the repository.
The build process will automatically deploy on the stage environment (wwwtest.smesec.eu/stage).


## License
This project is licensed under the Apache 2.0 license, see [LICENSE](LICENSE).
