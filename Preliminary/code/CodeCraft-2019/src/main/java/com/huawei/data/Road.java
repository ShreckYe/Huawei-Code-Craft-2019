package com.huawei.data;

public class Road {

    int id, length, speed, channel, from, to, isDuplex;

    public Road(int id, int length, int speed, int channel, int from, int to, int isDuplex) {
        this.id = id;
        this.length = length;
        this.speed = speed;
        this.channel = channel;
        this.from = from;
        this.to = to;
        this.isDuplex = isDuplex;
    }

    public Road(int[] tuple) {
        this(tuple[0], tuple[1], tuple[2], tuple[3], tuple[4], tuple[5], tuple[6]);
    }

    @Override
    public String toString() {
        return "Road{" +
                "id=" + id +
                ", length=" + length +
                ", speed=" + speed +
                ", channel=" + channel +
                ", from=" + from +
                ", to=" + to +
                ", isDuplex=" + isDuplex +
                '}';
    }
}
