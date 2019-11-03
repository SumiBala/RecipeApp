package com.example.recipeapp.database;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private float qty;
    private String measurement;
    private String ingredient;

    public Ingredient(float qty, String measurement, String ingredient) {
        this.qty = qty;
        this.measurement = measurement;
        this.ingredient = ingredient;
    }

    protected Ingredient(Parcel in) {
        qty = in.readFloat();
        measurement = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(qty);
        dest.writeString(measurement);
        dest.writeString(ingredient);
    }
}
