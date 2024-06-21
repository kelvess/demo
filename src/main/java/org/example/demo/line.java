package org.example.demo;

import javafx.beans.property.SimpleDoubleProperty;

public class line {
    private SimpleDoubleProperty T;
    private SimpleDoubleProperty Vx;
    private SimpleDoubleProperty Vy;
    private SimpleDoubleProperty Vz;
    private SimpleDoubleProperty X;
    private SimpleDoubleProperty Y;
    private SimpleDoubleProperty Z;


    public SimpleDoubleProperty XProperty() {
        return X;
    }


    public SimpleDoubleProperty TProperty() {
        return T;
    }

    public SimpleDoubleProperty YProperty() {
        return Y;
    }

    public SimpleDoubleProperty ZProperty() {
        return Z;
    }


    public SimpleDoubleProperty VxProperty() {
        return Vx;
    }


    public SimpleDoubleProperty VyProperty() {
        return Vy;
    }


    public SimpleDoubleProperty VzProperty() {
        return Vz;
    }


    //учитывает двойной пробел между числами
    public line(String string) {
        int t = string.indexOf(" ");
        int x = string.indexOf(" ", t + 2);
        int y = string.indexOf(" ", x + 2);
        int z = string.indexOf(" ", y + 2);
        int vx = string.indexOf(" ", z + 2);
        int vy = string.indexOf(" ", vx + 2);
        T = new SimpleDoubleProperty(Double.parseDouble(string.substring(0, t)));
        X = new SimpleDoubleProperty(Double.parseDouble(string.substring(t + 1, x)));
        Y = new SimpleDoubleProperty(Double.parseDouble(string.substring(x + 1, y)));
        Z = new SimpleDoubleProperty(Double.parseDouble(string.substring(y + 1, z)));
        Vx = new SimpleDoubleProperty(Double.parseDouble(string.substring(z + 1, vx)));
        Vy = new SimpleDoubleProperty(Double.parseDouble(string.substring(vx + 1, vy)));
        Vz = new SimpleDoubleProperty(Double.parseDouble(string.substring(vy + 1)));
    }


}
