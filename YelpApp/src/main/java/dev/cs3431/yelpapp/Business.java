package dev.cs3431.yelpapp;

public class Business {
    private final String id;
    private final String name;
    private final String address;
    private final String city;
    private final int stars;
    private final int tips;
    private final float latitude;
    private final float longitude;
    private final int zipcode;
    private final int rank;

    public Business(String id, String name, String address, String city, int stars, int tips, float latitude, float longitude, int zipcode) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.stars = stars;
        this.tips = tips;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zipcode = zipcode;
        this.rank = 0;
    }
    public Business(String id, String name, String address, String city, int stars, int tips, float latitude, float longitude, int zipcode, int rank) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.stars = stars;
        this.tips = tips;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zipcode = zipcode;
        this.rank = rank;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public int getStarRating() {
        return stars;
    }

    public int getNumTips() {
        return tips;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getZipcode() {return zipcode;}

    public int getRank() {return rank;}
}
