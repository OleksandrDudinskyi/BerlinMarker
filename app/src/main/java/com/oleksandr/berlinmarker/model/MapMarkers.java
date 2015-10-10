package com.oleksandr.berlinmarker.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
@Table(name = "MapMarkers")
public class MapMarkers extends Model {
    @Column(name = "Latitude")
    public double latitude;

    @Column(name = "Longitude")
    public double longitude;

    public MapMarkers() {
    }

    public MapMarkers(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
