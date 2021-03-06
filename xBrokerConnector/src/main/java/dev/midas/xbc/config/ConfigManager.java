package dev.midas.xbc.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.midas.xbc.config.domain.ExchangeConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class ConfigManager {

    private static final String DEFAULT_FILE = "exchange.yaml";
    private static final Logger LOG = LogManager.getLogger(ConfigManager.class.getName());

    public ConfigManager() {}

    public List<ExchangeConfig> loadDefaultExchangeConfig() {
        return this.loadExchangeConfig(DEFAULT_FILE);
    }

    /**
     * @param path if empty or null default path is loaded.
     * @return
     */
    public List<ExchangeConfig> loadExchangeConfig(String path) {
        path = StringUtils.isNotEmpty(path) ? path : DEFAULT_FILE;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        File file = new File(path);
        List<ExchangeConfig> configs = Collections.emptyList();

        try {
            file = new ClassPathResource(path).getFile();
            configs = mapper.readValue(file, new TypeReference<List<ExchangeConfig>>(){});

        } catch (FileNotFoundException e) {
            LOG.error(String.format("File %s not found", file.getName()));
            System.exit(1);

        } catch (IOException e) {
            LOG.error(String.format("Fail to load file %s", file.getName()));
            System.exit(1);
        }
        LOG.info(String.format("File %s successfully loaded", file.getName()));

        // catch not existing and repeated ids here
        return configs;
    }

}
