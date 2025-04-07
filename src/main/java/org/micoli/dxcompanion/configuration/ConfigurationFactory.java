package org.micoli.dxcompanion.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.micoli.dxcompanion.models.AbstractNode;
import org.micoli.dxcompanion.models.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ConfigurationFactory {
    private static final Logger LOGGER = Logger.getInstance(ConfigurationFactory.class);

    public static Configuration get(Project project) throws ConfigurationException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        if (project == null) {
            return null;
        }
        VirtualFile projectBaseDir = project.getBaseDir();
        VirtualFile configFile = projectBaseDir.findChild(".dx-companion.json");
        if (configFile == null) {
            throw new ConfigurationException("No .dx-companion.json configuration file found.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerSubtypes(AbstractNode.class);

        String stringContent = "";
        try {
            byte[] content = configFile.contentsToByteArray();
            stringContent = new String(content, StandardCharsets.UTF_8);
            Configuration configuration = objectMapper.readValue(stringContent, Configuration.class);
            configuration.setSerial(Arrays.toString(md.digest(content)));
            return configuration;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage() + "\\n" + stringContent);
        }
    }
}