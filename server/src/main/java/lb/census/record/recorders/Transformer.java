package lb.census.record.recorders;

import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lb.census.CensusException;
import lb.census.config.CensusConfig;
import lb.census.record.log.LogRecord;

/**
 * Special recorder that will execute a dynamic script. The script is allowed to modify log record before it will
 * be given to the actual recorders. The location of the script is configured using the census config. The language
 * of the script is Javascript.
 * Created by philippe on 05/05/16.
 */
@Component
@Order(value = 1)
public class Transformer implements Recorder {

    private static final Logger LOGGER = LoggerFactory.getLogger("CENSUS_RECORDERS");
    @Autowired
    private CensusConfig censusConfig;
    private ScriptEngine scriptEngine;
    private String script;

    @Override
    public void initialize() {
        if (censusConfig.getTransformer() == null) {
            return;
        }
        LOGGER.info("Reading script from {}", censusConfig.getTransformer());
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[100];
        try (FileReader reader = new FileReader(censusConfig.getTransformer())) {
            while (reader.ready()) {
                int length = reader.read(buffer);
                stringBuilder.append(buffer, 0, length);
            }
            script = stringBuilder.toString();
        } catch (IOException e) {
            throw new CensusException(e);
        }
        LOGGER.trace("Read script {}", script);
        scriptEngine = new ScriptEngineManager().getEngineByExtension("js");
    }

    @Override
    public void record(LogRecord logRecord, RecorderContext recorderContext) {
        if (scriptEngine == null) {
            return;
        }
        scriptEngine.put("logRecord", logRecord);
        try {
            scriptEngine.eval(script);
        } catch (ScriptException e) {
            throw new CensusException(e);
        }
    }

    @Override
    public void store(Date date, RecorderContext recorderContext) {
        // do nothing
    }

    @Override
    public void forget(RecorderContext recorderContext) {
        // do nothing
        script = null;
        scriptEngine = null;
    }
}
