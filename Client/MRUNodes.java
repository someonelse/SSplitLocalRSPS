// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import sign.signlink;

public final class MRUNodes {

    public MRUNodes(int i) {
        emptyNodeSub = new NodeSub();
        nodeSubList = new NodeSubList();
        initialCount = i;
        spaceLeft = i;
        nodeCache = new NodeCache();
    }

    public NodeSub insertFromCache(long l) {
        NodeSub nodeSub = (NodeSub) nodeCache.findNodeByID(l);
        if (nodeSub != null) {
            nodeSubList.insertHead(nodeSub);
        }
        return nodeSub;
    }

    public void removeFromCache(NodeSub nodeSub, long l) {
        try {
            if (spaceLeft == 0) {
                NodeSub nodeSub1 = nodeSubList.popTail();
                nodeSub1.unlink();
                nodeSub1.unlinkSub();

                if (nodeSub1 == emptyNodeSub) {
                    NodeSub nodeSub2 = nodeSubList.popTail();
                    nodeSub2.unlink();
                    nodeSub2.unlinkSub();
                }
            } else {
                spaceLeft--;
            }

            nodeCache.removeFromCache(nodeSub, l);
            nodeSubList.insertHead(nodeSub);
        } catch (RuntimeException runtimeexception) {
            signlink.reporterror("47547, " + nodeSub + ", " + l + ", " + (byte) 2 + ", " + runtimeexception.toString());
            throw new RuntimeException();
        }
    }

    public void unlinkAll() {
        while (true) {
            NodeSub nodeSub = nodeSubList.popTail();
            if (nodeSub != null) {
                nodeSub.unlink();
                nodeSub.unlinkSub();
            } else {
                spaceLeft = initialCount;
                return;
            }
        }
    }

    private final NodeSub emptyNodeSub;
    private final int initialCount;
    private int spaceLeft;
    private final NodeCache nodeCache;
    private final NodeSubList nodeSubList;
}
