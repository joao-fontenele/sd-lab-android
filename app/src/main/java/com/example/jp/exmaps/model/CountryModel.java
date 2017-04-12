package com.example.jp.exmaps.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import com.example.jp.exmaps.rest.Country;

/**
 * Created by jp on 07/08/16.
 */

@Table(name = "Country")
public class CountryModel extends Model {

    @Column(name = "name", unique = true, index = true)
    private String name;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public CountryModel() {
    }

    public CountryModel(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CountryModel getRandom() {
        return new Select()
                .from(CountryModel.class)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<CountryModel> listCountries() {
        return new Select()
                .from(CountryModel.class)
                .execute();
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "CountryModel{" +
                "name='" + name + '\'' +
                ", latLng=[" + latitude +
                "," + longitude +
                "]}";
    }
}
