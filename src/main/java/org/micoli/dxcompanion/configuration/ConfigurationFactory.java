package org.micoli.dxcompanion.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.diagnostic.Logger;
import org.micoli.dxcompanion.configuration.models.AbstractNode;
import org.micoli.dxcompanion.configuration.models.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationFactory {
    private static final Logger LOGGER = Logger.getInstance(ConfigurationFactory.class);
    private static final MessageDigest messageDigest;
    private static final ArrayList<String> acceptableConfigurationFiles = new ArrayList<>(Arrays.asList(".dx-companion.json", ".dx-companion.local.json"));
    ;

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration get(String projectPath) throws ConfigurationException {
        List<String> files = acceptableConfigurationFiles.stream().filter((configurationFile) -> new File(projectPath, configurationFile).exists()).toList();
        if (files.isEmpty()) {
            throw new ConfigurationException("No .dx-companion(.*).json configuration file(s) found.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerSubtypes(AbstractNode.class);
        String stringContent ="";
        try {
            stringContent = loadJsonFiles(projectPath, files);
            Configuration configuration = objectMapper.readValue(stringContent, Configuration.class);
            configuration.setSerial(Arrays.toString(messageDigest.digest(stringContent.getBytes(StandardCharsets.UTF_8))));
            return configuration;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage() + "\\n" + stringContent);
        }
    }

    private static String loadJsonFiles(String projectPath ,List<String> files) throws FileNotFoundException, GsonTools.JsonObjectExtensionConflictException {
        JsonParser parser = new JsonParser();
        JsonObject mergedJson = new JsonObject();
        for (String file : files) {
            JsonElement jsonFile = parser.parse(new FileReader(new File(projectPath, file)));
            GsonTools.extendJsonObject(mergedJson, GsonTools.ConflictStrategy.PREFER_SECOND_OBJ,jsonFile.getAsJsonObject());
        }
        return mergedJson.toString();
    }
}