// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

@SuppressWarnings("all")
public final class OnDemandData extends NodeSub {

    public OnDemandData() {
        // This flag indicates whether the data is yet to be fully loaded or processed
        this.incomplete = true;
    }

    // The type of data this object represents
    int dataType;

    // The raw byte buffer containing the data
    byte[] buffer;

    // The identifier for this data block
    int ID;

    // Flag indicating whether this data is complete or still pending
    boolean incomplete;

    // The loop cycle value at which this data was processed/requested
    int loopCycle;
}
