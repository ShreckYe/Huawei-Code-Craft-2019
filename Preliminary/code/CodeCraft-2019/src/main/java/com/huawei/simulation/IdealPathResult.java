package com.huawei.simulation;

import com.huawei.data.Path;

public class IdealPathResult {
    private double idealArriveTime;
    private Path path;

    public IdealPathResult(double idealArriveTime, Path path) {
        this.idealArriveTime = idealArriveTime;
        this.path = path;
    }

    public double getIdealArriveTime() {
        return idealArriveTime;
    }

    public Path getPath() {
        return path;
    }
}
