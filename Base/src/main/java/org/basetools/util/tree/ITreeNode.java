package org.basetools.util.tree;

import java.util.List;

public interface ITreeNode extends List<ITreeNode> {
    default boolean isRoot() {
        return getParent() == null;
    }

    <T extends ITreeNode> T getParent();

    int getPos();

    String getName();

    String getNamespace();

    boolean isAttribute();

    <T extends ITreeNode> T getRootNode();
}
