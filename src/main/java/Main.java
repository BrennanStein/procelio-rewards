import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.hc.core5.http.HttpException;

import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) throws IOException, HttpException {
        Gson g = new Gson();
        String content = Files.readString(Path.of("cfg.json"));
        Config cfg = g.fromJson(content, Config.class);

        System.out.println(cfg.ProcelioServerURL);

        Mappings m = new Mappings(cfg, "ProcelioPatreonForm.xlsx");
        for (Map.Entry<String, Long> info : m.PatreonToProcelioIDMap.entrySet()) {
            System.out.println(info.getKey()+" :: " + info.getValue());
        }
    }
}
