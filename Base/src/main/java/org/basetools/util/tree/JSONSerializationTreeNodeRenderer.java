package org.basetools.util.tree;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class JSONSerializationTreeNodeRenderer<T, U> implements TreeRenderer<T, U> {
    private final Stack builderStack = new Stack();

    public JSONSerializationTreeNodeRenderer() {
        JSONObject builder = new JSONObject();
        pushToStack(builder);
    }

    public JSONObject build() {
        JSONObject builder = (JSONObject) builderStack.peek();
        return builder;
    }

    public JSONArray buildArray() {
        Object builder = builderStack.peek();

        return (JSONArray) builder;
    }

    public void renderLeafStart(TreeNode node) {
        Object builder = builderStack.peek();
        if (node.getParent() != null && node.getParent().isList()) {
            if (builder instanceof JSONArray) {
                ((JSONArray) builder).add(node.getData());
            } else {
                JSONObject simpleBuilder = new JSONObject();
                simpleBuilder.put(node.getID(), node.getData());
                ((JSONArray) builder).add(simpleBuilder);
            }
        } else {
            if (builder instanceof JSONArray) {
                JSONObject simpleBuilder = new JSONObject();
                simpleBuilder.put(node.getID(), node.getData());
                ((JSONArray) builder).add(simpleBuilder);
            } else {
                ((JSONObject) builder).put(node.getID(), node.getData());
            }
        }
    }

    public void renderCompositeStart(TreeNode node) {
        Object builder = builderStack.peek();

        if (node.getParent() != null && node.getParent().isList()) {
            JSONObject simpleBuilder = new JSONObject();
            pushToStack(simpleBuilder);
        } else {
            JSONObject baseBuilder = new JSONObject();
            if (builder instanceof JSONArray) {
                baseBuilder.put(node.getName(), baseBuilder);
                ((JSONArray) builder).add(baseBuilder);
                pushToStack((JSONArray) builder);
            } else {
                ((JSONObject) builder).put(node.getID(), baseBuilder);
                pushToStack(baseBuilder);
            }
        }
    }

    public void renderStart(TreeNode node) {
        if (node.getParent() == null) {
            JSONObject simpleBuilder = new JSONObject();
            pushToStack(simpleBuilder);
        } else if (node.hasChildren()) {
            renderCompositeStart(node);
        } else {
            renderLeafStart(node);
        }
    }

    private void pushToStack(JSONObject simpleBuilder) {
        builderStack.push(simpleBuilder);
    }

    private void pushToStack(JSONArray simpleBuilder) {
        builderStack.push(simpleBuilder);
    }

    private void renderEnd(TreeNode node) {
        if (node.hasChildren()) {
            Object baseBuilder = builderStack.pop();
            Object parentBuilder = builderStack.peek();

            if (parentBuilder instanceof JSONArray) {
                ((JSONArray) parentBuilder).add(baseBuilder);
            } else {
                ((JSONObject) parentBuilder).put(Objects.toString(node.getName(), node.getID()), baseBuilder);
            }
        }
    }

    @Override
    public void render(TreeNode genericTreeNode, boolean in, Map traverseData) {
        //  System.out.println("in:" + in + " " + genericTreeNode.getID());
        if (in)
            renderStart(genericTreeNode);
        else
            renderEnd(genericTreeNode);
    }
}
