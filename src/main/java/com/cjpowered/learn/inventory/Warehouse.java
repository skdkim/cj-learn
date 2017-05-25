package com.cjpowered.learn.inventory;

public enum Warehouse {

    Poughkeepsie, Ashford, Zzyzx, Peculiar;

    static Warehouse home() {
        return Poughkeepsie;
    }
}
