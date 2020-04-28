package mz.skybill.maputo.USSD.utils;

public class Constants {

    public interface MatolaProducts{
        String IPA = "IPA";
        String IPAALT = "IPA";
        String IPRA ="IPRA";
        String TAE = "TAE";
//        String MARKETS = "Personal Tax";
        String MARKETS = "MARKET TAX";
    }

    public interface SerialProducts{
        String IPASERIAL= "IPA";
        String MARKETSERIAL= "MKT";
    }

    public interface Fields{
        String NAME= "NAME";
        String MARkET_STRUCTURE="Market Structure";
    }

    public interface CustomerType{
        String Individual = "1";
        String Company = "2";
    }

    public interface Beneficiaries{
        String PERSONAL="Self";
        String BENEFICIARIES = "Beneficiaries";
        String ALL = "Self and Beneficiaries";
    }

    public interface Exceptions{
        String BENEFICIARIES="beneficiary(ies)";
        String MARKETS="Markets";
        String PERMITS="business permits";
        String PRODUCTS="Products";
    }

    public interface PaymentPeriod{
        String DAILY="Daily";
        String MONTHLY="Monthly";
        String ANNUALLY="Annually";
        String SEMIANNUALLY="Semi-Annually";
    }

    public enum Status{
        SERVER_ERROR(500,"Connection Timed out. Please try again");

        public final int code;
        private final String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public interface IpaOptions{
        Integer SELF = 1;
        Integer ALL = 2;
        Integer BENEFICIARIES = 3;

    }


}
