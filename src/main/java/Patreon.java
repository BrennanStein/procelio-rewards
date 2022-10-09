import java.util.HashMap;
import java.util.Map;
import com.patreon.PatreonAPI;
import com.patreon.PatreonOAuth;
import com.patreon.PatreonOAuth;
import com.patreon.resources.User;
import com.patreon.resources.Pledge;

public class Patreon {
    public HashMap<Long, Long> ProcelioIDToTotalCents;
    public Patreon(Config cfg, Mappings map) {
        ProcelioIDToTotalCents = new HashMap<>();
        PatreonOAuth oauthClient = new PatreonOAuth(cfg.PatreonClientID, cfg.PatreonClientSecret, redirectUri);
        PatreonOAuth.TokensResponse tokens = oauthClient.getTokens(code);
        //Store the refresh TokensResponse in your data store
        String accessToken = tokens.getAccessToken();
        
        for (Map.Entry<String, Long> entry : map.PatreonToProcelioIDMap.entrySet()) {

        }
    }
}