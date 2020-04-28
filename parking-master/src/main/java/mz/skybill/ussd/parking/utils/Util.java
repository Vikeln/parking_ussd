package mz.skybill.ussd.parking.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import mz.skybill.ussd.parking.models.ItemResponse;
import mz.skybill.ussd.parking.models.Status;
import mz.skybill.ussd.parking.models.StatusExtended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Util<T> {
    public static Logger log = LoggerFactory.getLogger(USSDMenu.class);


    public static final String APP_KEY = "App-Key";
    public static final String IS_MERCHANT = "Merchant";

    public static PublicKey getPublicKey(String filename) {
        PublicKey publicKey = null;
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static String getDateString(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }


    public static ResponseEntity getResponse(Status status) {
        return getResponse(status, null);
    }

    public static ResponseEntity getResponse(Status status, Object entity) {
        ResponseEntity.BodyBuilder builder;
        if (status != null && status.getCode() == Response.ERROR_INVALID_ACCESS.status().getCode()) {
            builder = ResponseEntity.status(HttpStatus.FORBIDDEN);
        } else {
            builder = (status == null || status.getCode() == Response.SUCCESS.code) ? ResponseEntity.ok() : ResponseEntity.badRequest();
        }
        return builder.body(status.getCode() == Response.SUCCESS.status().getCode() ? (entity != null ? entity : status) : status);
    }

    public ItemResponse<T> getResponse(ResponseEntity<String> responseEntity, Class<T> clazz) {
        Status status;
        if (responseEntity.getBody() != null && !responseEntity.getBody().isEmpty()) {
            if (responseEntity.getStatusCodeValue() == 200) {
                T item = fromJson(responseEntity.getBody(), clazz);
                status = Util.Response.SUCCESS.status();
                return new ItemResponse<>(status, item);
            } else {
                StatusExtended statusExtended = new Util<StatusExtended>().fromJson(responseEntity.getBody(), StatusExtended.class);
                if (statusExtended.getStatus() != null) {
                    status = new Status(statusExtended.getStatus(), statusExtended.getMessage());
                } else {
                    status = new Status(statusExtended.getCode(), statusExtended.getMessage());
                    if (statusExtended.getErrors() != null && !statusExtended.getErrors().isEmpty()) {
                        StringBuilder message = new StringBuilder();
                        message.append(status.getMessage()).append("\n");
                        int i = 1;
                        for (Status error : statusExtended.getErrors()) {
                            message.append(i).append(") ").append(error.getMessage()).append("\n");
                            i++;
                        }
                        status.setMessage(message.toString());
                    }
                }
                return new ItemResponse<>(status, null);
            }
        } else {
            status = Response.INVALID_CREDENTIALS.status();
            return new ItemResponse<>(status, null);
        }
    }


    public static String toJson(Object entity) {
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // StdDateFormat is ISO8601 since jackson 2.9
        mapper.setDateFormat(new StdDateFormat().withTimeZone(TimeZone.getTimeZone("EAT")));
        try {
            json = mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static String toFormData(HashMap<String, String> data) {
        List<String> params = new ArrayList<>();
        try {
            if (data != null) {
                for (String key : data.keySet()) {
                    String value = data.get(key);
                    if (value != null) {
                        params.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.join("&", params);
    }

    public static String prependText(String preText, List<String> inputs, String postText) {
        String start = "";
        for (String input : inputs) {
            start = start + input;
        }

        if (!inputs.isEmpty())
            start = preText + start;

        log.info("Returned_start: {}", start);
        return start + postText;

    }

    public T fromJson(String json, TypeReference<T> type) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public T fromJson(String json, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date getDayOnlyDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public enum Response {
        SUCCESS(0, "Success"),
        ERROR_PHONE_NUMBER(9, "Invalid Phone number"),
        ERROR_ACTIVATION_REQUIRED(10, "Activation required"),
        ERROR_UNREGISTERED_USER(11, "UNREGISTERED: User has no account"),
        ERROR_INVALID_CODE(12, "Invalid verification code"),
        ERROR_EXPIRED_CODE(13, "Code has expired. Please request for another code"),
        ERROR_INVALID_ACCESS(14, "Invalid access"),
        ERROR_AUTH_ID_MISSING(15, "AUTH_ID field is required"),
        FIELD_REQUIRED(16, "Field `{0}` is required"),
        INVALID_CREDENTIALS(17, "Invalid PIN");
        //  CANCELLED(18, Translator.toLocale("cancelled"));
        private final int code;
        private final String message;

        Response(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public Status status() {
            return new Status(code, message);
        }
    }

    public interface UserType {
        String EMPLOYEE = "EMP";
        String USER = "USR";
    }

    public static List<String> changeIntToStrings(int firstIndex, int lastIndex) {
        List<String> inputs = new ArrayList<>();
        for (int i = firstIndex; i <= lastIndex; i++) {
            inputs.add(String.valueOf(i));
        }
        return inputs;
    }

    public T fromObject(Class<T> type, Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.convertValue(object, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
