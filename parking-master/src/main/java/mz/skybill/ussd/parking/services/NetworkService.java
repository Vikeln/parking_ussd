package mz.skybill.ussd.parking.services;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.utils.Config;
import mz.skybill.ussd.parking.utils.Util;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class NetworkService {
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String baseUrl = "url";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NetworkService.class);

    @Autowired
    Config config;

    public HashMap<String, Object> getHeaders(Session session) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + session.getAccessToken());
        headers.put("App-Key", config.getAppKey());
        return headers;
    }

    public HashMap<String, Object> getAppKey(Session session) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("App-Key", config.getAppKey());
        return headers;
    }


    public ResponseEntity<String> getData(HashMap<String, Object> data, HashMap<String, String> headersMap, String urlPart) {
        return sendData(urlPart, HttpMethod.GET, data, headersMap);
    }

    public ResponseEntity<String> getData(HashMap<String, String> headersMap, String urlPart) {
        return sendData(urlPart, HttpMethod.GET, null, headersMap);
    }

    public ResponseEntity<String> postData(HashMap<String, Object> data, HashMap<String, String> headersMap, String urlPart) {
        return sendData(urlPart, HttpMethod.POST, data, headersMap);
    }

    public ResponseEntity<String> postData(Object data, HashMap<String, String> headersMap, String urlPart) {
        return sendData(urlPart, HttpMethod.POST, data, headersMap);
    }

    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<String> sendData(String urlPart, HttpMethod method, Object data, HashMap<String, String> headersMap) {
        RestTemplate restTemplate = new RestTemplate();

        String url = !urlPart.startsWith("http") ? baseUrl + urlPart : urlPart;

        disableSSLCertificateChecking();

        if (method.equals(HttpMethod.GET) && data != null) {
            List<String> dataList = new ArrayList<>();
            if (data instanceof HashMap) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
                for (String key : dataMap.keySet()) {
                    try {
                        Object value = dataMap.get(key);
                        if (value != null) {
                            dataList.add(String.format("%s=%s", key, URLEncoder.encode(value.toString(), "UTF-8")));
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                url = url + "?" + String.join("&", dataList);
            }
        }


        log.info("URL: {} {} ", method, url);

        HttpHeaders headers = new HttpHeaders();
        if (headersMap != null) {
            for (String key : headersMap.keySet()) {
                headers.set(key, headersMap.get(key));
            }
            log.info("Headers: {}", Util.toJson(headers));
        }

        if (data != null) {
            log.info("Data: {}", Util.toJson(data));
        }

        HttpEntity entity = new HttpEntity<>(data, headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(url, method, entity, String.class);
        } catch (final HttpClientErrorException e) {
            result = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
        log.info("{} : {}", result.getStatusCodeValue(), result.getBody());
        return result;
    }
}