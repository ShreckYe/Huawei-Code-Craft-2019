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

    public int getId() {
        return id;
    }

    public int getRoadIdNorth() {
        return roadIdNorth;
    }

    public int getRoadIdEast() {
        return roadIdEast;
    }

    public int getRoadIdSouth() {
        return roadIdSouth;
    }

    public int getRoadIdWest() {
        return roadIdWest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cross cross = (Cross) o;

        if (id != cross.id) return false;
        if (roadIdNorth != cross.roadIdNorth) return false;
        if (roadIdEast != cross.roadIdEast) return false;
        if (roadIdSouth != cross.roadIdSouth) return false;
        return roadIdWest == cross.roadIdWest;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + roadIdNorth;
        result = 31 * result + roadIdEast;
        result = 31 * result + roadIdSouth;
        result = 31 * result + roadIdWest;
        return result;
    }

    @Override
    public String toString() {
        return "GraphCross{" +
                "id=" + id +
                ", roadIdNorth=" + roadIdNorth +
                ", roadIdEast=" + roadIdEast +
                ", roadIdSouth=" + roadIdSouth +
                ", roadIdWest=" + roadIdWest +
                '}';
    }
}
