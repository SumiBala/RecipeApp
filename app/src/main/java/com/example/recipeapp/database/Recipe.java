package com.example.recipeapp.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    private int rid;
    private String rname;
    private int servings;
    private String image;
    private List<Ingredient> ingredientList;
    private List<Steps> steps;

    public Recipe(int rid, String rname, String image, int servings, List<Ingredient> ingredientList, List<Steps> steps) {
        this.rid = rid;
        this.rname = rname;
        this.image = image;
        this.servings = servings;
        this.ingredientList = ingredientList;
        this.steps = steps;
    }

    protected Recipe(Parcel in) {
        rid = in.readInt();
        rname = in.readString();
        image = in.readString();
        servings = in.readInt();
        ingredientList = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Steps.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rid);
        dest.writeString(rname);
        dest.writeString(image);
        dest.writeInt(servings);
        dest.writeTypedList(ingredientList);
        dest.writeTypedList(steps);
    }
}
