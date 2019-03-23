package com.huawei.graph;

public class DirectedRoadId {
    int roadId;
    boolean reverseDirection;

    public DirectedRoadId(int roadId, boolean reverseDirection) {
        this.roadId = roadId;
        this.reverseDirection = reverseDirection;
    }

    public int getRoadId() {
        return roadId;
    }

    public boolean isReverseDirection() {
        return reverseDirection;
    }

    @Override
    public String toString() {
        return "DirectedRoadId{" +
                "roadId=" + roadId +
                ", reverseDirection=" + reverseDirection +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectedRoadId that = (DirectedRoadId) o;

        if (roadId != that.roadId) return false;
        return reverseDirection == that.reverseDirection;
    }

    @Override
    public int hashCode() {
        int result = roadId;
        result = 31 * result + (reverseDirection ? 1 : 0);
        return result;
    }
}
