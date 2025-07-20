package server.world.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class C {

    // Store the directions/types for this cell
    public final List<Integer> I;
    public final int Z;

    // Constructor expects a list of directions/types and a property value
    public C(List<Integer> list, int i) {
        this.I = new ArrayList<>();
        if (list != null) {
            this.I.addAll(list);
        }
        this.Z = i;
    }

    // Legacy compatibility constructor (accepts raw ArrayList, unchecked)
    @SuppressWarnings("unchecked")
    public C(ArrayList arraylist, int i) {
        this.I = new ArrayList<>();
        if (arraylist != null) {
            for (Object obj : arraylist) {
                if (obj instanceof Integer) {
                    this.I.add((Integer) obj);
                }
            }
        }
        this.Z = i;
    }

    @Override
    public int hashCode() {
        return Objects.hash(I);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof C)) return false;
        C other = (C) obj;
        return Objects.equals(I, other.I);
    }
}
