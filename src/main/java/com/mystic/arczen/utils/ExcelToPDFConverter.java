package com.mystic.arczen.utils;

import org.testng.reporters.jq.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ExcelToPDFConverter {
    public static File getPdfFromExcel(File xlsxFile) throws IOException, InterruptedException {
        //create a temporary file and grab the path for it
        Path tempScript = Files.createTempFile("script", ".vbs");

        //read all the lines of the .vbs script into memory as a list
        //here we pull from the resources of a Gradle build, where the vbs script is stored
        System.out.println("Path for vbs script is: '" + System.getProperty("user.dir") + "xlsx2pdf.vbs'");
        List<String> script = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "xlsx2pdf.vbs"));

        // append test.xlsm for file name. savePath was passed to this function
        String templateFile = xlsxFile.getName();
        File pdfFile = File.createTempFile(templateFile, ".pdf");
        System.out.println("templateFile is: " + templateFile);
        System.out.println("pdfFile is: " + pdfFile);

        //replace the placeholders in the vbs script with the chosen file paths
        for (int i = 0; i < script.size(); i++) {
            script.set(i, script.get(i).replaceAll("XL_FILE", templateFile));
            script.set(i, script.get(i).replaceAll("PDF_FILE", pdfFile.getName()));
            System.out.println("Line " + i + " is: " + script.get(i));
        }

        //write the modified code to the temporary script
        Files.write(tempScript, script);

        //create a processBuilder for starting an operating system process
        ProcessBuilder pb = new ProcessBuilder("wscript", tempScript.toString());

        //start the process on the operating system
        Process process = pb.start();

        long timeout = 10000L;
        TimeUnit minutes = TimeUnit.MINUTES;

        //tell the process how long to wait for timeout
        boolean success = process.waitFor(timeout, minutes);
        if (!success) {
            System.out.println("Error: Could not print PDF within " + timeout + minutes);
        } else {
            System.out.println("Process to run visual basic script for pdf conversion succeeded.");
        }

        return pdfFile;
    }

    public static void deletePdf(File pdf) {
        if (pdf.delete()) {
            System.out.println("PDF deleted successfully");
        } else {
            System.out.println("Error: Failed to delete the pdf");
        }
    }
}
