package mz.skybill.ussd.parking.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.models.*;
import mz.skybill.ussd.parking.utils.Config;
import mz.skybill.ussd.parking.utils.Constants;
import mz.skybill.ussd.parking.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MatolaService {

    Logger log = LoggerFactory.getLogger(MatolaService.class);

    @Autowired
    private NetworkService networkService;
    @Autowired
    Config config;


    public UserModel getUser(Session session) {
        ClaimUserModel claimUserModel = null;
        final ObjectMapper mapper = new ObjectMapper();
        Claims claims = Jwts.parser()
                .setSigningKey(Util.getPublicKey(config.getFilePath() + "public_key.der"))
                .parseClaimsJws(session.getAccessToken())
                .getBody();

        claimUserModel = mapper.convertValue(claims, ClaimUserModel.class);

        return claimUserModel.getUser();

    }


    public HashMap<String, String> getHeaders(Session session) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + session.getAccessToken());
        headers.put("App-Key", config.getAppKey());
        return headers;
    }

    public HashMap<String, String> getAppKey() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("App-Key", config.getAppKey());
        return headers;
    }

    private HashMap<String, String> getHeaders(String token, String appKey) {
        HashMap<String, String> headers = new HashMap<>();
        if (token != null) {
            headers.put("Authorization", "Bearer " + token);
        }

        if (appKey != null) {
            headers.put("App-Key", appKey);
        }
        headers.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private HashMap<String, String> getHeaders(String token) {
        return getHeaders(token, null);
    }

    public ProductListResponseData getAllProducts(Session session) {
        ProductListResponseData productListResponseData = null;
        ResponseEntity<String> responseEntity = networkService.getData(getHeaders(session), config.getCustomerService() + "products");
        if (responseEntity.getStatusCodeValue() == 200) {
            productListResponseData = new Util<ProductListResponseData>().fromJson(responseEntity.getBody(), ProductListResponseData.class);
        }

        return productListResponseData;
    }


    public ProductCustomerResponseData findCustomerProducts(Session session) {
        ResponseEntity<String> responseEntity = networkService.getData(getHeaders(session), config.getCustomerService() + "customer/products/ussd");
        ProductCustomerResponseData itemResponse = null;
        if (responseEntity.getStatusCodeValue() == 200) {
            itemResponse = new Util<ProductCustomerResponseData>().fromJson(responseEntity.getBody(), ProductCustomerResponseData.class);
        }

        return itemResponse;
    }


    public ProductCustomerResponseData getCustomerProduct(int request, Session session) {
        ProductCustomerResponseData model = null;
        List<CustomerProduct> charges = new ArrayList<>();
        ResponseEntity<String> responseEntity =
                networkService.getData(getHeaders(session), config.getCustomerService() + "customer/products?customer=" + getUser(session).getAccountOwner().getId() + "&product=" + request);
        if (responseEntity.getStatusCodeValue() == 200) {
            model = new Util<ProductCustomerResponseData>().fromJson(responseEntity.getBody(), ProductCustomerResponseData.class);
            return model;
        }
        return model;
    }

//    public ProductCustomerResponseData getCustomerProduct(int productId, Session session) {
//        ResponseEntity<String> responseEntity =
//                networkService.getData(getHeaders(session), config.getCustomerService() + "customer/products?customer=" + getUser(session).getAccountOwner().getId() + "&product=" + productId);
//        ProductCustomerResponseData itemResponse = null;
//        if (responseEntity.getStatusCodeValue() == 200) {
//            itemResponse = new Util<ProductCustomerResponseData>().fromJson(responseEntity.getBody(), ProductCustomerResponseData.class);
//        }
//
//        return itemResponse;
//
//    }

    public String getAccessToken(String phoneNumber) {
        if (phoneNumber.startsWith(" 258")) {
            phoneNumber = "+" + phoneNumber.substring(1);
        }
        log.info("PHONE_NUMBER:{}", phoneNumber);
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", phoneNumber);
        String accessToken = null;
        ResponseEntity<String> responseEntity = networkService.postData(data, null, config.getAccountService() + "/auth");
        if (responseEntity.getStatusCodeValue() == 200) {
            TokenResponse tokenResponse = new Util<TokenResponse>().fromJson(responseEntity.getBody(), TokenResponse.class);
            accessToken = tokenResponse.getAccessToken();
        }
        return accessToken;
    }


    public CustomerModel registerUser(String phoneNumber, String idNumber, String firstName, String lastName, Session session) {
        CustomerModel customerModel = null;
        Status status;
        CustomerModelResponseData customerModelResponseData = null;
        HashMap<String, Object> data = new HashMap<>();
        data.put("idNumber", idNumber);
        data.put("firstName", firstName);   //firstName
        if (phoneNumber != null) {
            data.put("phoneNumber", phoneNumber);
        } else {
            data.put("phoneNumber", session.getMsisdn());
        }
        data.put("lastName", lastName);
        data.put("customerType", Integer.valueOf(Constants.CustomerType.Individual));
        ResponseEntity<String> responseEntity = networkService.postData(data, getAppKey(), config.getCustomerService() + "/customer/register");
        if (responseEntity.getStatusCodeValue() == 200) {
            customerModelResponseData = new Util<CustomerModelResponseData>().fromJson(responseEntity.getBody(), CustomerModelResponseData.class);
            customerModel = customerModelResponseData.getData();
        }
        return customerModel;
    }

    public Item registerBeneficiary(int customer, int parent, int id, Session session) {
        Item status = null;
        HashMap<String, Object> data = new HashMap<>();
        data.put("customer", customer);
        data.put("parent", parent);
        data.put("id", id);
        ResponseEntity<String> responseEntity = networkService.postData(data, getHeaders(session), config.getCustomerService() + "/beneficiaries");
        if (responseEntity.getStatusCodeValue() == 200) {
            status = new Util<Item>().fromJson(responseEntity.getBody(), Item.class);
        }

        return status;
    }

    public ReceiptResponse receiptParking(EnforceParkingRequest request, Session session) {
        ReceiptResponse model = null;

        ResponseEntity<String> responseEntity = networkService.postData(request, getHeaders(session), config.getCustomerService() + "/transact/receipt-parking");
        if (responseEntity.getStatusCodeValue() == 200) {
            model = new Util<ReceiptResponse>().fromJson(responseEntity.getBody(), ReceiptResponse.class);
        }
        return model;
    }

    public CustomerProductResponse registerVehicleParking(CustomerProductParkingRequest request, Session session) {
        CustomerProductResponse model = null;

        ResponseEntity<String> responseEntity = networkService.postData(request, getHeaders(session), config.getCustomerService() + "/enforcement/parking");
        if (responseEntity.getStatusCodeValue() == 200) {
            model = new Util<CustomerProductResponse>().fromJson(responseEntity.getBody(), CustomerProductResponse.class);
        }
        return model;
    }

    public ReceiptResponse enforceParking(EnforceParkingRequest request, Session session) {
        ReceiptResponse model = null;

        ResponseEntity<String> responseEntity = networkService.postData(request, getHeaders(session), config.getCustomerService() + "/transact/enforce-parking");
        if (responseEntity.getStatusCodeValue() == 200) {
            model = new Util<ReceiptResponse>().fromJson(responseEntity.getBody(), ReceiptResponse.class);
        }
        return model;
    }

    public List<CustomerProductChargeEntryModel> getCharges(String request, Session session) {
        ChargesResponse model = null;
        List<CustomerProductChargeEntryModel> charges = new ArrayList<>();

        ResponseEntity<String> responseEntity = networkService.getData(getHeaders(session), config.getCustomerService() + "/transact/penalty-bills?serial=" + request);
        if (responseEntity.getStatusCodeValue() == 200) {
            model = new Util<ChargesResponse>().fromJson(responseEntity.getBody(), ChargesResponse.class);
            charges = model.getData();
        }
        return charges;
    }

    public CustomerTaxExemptionResponseData requestExemption(CustomerProduct customerProduct, TaxExemptionModel taxExemptionModel, Session session) {

        CustomerTaxExemptionResponseData taxExemptionResponseData = null;
        HashMap<String, Object> data = new HashMap<>();
        data.put("exemption", taxExemptionModel.getId());
        data.put("id", getUser(session).getAccountOwner().getId());
        data.put("serial", customerProduct.getSerial());
        ResponseEntity<String> responseEntity = networkService.postData(data, getHeaders(session), config.getCustomerService() + "/customer-exemptions");
        if (responseEntity.getStatusCodeValue() == 200) {
            taxExemptionResponseData = new Util<CustomerTaxExemptionResponseData>().fromJson(responseEntity.getBody(), CustomerTaxExemptionResponseData.class);
        }
        return taxExemptionResponseData;
    }

    public StatusResponseData taxPaymentEnforcement(IpaBeneficiaryOption ipaBeneficiaryOption, CustomerProduct customerProduct, Session session, int productPaymentId) {
        HashMap<String, Object> data = new HashMap<>();
        StatusResponseData item = null;
        if (ipaBeneficiaryOption != null) {
            data.put("beneficiaries", ipaBeneficiaryOption.getBeneficiaries());
            data.put("ipaOptions", ipaBeneficiaryOption.getIpaOption());
        } else {
            data.put("beneficiaries", null);
            data.put("ipaOptions", 0);
        }
        data.put("additional", false);
        data.put("additionalGround", 0);
        data.put("paymentMethod", "MPESA");
        data.put("customer", getUser(session).getAccountOwner().getId());
        data.put("installationType", null);
        data.put("market", null);
        data.put("paymentPeriod", productPaymentId);
        data.put("product", null);
        data.put("serial", customerProduct.getSerial());
        data.put("structure", null);


        ResponseEntity<String> responseEntity = networkService.postData(data, getHeaders(session), config.getCustomerService() + "/transact/enforce");

        if (responseEntity.getStatusCodeValue() == 200) {
            item = new Util<StatusResponseData>().fromJson(responseEntity.getBody(), StatusResponseData.class);
        } else {
            item.setStatus(new Status(500, "Connection Time out. Please Try Again"));
        }

        return item;
    }

    public StatusResponseData getBillPresentment(IpaBeneficiaryOption ipaBeneficiaryOption, CustomerProduct customerProduct, int paymentPeriod, Session session) {
        HashMap<String, Object> data = new HashMap<>();
        StatusResponseData item = null;
        if (ipaBeneficiaryOption != null) {
            data.put("beneficiaries", ipaBeneficiaryOption.getBeneficiaries());
            data.put("ipaOptions", ipaBeneficiaryOption.getIpaOption());
        } else {
            data.put("beneficiaries", null);
            data.put("ipaOptions", 0);
        }
        data.put("additional", false);
        data.put("additionalGround", 0);
        data.put("paymentMethod", "MPESA");
        data.put("customer", getUser(session).getAccountOwner().getId());
        data.put("installationType", null);
        data.put("market", 0);
        data.put("paymentPeriod", paymentPeriod);
        data.put("product", customerProduct.getProduct().getId());
        data.put("serial", customerProduct.getSerial());
        data.put("structure", null);


        ResponseEntity<String> responseEntity = networkService.postData(data, getHeaders(session), config.getCustomerService() + "/transact/receipt");
        if (responseEntity.getStatusCodeValue() == 200) {
            item = new Util<StatusResponseData>().fromJson(responseEntity.getBody(), StatusResponseData.class);
        } else {
            item.setStatus(new Status(500, "Connection Time out. Please Try Again"));
        }


        return item;
    }

    public BeneficiaryModelResponseData getCustomersBeneficiary(Session session) {
        BeneficiaryModelResponseData beneficiaryPagedResponse = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/beneficiaries?customer=5");
        if (responseEntity.getStatusCodeValue() == 200) {
            beneficiaryPagedResponse = new Util<BeneficiaryModelResponseData>().fromJson(responseEntity.getBody(), BeneficiaryModelResponseData.class);
        }

        return beneficiaryPagedResponse;
    }


    public ProductProfileResponseData checkProductProfile(CustomerProduct customerProduct, Session session) {
        ProductProfileResponseData productProfileResponseData = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/transact/check-profile?serial=" + customerProduct.getSerial());
        if (responseEntity.getStatusCodeValue() == 200) {
            productProfileResponseData = new Util<ProductProfileResponseData>().fromJson(responseEntity.getBody(), ProductProfileResponseData.class);
        }
        return productProfileResponseData;
    }

    public CustomerProductResponseData checkRenewalProfile(String serial, Session session) {
        CustomerProductResponseData customerProduct = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/customer/products/serial/" + serial);
        if (responseEntity.getStatusCodeValue() == 200) {
            customerProduct = new Util<CustomerProductResponseData>().fromJson(responseEntity.getBody(), CustomerProductResponseData.class);

        }

        return customerProduct;
    }

    public BillVerificationResponseData verifyReceipt(String invoice, Session session) {
        BillVerificationResponseData billVerification = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/transact/verify-bill/" + invoice);
        if (responseEntity.getStatusCodeValue() == 200) {
            billVerification = new Util<BillVerificationResponseData>().fromJson(responseEntity.getBody(), BillVerificationResponseData.class);
        }


        return billVerification;

    }

    public PaymentPeriodListModel getPaginatedListOfPaymentPeriods(Session session) {
        PaymentPeriodListModel paymentPeriodListModel = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/payment-periods");
        if (responseEntity.getStatusCodeValue() == 200) {
            paymentPeriodListModel = new Util<PaymentPeriodListModel>().fromJson(responseEntity.getBody(), PaymentPeriodListModel.class);
        }
        return paymentPeriodListModel;
    }

    public PaymentPeriodResponseData getPaymentPeriods(Session session, int id) {
        PaymentPeriodResponseData paymentPeriodResponseData = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/product-payment-periods?product=" + id);
        if (responseEntity.getStatusCodeValue() == 200) {
            paymentPeriodResponseData = new Util<PaymentPeriodResponseData>().fromJson(responseEntity.getBody(), PaymentPeriodResponseData.class);
        }

        return paymentPeriodResponseData;
    }

    public TaxExemptionResponseData getExemptions(Session session) {
        TaxExemptionResponseData taxExemptionModel = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/exemptions");
        if (responseEntity.getStatusCodeValue() == 200) {
            taxExemptionModel = new Util<TaxExemptionResponseData>().fromJson(responseEntity.getBody(), TaxExemptionResponseData.class);

        }

        return taxExemptionModel;
    }

    public ProductExemptionResponseData getProductExemptions(Session session) {
        ProductExemptionResponseData productExemptionResponseData = null;
        ResponseEntity<String> responseEntity = networkService.getData(null, getHeaders(session), config.getCustomerService() + "/product-exemptions?product=" + 1);
        if (responseEntity.getStatusCodeValue() == 200) {
            productExemptionResponseData = new Util<ProductExemptionResponseData>().fromJson(responseEntity.getBody(), ProductExemptionResponseData.class);
        }

        return productExemptionResponseData;
    }

}