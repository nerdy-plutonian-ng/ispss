package com.persol.ispss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.persol.ispss.Constants.BankAccountTypesGlobal;
import static com.persol.ispss.Constants.Beneficiaries;
import static com.persol.ispss.Constants.DOMAIN;
import static com.persol.ispss.Constants.FavouritesGlobal;
import static com.persol.ispss.Constants.GET_ALL_BENEFICIARIES_WITH_SCHEMES;
import static com.persol.ispss.Constants.GET_ALL_RELATIONSHIPS;
import static com.persol.ispss.Constants.GET_ALL_SCHEMES;
import static com.persol.ispss.Constants.GET_BANK_ACCOUNT_TYPES;
import static com.persol.ispss.Constants.GET_BENEFICIARIES;
import static com.persol.ispss.Constants.GET_FAVOURITES;
import static com.persol.ispss.Constants.GET_ID_TYPES;
import static com.persol.ispss.Constants.GET_NETWORK_PROVIDERS;
import static com.persol.ispss.Constants.IDCardTypesGlobal;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.NetworkProvidersGlobal;
import static com.persol.ispss.Constants.REMOVE_BENEFICIARY;
import static com.persol.ispss.Constants.RelationshipsGlobal;
import static com.persol.ispss.Constants.SchemesGlobal;
import static com.persol.ispss.Constants.UPDATE_BENEFICIARY;

public class ISPSSManager {

    private Context context;
    private SharedPreferences sharedPref;
    private DialogFragment dialogFragment;

    public ISPSSManager(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
    }

    public void showDialog(DialogFragment newFragment, String tag){
        newFragment.setCancelable(false);
        newFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), tag);
    }

    public void cancelDialog(DialogFragment newFragment){
        newFragment.getDialog().cancel();
    }

    public String getContributorID(){
        return sharedPref.getString(context.getString(R.string.user_id),"");
    }

    public String greet(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay < 12){
            return "Good Morning,";
        }else if(timeOfDay < 16){
            return "Good Afternoon,";
        }else if(timeOfDay < 21){
            return "Good Evening,";
        }else{
            return "Good Night,";
        }
    }

    public boolean isFavFirstTime(){
        return sharedPref.getBoolean(context.getString(R.string.fav_first_time),true);
    }

    public void setFavFirstTime(){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.fav_first_time), false );
        editor.apply();
    }

    public String getUserName(){
        return sharedPref.getString(context.getString(R.string.name),"");
    }

    public String getFundMedium(){
        return sharedPref.getString(context.getString(R.string.momo),"").isEmpty() ? context.getString(R.string.bank) : context.getString(R.string.momo);
    }

    public void setMomoDetails(String momo){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.momo), momo );
        editor.putString(context.getString(R.string.bank),"");
        editor.apply();
    }

    public void setBankDetails(String bank){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.bank), bank );
        editor.putString(context.getString(R.string.momo),"");
        editor.apply();
    }

    public JSONObject getMomoDetails(){
        try {
            return new JSONObject(sharedPref.getString(context.getString(R.string.momo),""));
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONObject getBankDetails(){
        try {
            Log.d(ISPSS,sharedPref.getString(context.getString(R.string.bank),""));
            return new JSONObject(sharedPref.getString(context.getString(R.string.bank),""));
        } catch (JSONException e) {
            Log.d(ISPSS, "getBankDetails: "+e.toString());
            return null;
        }
    }

    public boolean addToFavourites(Favourite favourite){
        Log.d(ISPSS, "addToFavourites: "+favourite.toString());
        try {
            int exists = getFavouriteIndex(favourite);
            if(exists == -1){
                ArrayList<Favourite> favourites = getFavourites();
                favourites.add(favourite);
                JSONArray jsonArray = new JSONArray();
                for (Favourite fav : favourites){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",fav.getId());
                    jsonObject.put("name",fav.getName());
                    jsonArray.put(jsonObject);
                }
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(context.getString(R.string.favourites), jsonArray.toString());
                editor.apply();
                return true;
            } else {
                Toast.makeText(context, "Member already a favourite", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e){
            return false;
        }
    }

    public Favourite getFavourite(String id){
        try {
            ArrayList<Favourite> favs = getFavourites();
            for(Favourite fav : favs){
                if(id.equals(fav.getId())){
                    return fav;
                }
            }
            return null;
        } catch (Exception e) {
            Log.d(ISPSS, "getBankDetails: "+e.toString());
            return null;
        }
    }

    public int getFavouriteIndex(String id){
        try {
            ArrayList<Favourite> favs = getFavourites();
            int count = 0;
            for(Favourite fav : favs){
                if(id.equals(fav.getId())){
                    return count;
                }
                count++;
            }
            return -1;
        } catch (Exception e) {
            Log.d(ISPSS, "getBankDetails: "+e.toString());
            return -1;
        }
    }

    public int getFavouriteIndex(Favourite favourite){
        try {
            ArrayList<Favourite> favs = getFavourites();
            int count = 0;
            for(Favourite fav : favs){
                if(favourite.getId().equals(fav.getId())){
                    return count;
                }
                count++;
            }
            return -1;
        } catch (Exception e) {
            Log.d(ISPSS, "getBankDetails: "+e.toString());
            return -1;
        }
    }

    public ArrayList<Favourite> getFavourites(){
        return new ArrayList<>();
//        try {
//            JSONArray jsonArray = new JSONArray(sharedPref.getString(context.getString(R.string.favourites),""));
//            ArrayList<Favourite> favourites = new ArrayList<>();
//            for(int i = 0; i < jsonArray.length();i++){
//                Favourite favourite = new Favourite(jsonArray.getJSONObject(i).getString("id"),
//                        jsonArray.getJSONObject(i).getString("name"));
//                favourites.add(favourite);
//                Log.d(ISPSS, "getFavourites: " + favourite.toString());
//            }
//            return favourites;
//        } catch (JSONException e) {
//            return new ArrayList<>();
//        }
    }

    public boolean deleteFavourite(String id){
        try {
            int index = getFavouriteIndex(id);
            if(index == -1){
                Toast.makeText(context, "This favourite does not exist", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                ArrayList<Favourite> newFavs = new ArrayList<>();
                ArrayList<Favourite> favourites = getFavourites();
                int count = 0;
                for(Favourite favourite : favourites){
                    if(count != index){
                        newFavs.add(favourite);
                    }
                    count++;
                }
                JSONArray jsonArray = new JSONArray();
                for (Favourite fav : newFavs) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", fav.getId());
                    jsonObject.put("name", fav.getName());
                    jsonArray.put(jsonObject);
                }
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(context.getString(R.string.favourites), jsonArray.toString());
                editor.apply();
                return true;
            }
        } catch (Exception e){
            Log.d(ISPSS, "deleteFavourite: "+e.toString());
            return false;
        }
    }

    public String getUserPhone(){
        return sharedPref.getString(context.getString(R.string.phone),"");
    }

    public String getUserEmail(){
        return sharedPref.getString(context.getString(R.string.email),"Not set");
    }

    public String getUserResidence(){
        return sharedPref.getString(context.getString(R.string.residence),"Not set");
    }

    public double getMMC(){
        return sharedPref.getFloat("mmc", 0);
    }

    public int getUserType(){
        double mmc = sharedPref.getFloat("mmc", 0);
        Log.e("mmc",mmc+"");
        if(mmc > 0){
            return 1;
        }
        return 0;
    }

    public String[] getBeneficiariesNames(ArrayList<Beneficiary> beneficiaries){
        String[] names = new String[beneficiaries.size()];
        int count = 0;
        for(Beneficiary beneficiary : beneficiaries){
            names[count] = beneficiary.getFirstName() + " " + beneficiary.getLastName();
            count++;
        }
        return names;
    }

    public String[] getProviderNames(ArrayList<NetworkProvider> networkProviders){
        String[] names = new String[networkProviders.size()];
        int count = 0;
        for(NetworkProvider networkProvider : networkProviders){
            names[count] = networkProvider.getName();
            count++;
        }
        return names;
    }

    public String[] getAccountTypesNames(ArrayList<BankAccountType> bankAccountTypes){
        String[] names = new String[bankAccountTypes.size()];
        int count = 0;
        for(BankAccountType bankAccountType : bankAccountTypes){
            names[count] = bankAccountType.getName();
            count++;
        }
        return names;
    }

    public String[] getRelationshipNames(ArrayList<Relationship> relationshipArrayList){
        String[] names = new String[relationshipArrayList.size()];
        int count = 0;
        for(Relationship relationship : relationshipArrayList){
            names[count] = relationship.getName();
            count++;
        }
        return names;
    }

    public String[] getIdTypesNames(ArrayList<IDType> idTypeArrayList){
        String[] names = new String[idTypeArrayList.size()];
        int count = 0;
        for(IDType idType : idTypeArrayList){
            names[count] = idType.getName();
            count++;
        }
        return names;
    }

    public String[] getSchemeNames(ArrayList<Scheme> schemeArrayList){
        String[] names = new String[schemeArrayList.size()];
        int count = 0;
        for(Scheme scheme : schemeArrayList){
            names[count] = scheme.getName();
            count++;
        }
        return names;
    }

    public void logout(){
        // User chose the "Settings" item, show the app settings UI...
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void updateMMC(double mmc){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(context.getString(R.string.mmc), (float)mmc );
        editor.apply();
    }

    public void getGenericData(){
        DialogFragment dialogFragment = new Loader();
        showDialog(dialogFragment,"loader");
        getRelationships(dialogFragment);
    }

    public boolean beingADay(){
        Long currentDate = new Date().getTime();
        Long diff = currentDate - getLastSync();
        int days = (int) (diff / (1000*60*60*24));
        if(days >= 1){
            return true;
        } else {
            return false;
        }
    }

    public Long getLastSync(){
        return sharedPref.getLong(context.getString(R.string.last_sync),0L);
    }

    public void getSchemes(final DialogFragment dialogFragment){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ALL_SCHEMES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<Scheme> schemeArrayList = new ArrayList<>();
                            if(response.getInt("code") == 200){
                                JSONArray body = response.getJSONArray("body");
                                for(int i = 0;i < body.length();i++){
                                    JSONObject data = body.getJSONObject(i);
                                    if(Integer.parseInt(data.getString("status")) == 1){
                                        Scheme scheme = new Scheme(data.getString("id"),
                                                data.getString("name"));
                                        schemeArrayList.add(scheme);
                                    }
                                }
                                SchemesGlobal = schemeArrayList;
                                Log.e("debug","SchemeNo : "+SchemesGlobal.size());
                                getAccountTypes(dialogFragment);
                            }
                        } catch (JSONException e) {
                            cancelDialog(dialogFragment);
                            Log.e("debug",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                        Log.e("debug",error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void getRelationships(final DialogFragment dialogFragment){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ALL_RELATIONSHIPS,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<Relationship> relationships = new ArrayList<>();
                            if(response.getInt("code") == 0){
                                JSONArray body = response.getJSONArray("body");
                                for(int i = 0;i < body.length();i++){
                                    JSONObject data = body.getJSONObject(i);
                                    if(data.getString("status").equals("ON")){
                                        Relationship relationship = new Relationship(data.getString("id"),
                                                data.getString("description"));
                                        relationships.add(relationship);
                                    }
                                }
                                RelationshipsGlobal = relationships;
                                Log.e("debug","relNo : "+RelationshipsGlobal.size());
                                getSchemes(dialogFragment);
                            }

                        } catch (JSONException e) {
                            cancelDialog(dialogFragment);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void getAccountTypes(final DialogFragment dialogFragment){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_BANK_ACCOUNT_TYPES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<BankAccountType> bankAccountTypes = new ArrayList<>();
                            if(response.getInt("code") == 0){
                                JSONArray body = response.getJSONArray("body");
                                for(int i = 0;i < body.length();i++){
                                    JSONObject data = body.getJSONObject(i);
                                    if(data.getString("status").equals("ON")){
                                        BankAccountType bankAccountType = new BankAccountType(data.getString("id"),
                                                data.getString("description"));
                                        bankAccountTypes.add(bankAccountType);
                                    }
                                }
                                BankAccountTypesGlobal = bankAccountTypes;
                                Log.e("debug","acctTypes : "+BankAccountTypesGlobal.size());
                                getNetworkProviders(dialogFragment);
                            }

                        } catch (JSONException e) {
                            cancelDialog(dialogFragment);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void getNetworkProviders(final DialogFragment dialogFragment){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_NETWORK_PROVIDERS,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<NetworkProvider> networkProviderArrayList = new ArrayList<>();
                            if(response.getInt("code") == 0){
                                JSONArray body = response.getJSONArray("body");
                                for(int i = 0;i < body.length();i++){
                                    JSONObject data = body.getJSONObject(i);
                                    if(data.getString("status").equals("ON")){
                                        NetworkProvider networkProvider = new NetworkProvider(data.getString("id"),
                                                data.getString("description"));
                                        networkProviderArrayList.add(networkProvider);
                                    }
                                }
                                NetworkProvidersGlobal = networkProviderArrayList;
                                Log.e("debug","networks : "+NetworkProvidersGlobal.size());
                                getIDTypes(dialogFragment);
                            }

                        } catch (JSONException e) {
                            cancelDialog(dialogFragment);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void getIDTypes(final DialogFragment dialogFragment){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ID_TYPES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<IDType> idTypeArrayList = new ArrayList<>();
                            if(response.getInt("code") == 0){
                                JSONArray body = response.getJSONArray("body");
                                for(int i = 0;i < body.length();i++){
                                    JSONObject data = body.getJSONObject(i);
                                    IDType idType = new IDType(data.getString("id"),
                                            data.getString("description"));
                                    idTypeArrayList.add(idType);
                                }
                                IDCardTypesGlobal = idTypeArrayList;
                                Log.e("debug","idtypes : "+IDCardTypesGlobal.size());
                                getBeneficiaries(dialogFragment);
                            }

                        } catch (JSONException e) {
                            cancelDialog(dialogFragment);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void getBeneficiaries(final DialogFragment dialogFragment){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_ALL_BENEFICIARIES_WITH_SCHEMES + getContributorID() + "/Schemes",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 0) {
                                JSONArray data = response.getJSONArray("body");
                                ArrayList<Beneficiary> beneficiaries = new ArrayList<>();
                                Beneficiaries = new Beneficiary[data.length()];
                                for(int i = 0; i < data.length();i++) {
                                    JSONArray schemesArray = data.getJSONObject(i).getJSONArray("schemes");
                                    Scheme[] schemes = new Scheme[schemesArray.length()];
                                    for(int j = 0; j < schemesArray.length();j++){
                                        schemes[j] = new Scheme(schemesArray.getJSONObject(j).getString("schemeId"),
                                                schemesArray.getJSONObject(j).getDouble("percentage"));
                                    }
                                    beneficiaries.add(new Beneficiary(data.getJSONObject(i).getString("id"),
                                            data.getJSONObject(i).getString("firstName"),
                                            data.getJSONObject(i).getString("lastName"),
                                            Utils.getDateNoTime(data.getJSONObject(i).getString("dob")),
                                            data.getJSONObject(i).getString("phoneNumber"),
                                            data.getJSONObject(i).getString("relationshipId"),
                                            0.00,
                                            data.getJSONObject(i).getString("gender"),
                                            schemes));
                                    Beneficiaries[i] = beneficiaries.get(i);
                                    Log.d("ben Dob", data.getJSONObject(i).getString("dob"));
                                }
                                getFavourites(dialogFragment);
                                return;
                            }
                            cancelDialog(dialogFragment);
                        } catch (Exception e) {
                            Log.d(ISPSS, "onResponse: "+e.toString());
                            cancelDialog(dialogFragment);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                        Log.d(ISPSS, "onErrorResponse: "+error);
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void getFavourites(final DialogFragment dialogFragment){
        final ArrayList<Favourite> favs = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                DOMAIN + GET_FAVOURITES + getContributorID(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 0){
                                JSONArray favArray = response.getJSONArray("body");
                                for(int i = 0; i < favArray.length();i++){
                                    JSONObject fav = favArray.getJSONObject(i);
                                    favs.add(new Favourite(fav.getString("id"),
                                            fav.getJSONObject("favourite").getString("memberID"),
                                            fav.getJSONObject("favourite").getString("firstName") + " " +
                                                    (fav.getJSONObject("favourite").getString("middleName").isEmpty() ? "" : fav.getJSONObject("favourite").getString("middleName") + " ") +
                                                    fav.getJSONObject("favourite").getString("lastName")));
                                }
                                FavouritesGlobal.addAll(favs);
                            } else {
                            }
                            cancelDialog(dialogFragment);
                        } catch (Exception e){
                            cancelDialog(dialogFragment);
                            Log.d(ISPSS, "onResponse: "+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelDialog(dialogFragment);
                        Log.d(ISPSS, "onErrorResponse: "+error.toString());
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }

    public int getRelationshipPosition(String id){
        for(int i = 0; i < RelationshipsGlobal.size(); i++){
            if(id.equals(RelationshipsGlobal.get(i).getId())){
                return i;
            }
        }
        return 1000000000;
    }

    public String getSchemeName(String id){
        String name = "";
        for(Scheme scheme : SchemesGlobal){
            if(scheme.getId().equals(id)){
                name = scheme.getName();
                return name;
            }
        }
        return name;
    }

    public boolean beneficiariesHas(String id){
        for (Beneficiary beneficiary : Beneficiaries) {
            if (beneficiary.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean beneficiariesHas(ArrayList<Beneficiary> beneficiaries, String id){
        for (Beneficiary beneficiary : beneficiaries) {
            if (beneficiary.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

}
