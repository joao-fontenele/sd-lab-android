package com.example.jp.exmaps.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;

/**
 * Created by jp on 07/08/16.
 */
public class ApiCountries {
    public static CountryInterface countryService;

    public static CountryInterface getCountryClient() {
        if (countryService == null) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://restcountries.eu")
                    .setConverter(new GsonConverter(gson)).build();
            countryService = restAdapter.create(CountryInterface.class);
        }
        return countryService;
    }

    public interface CountryInterface {
        @GET("/rest/v1/all/")
        void getCountries(Callback<List<Country>> callback);
    }
}
