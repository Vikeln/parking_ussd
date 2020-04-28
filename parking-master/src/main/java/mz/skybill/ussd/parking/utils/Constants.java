package mz.skybill.ussd.parking.utils;

public class Constants {

    public interface Parking{
        String DAILY = "Daily Parking";
        String SEASONAL = "Seasonal Parking";
        String Clamping = "Clamping";
    }
    public interface ParkingType{
        String Residential = "Residential";
        String Commercial = "Commercial";
        String Seasonal = "Seasonal";
    }
    public interface DailyOptions{
        String Full = "Full Day";
        String Hourly = "Hourly";
    }


    public interface SerialProducts {
        String IPASERIAL = "IPA";
        String MARKETSERIAL = "MKT";
    }

    public interface Seasons{
        String DAILY = "Daily";
        String MONTHLY = "Monthly";
        String SEMIANUALLY = "Semi-Annually";
        String ANNUALLY = "Annually";
    }
    public interface Season{
        String MONTHLY = "Monthly";
        String Annual = "Annual";
        String Seasonal = "Seasonal";
    }


    public interface Fields {
        String NAME = "NAME";
        String MARkET_STRUCTURE = "Market Structure";
    }

    public interface CustomerType {
        String Individual = "1";
        String Company = "2";
    }

    public interface Beneficiaries {
        String PERSONAL = "Self";
        String BENEFICIARIES = "Beneficiaries";
        String ALL = "Self and Beneficiaries";
    }

    public interface Exceptions {
        String BENEFICIARIES = "beneficiary(ies)";
        String MARKETS = "Markets";
        String PERMITS = "business permits";
        String PRODUCTS = "Products";
    }

    public interface PaymentPeriod {
        String DAILY = "Daily";
        String MONTHLY = "Monthly";
        String ANNUALLY = "Annually";
        String SEMIANNUALLY = "Semi-Annually";
    }

    public enum Status {
        SERVER_ERROR(500, "Connection Timed out. Please try again");

        public final int code;
        private final String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }


    }

    public interface IpaOptions {
        Integer SELF = 1;
        Integer ALL = 2;
        Integer BENEFICIARIES = 3;

    }


}
