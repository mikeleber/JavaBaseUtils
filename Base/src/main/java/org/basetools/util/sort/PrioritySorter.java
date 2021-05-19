package org.basetools.util.sort;

import java.util.Comparator;

public class PrioritySorter<T extends Priorized> implements Comparator<T> {
    public boolean asc = true;

    public PrioritySorter() {
    }

    public PrioritySorter(boolean sortOrderAsc) {
        asc = sortOrderAsc;
    }

    @Override
    public int compare(Priorized prioA, Priorized prioB) {
        if (prioA == prioB) {
            return 0;
        }

        int rA = prioA.getPriority();
        int rB = prioB.getPriority();
        // wee need this because of altering the sequence if priority equals!
        // (without this we have problems while deleting a entry)
        if (rA == rB) {
            if (prioA.getCreationId() < prioB.getCreationId()) {
                return -1;
            } else {
                return 1;
            }
        }
        if (asc) {
            if (rA < rB) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (rA > rB) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }
}
