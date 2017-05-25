package com.cjpowered.learn.inventory;

public enum Warehouse {

    Poughkeepsie, Ashford, Zzyzx, Peculiar;

    public static Warehouse home() {
        return Poughkeepsie;
    }
}
