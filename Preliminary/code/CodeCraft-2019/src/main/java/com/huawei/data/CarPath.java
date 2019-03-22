package com.huawei.data;

public class CarPath {
    private Car car;
    private Path path;

    public CarPath(Car car, Path path) {
        this.car = car;
        this.path = path;
    }

    public Car getCar() {
        return car;
    }

    public Path getPath() {
        return path;
    }
}
