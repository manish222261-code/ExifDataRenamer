# Exif Data Renamer

This is a File Renamer written in Java , which read exif data of file like Date and Time and rename file for better sorting!


## Screenshots
This is a Example on Android using Jvdroid App (Jvdroid is only available for arm64 cpu)

![terminal](https://github.com/user-attachments/assets/aea6155f-c166-4043-a43d-6786d8734748)





## Usage/Examples

Make sure you have Right Permission to Access Folder.

  Example :- 

    Folder Path for Android = "/storage/emulated/0/TEST/";

    Folder Path for Windows = "C:\Users\XYZ\Pictures\Photos";

    String FolderName = ""; // Enter the Proper FolderPath

  ## Change your Formatter Pattern

     public static String formatDate(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return formatter.format(zonedDateTime);
    }

Go to /src/main/java/ExifDataRenamer.java and replace your Folder Name and Formatter Pattern.
        


## Dependencies

This Project use [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) it is a Java library for reading metadata from media files.
## Authors

- [@manish222261](https://github.com/manish222261-code/)
