package lb.census.record;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by philippeschottey on 05/03/2017.
 */
public class FilesLogSet extends LogSet {

    private List<File> logFiles;

    public List<File> getLogFiles() {
        return logFiles;
    }

    public void setLogFiles(List<File> logFiles) {
        this.logFiles = logFiles;
    }


    @Override
    public List<Stream<String>> getLogData() {
        return getLogFiles().stream().map(f -> getStreams(f)).collect(Collectors.toList());
    }

    private Stream<String> getStreams(File file) {
        try {
            return Files.lines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
