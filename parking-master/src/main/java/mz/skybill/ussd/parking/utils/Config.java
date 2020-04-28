package mz.skybill.ussd.parking.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Config {

    @Value("${customer.service}")
    private String customerService;
    @Value("${account.service}")
    private String accountService;
    @Value("${matola.app.key}")
    private String appKey;
    @Value("${file.path}")
    private String filePath;


}

