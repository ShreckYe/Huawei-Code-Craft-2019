package com.huawei.data;

import java.util.Arrays;

public class Path {
    private int[] roadIds;

    public Path(int[] roadIds) {
        this.roadIds = roadIds;
    }

    public int[] getRoadIds() {
        return roadIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        return Arrays.equals(roadIds, path.roadIds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(roadIds);
    }

    @Override
    public String toString() {
        return "Path{" +
                "roadIds=" + Arrays.toString(roadIds) +
                '}';
    }
}
