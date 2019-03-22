package com.huawei.data;

public class CarAnswer {
    private Car car;
    private Answer answer;

    public CarAnswer(Car car, Answer answer) {
        this.car = car;
        this.answer = answer;
    }

    public Car getCar() {
        return car;
    }

    public Answer getAnswer() {
        return answer;
    }
}
