package mz.skybill.ussd.parking.services;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.models.*;
import mz.skybill.ussd.parking.repos.SessionLogRepository;
import mz.skybill.ussd.parking.repos.SessionRepository;
import mz.skybill.ussd.parking.utils.Config;
import mz.skybill.ussd.parking.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionLogRepository logRepository;
    @Autowired
    private NetworkService networkService;
    @Autowired
    private Config config;
    @Autowired
    private MatolaService matolaService;

    private static Logger log = LoggerFactory.getLogger(CustomerService.class);

//    public CustomerProductListModel getCustomerProductList(String filterBy,Session session) {
//        List<CustomerProductListModel> customerProductLists = new ArrayList<>();
//        if (session != null)
//            customerProductLists = matolaService.getCustomerProduct(,session).getData();
//        ;
//        CustomerProductListModel productListModel = null;
//        if (!customerProductLists.isEmpty()) {
//            productListModel = customerProductLists.stream().filter(customerProductListModel -> customerProductListModel.getCategory().getName().equals(filterBy)).findFirst().orElse(null);
//
//        }
//        return productListModel;
//    }

    public List<CustomerProduct> getCustomerProductsBasedOnProduct(String filterBy, Session session) {
        List<CustomerProduct> customerProduct = new ArrayList<>();
        if (session != null) {
            int productId = matolaService.getAllProducts(session).getData().stream()
                    .filter(productModel -> productModel.getName().equalsIgnoreCase(filterBy)).mapToInt(productModel -> productModel.getId()).findFirst().orElse(0);
            if (productId != 0)
                customerProduct = matolaService.getCustomerProduct(productId, session).getData();
        }
        return customerProduct;
    }

    public Item registerBeneficiary(String phoneNumber, String idNumber, String firstName, String lastName, Session session) {
        Item item = new Item();
        CustomerModel customerModel = matolaService.registerUser(phoneNumber, idNumber, firstName, lastName, null);
        if (customerModel != null) {
            int beneficiaryId = customerModel.getId();
            int parentId = 5;
            item = matolaService.registerBeneficiary(beneficiaryId, parentId, 0, session);
        }

        return item;
    }

    public UserModel getUserProfile(Session session) {
        UserModel userModel = null;
        userModel = matolaService.getUser(session);
        return userModel;
    }

    public CustomerProduct getCustomerProduct(String parent, String filterBy, Session session) {
        List<CustomerProduct> customerProducts = new ArrayList<>();
        String standBy = filterBy.substring(filterBy.lastIndexOf("-") + 1);

        CustomerProduct x = new CustomerProduct();
        if (session != null) {
            int parent_productId = matolaService.getAllProducts(session).getData().stream().filter(productModel -> productModel.getName().equalsIgnoreCase(parent))
                    .mapToInt(productModel -> productModel.getId()).findFirst().orElse(0);
            if (parent_productId != 0) {
                customerProducts = matolaService.getCustomerProduct(parent_productId, session).getData();
                if (!customerProducts.isEmpty()) {

                    Optional<CustomerProduct> customerProduct = customerProducts.stream().filter(cp -> cp.getSerial() != null && cp.getSerial().equalsIgnoreCase(filterBy.split("-")[1])).findFirst();
                    if (customerProduct.isPresent())
                        x = customerProduct.get();


                }
            }
        }

        return x;

    }

    public StatusResponseData getBillPresentment(IpaBeneficiaryOption ipaBeneficiaryOption, CustomerProduct customerProduct, String parentProduct, Session session) {
        StatusResponseData statusResponseData = null;
        int payment_productId = 0;
        int lastIndex = 0;
        int parent_productId = matolaService.getAllProducts(session).getData().stream()
                .filter(productModel -> productModel.getName().equalsIgnoreCase(parentProduct)).mapToInt(productModel -> productModel.getId()).findFirst().orElse(0);
        if (parent_productId != 0)
            lastIndex = Integer.valueOf(session.getSessionInputs().get(session.getSessionInputs().size() - 1));
        if (parent_productId == 1)
            payment_productId = matolaService.getPaymentPeriods(session, parent_productId).getData().get(0).getId();
        else
            payment_productId = matolaService.getPaymentPeriods(session, parent_productId).getData().get(lastIndex - 1).getId();

        statusResponseData = matolaService.getBillPresentment(ipaBeneficiaryOption, customerProduct, payment_productId, session);


        return statusResponseData;
    }

    public Integer getProductPaymentPeriod(Session session, List<String> inputs) {
        int payment_productId = 0;
        int lastIndex = 0;
        int parent_productId = matolaService.getAllProducts(session).getData().stream()
                .filter(productModel -> productModel.getName().equalsIgnoreCase(inputs.get(0))).mapToInt(productModel -> productModel.getId()).findFirst().orElse(0);
        if (parent_productId != 0)
            lastIndex = Integer.valueOf(session.getSessionInputs().get(3));
        if (parent_productId == 1)
            payment_productId = matolaService.getPaymentPeriods(session, parent_productId).getData().get(0).getId();
        else
            payment_productId = matolaService.getPaymentPeriods(session, parent_productId).getData().get(lastIndex - 1).getId();

        return parent_productId;

    }



    /*

    public StatusResponseData renewForOthers(String serial,Session session){
        StatusResponseData item = null;
        if (serial != null){
            CustomerProduct customerProduct =  matolaService.checkRenewalProfile(serial,session).getData();
            if (customerProduct != null){
                int payment_productId = customerService.getProductPaymentPeriod(session,data);
                item = matolaService.taxPaymentEnforcement(null,customerProduct,session,);
            }
        }

        return item;
    }

     */

    public IpaBeneficiaryOption getBeneficiaries(String filterBy, Session session) {
        IpaBeneficiaryOption ipaBeneficiaryOption = new IpaBeneficiaryOption();
        List<BeneficiaryModel> beneficiaryModels = null;
        List<Integer> beneficiaries = new ArrayList<>();
        int ipaOption = Constants.IpaOptions.SELF;
        beneficiaryModels = matolaService.getCustomersBeneficiary(session).getData();
        if (!filterBy.equals(Constants.Beneficiaries.PERSONAL)) {
            beneficiaries = beneficiaryModels.stream().map(beneficiaryModel -> beneficiaryModel.getCustomer().getId()).collect(Collectors.toList());
            if (filterBy.equals(Constants.Beneficiaries.BENEFICIARIES))
                ipaOption = Constants.IpaOptions.BENEFICIARIES;
            else
                ipaOption = Constants.IpaOptions.ALL;

        }


        ipaBeneficiaryOption.setIpaOption(ipaOption);
        ipaBeneficiaryOption.setBeneficiaries(beneficiaries);

        return ipaBeneficiaryOption;
    }


    public IpaBeneficiaryOption getBillPresentmentBeneficiary(Session session) {
        String lastIndex = session.getSessionInputs().get(session.getSessionInputs().size() - 1);
        String filterBy = null;
        if (lastIndex.equalsIgnoreCase("1")) {
            filterBy = Constants.Beneficiaries.PERSONAL;
        } else if (lastIndex.equalsIgnoreCase("2")) {
            filterBy = Constants.Beneficiaries.BENEFICIARIES;
        } else {
            filterBy = Constants.Beneficiaries.ALL;
        }
        IpaBeneficiaryOption ipaBeneficiaryOption = getBeneficiaries(filterBy, session);

        return ipaBeneficiaryOption;
    }


    public TaxExemptionModel getTaxExemption(String filterBy, Session session) {

        TaxExemptionModel taxExemptionModel = null;
        TaxExemptionResponseData taxExemptionResponseData = matolaService.getExemptions(session);
        List<TaxExemptionModel> taxExemptionModels = new ArrayList<>();
        if (taxExemptionResponseData != null && !taxExemptionResponseData.getData().isEmpty()) {
            taxExemptionModels = taxExemptionResponseData.getData();

            taxExemptionModel = taxExemptionModels.stream().filter(taxExemptionModel1 -> taxExemptionModel1.getName().equals(filterBy)).findFirst().orElse(null);
        }

        log.info("TaXExemptionModel: {}", taxExemptionModel);
        return taxExemptionModel;
    }


}
