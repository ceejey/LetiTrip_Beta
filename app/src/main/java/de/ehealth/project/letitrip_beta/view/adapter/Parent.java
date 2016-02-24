package de.ehealth.project.letitrip_beta.view.adapter;

import java.util.ArrayList;

/**
 * Created by Mirorn on 24.02.2016.
 */
public class Parent {
    private String mTitle;
    private ArrayList<String> mArrayChildren;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public ArrayList<String> getArrayChildren() {
        return mArrayChildren;
    }

    public void setArrayChildren(ArrayList<String> arrayChildren) {
        mArrayChildren = arrayChildren;
    }
}