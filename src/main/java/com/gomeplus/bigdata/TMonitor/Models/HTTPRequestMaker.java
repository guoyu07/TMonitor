package com.gomeplus.bigdata.TMonitor.Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class HTTPRequestMaker {
    @Getter @Setter private String requestURL;
    @Getter @Setter private String requestMethod;
    @Getter @Setter private List<List<String>> requestHeaders;
    @Getter @Setter private String requestBody;

    private static List<List<String>> parseHeaders(String rawString) {
        // todo: validate the total length of request headers
        if (rawString == null)
            return null;

        List<List<String>> headers = new ArrayList<List<String>>();
        for (String subString: rawString.split("&")) {
            String[] tokens = subString.split("=", 2);
            if (tokens.length != 2 ||
                    tokens[0].trim().length() == 0 ||
                    tokens[1].trim().length() == 0)
                continue;
            List<String> header = new ArrayList<String>();
            header.add(tokens[0]);
            header.add(tokens[1]);
            headers.add(header);
        }
        return headers.size() > 0 ? headers : null;
    }

    public static HTTPRequestMaker fromMap(Map<String, Object> map) {
        HTTPRequestMaker httpRequestMaker = new HTTPRequestMaker();

        if (map.get("requestURL") != null)
            httpRequestMaker.setRequestURL((String) map.get("requestURL"));
        else
            throw new RuntimeException("requestURL must exist");

        httpRequestMaker.setRequestMethod("GET");
        if (map.get("requestMethod") != null)
            //todo: validate request method
            httpRequestMaker.setRequestMethod((String) map.get("requestMethod"));

        if (map.get("requestHeaders") != null)
            httpRequestMaker.setRequestHeaders(parseHeaders(
                    (String) map.get("requestHeaders")));

        if (map.get("requestBody") != null)
            httpRequestMaker.setRequestBody((String) map.get("requestBody"));

        return httpRequestMaker;
    }
}
