package com.huawei.data;

public class Cross {

    int id, roadIdNorth, roadIdEast, roadIdSouth, roadIdWest;

    public Cross(int id, int roadIdNorth, int roadIdEast, int roadIdSouth, int roadIdWest) {
        this.id = id;
        this.roadIdNorth = roadIdNorth;
        this.roadIdEast = roadIdEast;
        this.roadIdSouth = roadIdSouth;
        this.roadIdWest = roadIdWest;
    }

    public Cross(int[] tuple) {
        this(tuple[0], tuple[1], tuple[2], tuple[3], tuple[4]);
    }

    @Override
    public String toString() {
        return "Cross{" +
                "id=" + id +
                ", roadIdNorth=" + roadIdNorth +
                ", roadIdEast=" + roadIdEast +
                ", roadIdSouth=" + roadIdSouth +
                ", roadIdWest=" + roadIdWest +
                '}';
    }
}
