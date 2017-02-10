package com.finaxys.loging.injectors;


import com.finaxys.utils.InjectLayerException;
import configuration.OutputSimulationConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that will insert a given String or a list of String in a given Kafka topic
 */
public class FileInjector extends AtomDataInjector {


    private static Logger LOGGER = LogManager.getLogger(FileInjector.class);

    private final OutputSimulationConfiguration outputConf;

    FileSystem fileSystem = FileSystems.getDefault();
    Writer fileWriter = null;
    Boolean firstLineWritten = false;




    public FileInjector(OutputSimulationConfiguration outputConf) {
        this.outputConf = outputConf;
    }

    @Override
    public void createOutput() throws InjectLayerException {

        String filePath = outputConf.getPathToOutputFile();
        Path path = fileSystem.getPath(filePath);

        // create directories if they don't exists
        File outputFile = new File(path.toString());
        if (outputFile.getParentFile() != null)
            outputFile.getParentFile().mkdirs();

        // Delete file if it already exists
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new InjectLayerException(this.getClass().getCanonicalName() + " : Impossible to delete file " + filePath, e);
        }


        try {
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString(), true)), 10000);
            // fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toString(), true)), 10000);
        } catch (FileNotFoundException e) {
            throw new InjectLayerException(this.getClass().getCanonicalName() + " : Impossible to instantiate file writer for file " + filePath, e);
        }

        LOGGER.debug("Output successfully created");
    }

    @Override
    public void closeOutput() throws InjectLayerException {
        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new InjectLayerException(this.getClass().getCanonicalName() + " : Impossible to close output", e);
        }
        LOGGER.debug("Output successfully closed");
    }

    @Override
    public void send(String message) throws InjectLayerException {
        // Writes a log by line with no empty lines at the beginning nor the end
        try {
            String messageToWrite;
            messageToWrite = firstLineWritten
                    ? "\n" + message
                    : message;
            fileWriter.write(messageToWrite);
            firstLineWritten = true;
        } catch (IOException e) {
            throw new InjectLayerException(this.getClass().getCanonicalName() + " : Impossible to write to file", e);
        }
    }
}
