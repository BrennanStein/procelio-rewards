import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.bootstrap.HttpRequester;
import org.apache.hc.core5.http.impl.bootstrap.RequesterBootstrap;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.util.Timeout;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

public class Mappings {
    public static class AccountResponse {
        public String code;
        public String message;
    }
    public HashMap<String, String> PatreonToProcelioMap;
    public HashMap<String, String> PatreonToContactMap;

    public HashMap<String, Long> PatreonToProcelioIDMap;
    public Mappings(Config cfg, String filePath) throws IOException, HttpException {
        FileInputStream file = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(file);

        PatreonToProcelioMap = new HashMap<>();
        PatreonToContactMap = new HashMap<>();
        Sheet sheet = workbook.getSheetAt(0);
        
        int i = 0;
        for (Row row : sheet) {
            if (i++ == 0)
                continue;
            String patreon = row.getCell(1).getStringCellValue();
            String procelio = row.getCell(2).getStringCellValue();
            String contact = (row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue());
            if (PatreonToProcelioMap.containsKey(patreon)) {
                System.err.println("Error: Conflicting Account Definitions: ");
                System.err.println("  " + patreon + ": " + procelio+" | " + contact);
                System.err.println("  " + patreon + ": " + PatreonToProcelioMap.get(patreon) +" | " + PatreonToContactMap.get(patreon));
                throw new RuntimeException("Fault");
            }

            PatreonToProcelioMap.put(patreon, procelio);
            PatreonToContactMap.put(patreon, contact);
        }
        workbook.close();
        PatreonToProcelioIDMap = new HashMap<>();
        HttpCoreContext coreContext = HttpCoreContext.create();
        HttpHost host = new HttpHost(cfg.ProcelioServerHost);
        HttpRequester httpRequester = RequesterBootstrap.bootstrap().create();

        Gson g = new Gson();
        for (Map.Entry<String, String> account : PatreonToProcelioMap.entrySet()) {
            URL myURL = new URL(cfg.ProcelioServerURL + "reverse/" + account.getValue());
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            
            String auth = "Bearer " + cfg.ProcelioServerToken;
            
            myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            myURLConnection.setRequestProperty ("Authorization", auth);
            myURLConnection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                  
            String body = in.readLine();
            in.close();
            AccountResponse resp = g.fromJson(body, AccountResponse.class);
            PatreonToProcelioIDMap.put(account.getKey(), Long.parseLong(resp.message));
        }
    }
    public String ProcelioServerURL;
    public String ProcelioServerToken;

    public String PatreonCampaignName;
    public String PatreonClientID;
    public String PatreonClientSecret;
}