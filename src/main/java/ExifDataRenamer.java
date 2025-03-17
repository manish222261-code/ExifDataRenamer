import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.exif.ExifDirectoryBase;

public class ExifDataRenamer {

    public static void main(String[] args) {
    	
        // Enter Here the Folder Name which you want to Rename Photos and Videos
        
        // Folder Path for Android = "/storage/emulated/0/TEST/";
        // Folder Path for Windows = "C:\Users\XYZ\Pictures\Photos";

        String FolderName = ""; // Enter the Proper FolderPath
        
        
        File directory = new File(FolderName);
        File[] files = directory.listFiles();
        
        // This will check whether files are present or not in given Folder
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        Optional<Instant> instantOptional = extractDateTime(file.toPath());
                        
                        if (instantOptional.isPresent()) {
                            Instant instant = instantOptional.get();
                            
                            // Convert to IST if the file is an MP4
                            boolean isMp4 = file.getName().toLowerCase().endsWith(".mp4");
                            ZonedDateTime zonedDateTime = isMp4 ? instant.atZone(ZoneId.of("GMT")).withZoneSameInstant(ZoneId.of("Asia/Kolkata")) : instant.atZone(ZoneId.systemDefault());

                            String formattedDateTime = formatDate(zonedDateTime);
                            String newFileName = formattedDateTime + "." + getFileExtension(file);
                            Path newPath = file.toPath().resolveSibling(newFileName);
                            Files.move(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("File renamed: " + newFileName);
                        } else {
                            System.out.println("Failed to extract DateTime from file: " + file.getName());
                        }
                    } catch (IOException | ImageProcessingException e) {
                        System.err.println("Error processing file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }



    }
    
    // This extractDateTime method will extract DateTime from Photos and Videos using drew.imaging Library.
    
    private static Optional<Instant> extractDateTime(Path filePath) throws IOException, ImageProcessingException {
    	
        //File Path is initialised to Inputstream
        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
        	
            //Inputstream is passed inside readMetadataReader Function
            
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
             
             //Here DateTime is extracted and return it.
             
            return getDateTime(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
                    .or(() -> getDateTime(metadata, Mp4Directory.class, Mp4Directory.TAG_CREATION_TIME))
                    .or(() -> getDateTime(metadata, ExifDirectoryBase.class , ExifDirectoryBase.TAG_DATETIME_ORIGINAL))
                    .map(Date::toInstant); // Convert java.util.Date to Instant
        }
    }
     
     
     // This Function will check whether files contains Metadata or not.
     
    private static Optional<Date> getDateTime(Metadata metadata, Class<? extends Directory> directoryClass, int tagType) {
        if (metadata.containsDirectoryOfType(directoryClass)) {
            Directory directory = metadata.getFirstDirectoryOfType(directoryClass);
            if (directory != null) {
                return Optional.ofNullable(directory.getDate(tagType));
            }
        }
        return Optional.empty();
    }

    // This Function will Format the DateTime in desired Pattern.
    
    public static String formatDate(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return formatter.format(zonedDateTime);
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}