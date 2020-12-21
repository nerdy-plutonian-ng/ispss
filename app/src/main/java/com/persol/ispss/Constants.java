package com.persol.ispss;

import java.util.ArrayList;

public class Constants {

    public static final String DOMAIN = "https://demo.persol-apps.com/PISPSS-MembersAPI/api/";
    public static final String DOMAIN_ADMIN = "https://collect.localrevenue-gh.com/ISPSSAdminAPI/api/";
    public static final String REGISTER = "Members/Register";
    public static final String SCHEMES_LIST = "mobile/companies";
    public static final String ADD_SCHEME = "SchemeAccounts/Add";
    public static final String GET_BENEFICIARIES = "Beneficiaries/";
    public static final String PAY_URL_TEST = "https://test.interpayafrica.com/interapi/ProcessPayment";
    public static final String PAY_CONTRIBUTION = "Contributions/Create";
    public static final String LOGIN = "Logins/Create/";
    public static final String LOGIN_CODE = "Logins/Verify/";
    public static final String DATABASE_NAME = "ISPSS";
    public static final int DATABASE_VERSION = 1;
    public static final String ADD_CRITICAL_INFO = "SchemeAccounts/Create";
    public static final String BECOME_A_MEMBER = "Registrations/BecomeMember";
    public static final String CONTRIBUTORS_SCHEMES = "SchemeAccounts/";
    public static final String APP_ID = "7453014357";
    public static final String APP_KEY = "57217937";
    public static final String APP_ID_TEST = "2452016059";
    public static final String APP_KEY_TEST = "70964183";
    public static final String PAY_URL_LIVE = "https://interpayafrica.com/interapi/ProcessPayment";
    public static final String CONTRIBUTE_INVOICE = "Contributions/Create";
    public static final String EMERGENT_REDIRECT = "https://collect.localrevenue-gh.com/ispssfundmanager/Transaction/Payment";
    public static final String GENERATE_PAYMENT_INVOICE = "Payments/Create";
    public static final String GENERATE_CONTRIBUTION_INVOICE = "Contributions/Create";
    public static final String GET_A_MEMBER = "Members/GetByMemberID?memberID=";
    public static final String GET_ALL_SCHEMES = "Externals/Schemes";
    public static final String GET_ALL_RELATIONSHIPS = "Generics/GetGenericsByType/REL";
    public static final String GET_BANK_ACCOUNT_TYPES = "Generics/GetGenericsByType/BAT";
    public static final String GET_NETWORK_PROVIDERS = "Generics/GetGenericsByType/MNP";
    public static final String ADD_A_MEMBER = "Registrations/Add";
    public static final String UPDATE_MMC = "Members/UpdateMMC";
    public static final String UPDATE_MOMO_ACCOUNT = "MomoAccounts/Update";
    public static final String UPDATE_BANK_ACCOUNT = "BankAccounts/Update";
    public static final String GET_ALL_BENEFICIARIES = "Beneficiaries/";
    public static final String ADD_NEW_BENEFICIARY = "Beneficiaries/Create/";
    public static final String UPDATE_BENEFICIARY = "Beneficiaries/Update";
    public static final String REMOVE_BENEFICIARY = "Beneficiaries/Remove/";
    public static final String WITHDRAWAL = "Withdrawals/PlaceRequest";
    public static final String UPDATE_SCHEME_CONFIG = "SchemeAccounts/Update";
    public static final String DELETE_BENEFICIARY = "Beneficiaries/Remove/";

    public static final String NAME = "NAME";
    public static final String JOINED = "JOINED";

    public static final String USER_TABLE = "USERTABLE";
    public static  ArrayList<Relationship> RelationshipsGlobal = new ArrayList<>();
    public static  ArrayList<NetworkProvider> NetworkProvidersGlobal = new ArrayList<>();
    public static  ArrayList<Scheme> SchemesGlobal = new ArrayList<>();
    public static  ArrayList<BankAccountType> BankAccountTypesGlobal = new ArrayList<>();
    public static Beneficiary[] Beneficiaries;
    public static final String ISPSS = "ISPSS";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_NO_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    //DB
    public static final String DB_NAME = "ISPSS_DB";
    public static final int DB_VERSION = 1;
    public static final String PERSONAL_TABLE = "PERSONAL";
    public static final String FIRST_NAME = "FIRSTNAME";
    public static final String MIDDLE_NAME = "MIDDLENAME";
    public static final String LAST_NAME = "LASTNAME";
    public static final String GENDER = "GENDER";
    public static final String DATE = "DATE";
    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";
    public static final String USER = "USER";
    public static final String RESIDENCE = "RESIDENCE";
    public static final String OCCUPATION = "OCCUPATION";
    public static final String HOMETOWN = "HOMETOWN";
    public static final String BENEFICIARIES_TABLE = "BENEFICIARIES";
    public static final String ID = "_id";
    public static final String RELATIONSHIP = "RELATIONSHIP";
    public static final String PERCENTAGE = "PERCENTAGE";
    public static final String BANK_TABLE = "BANK";
    public static final String ACCOUNT_NAME = "ACCOUNTNAME";
    public static final String ACCOUNT_NUMBER = "ACCOUNTNUMBER";
    public static final String ACCOUNT_TYPE = "ACCOUNTTYPE";
    public static final String BANK_NAME = "BANKNAME";
    public static final String BANK_BRANCH = "BANKBRANCH";
    public static final String FUND_MEDIUM = "FUNDMEDIUM";
    public static final String MOMO_TABLE = "MOMO";
    public static final String PROVIDER= "PROVIDER";
    public static final String MMC = "MMC";
    public static final String SCHEMES_TABLE = "SCHEMES";
    public static final String APPR = "APPR";
    public static final String USER_CODE = "USERCODE";
    public static final String BANK_CODE = "BANKCODE";
    public static final String MOMO_CODE = "MOMOCODE";
    public static final int USER_CODE_VALUE = 111666;
    public static final int USER_BANK_VALUE = 111667;
    public static final int USER_MOMO_VALUE = 111668;

    public static final String PKID = "PKID";

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;

    public static String[] Months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

}