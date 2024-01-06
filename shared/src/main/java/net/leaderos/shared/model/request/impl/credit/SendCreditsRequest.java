package net.leaderos.shared.model.request.impl.credit;

import net.leaderos.shared.model.request.PostRequest;

import java.io.IOException;
import java.util.HashMap;

public class SendCreditsRequest extends PostRequest {
    public SendCreditsRequest(String sender, String target, double amount) throws IOException {
        super("credits/" + sender + "/send", new HashMap<String, String>() {{
            put("target", target);
            put("amount", String.valueOf(amount));
        }});
    }
}
