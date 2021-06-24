package org.basetools.util.tree;

import org.basetools.util.json.JSONUtilities;

import javax.json.*;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class JSONSerializationTreeNodeRenderer<T, U> implements TreeRenderer<T, U> {
    private static final JsonObjectBuilder NULL_BUILDER = Json.createObjectBuilder();
    private final Stack builderStack = new Stack();

    public JSONSerializationTreeNodeRenderer() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        pushToStack(builder);
    }

    public JsonObject build() {
        Object builder = builderStack.peek();
        return ((JsonObjectBuilder) builder).build();
    }

    public JsonArray buildArray() {
        Object builder = builderStack.peek();
        if (builder instanceof JsonArrayBuilder) {
            return ((JsonArrayBuilder) builder).build();
        }
        return null;
    }

    public void renderLeafStart(TreeNode node) {
        Object builder = builderStack.peek();
        if (node.getParent() != null && node.getParent().isList()) {
            if (builder instanceof JsonArrayBuilder) {
                JSONUtilities.add((JsonArrayBuilder) builder, node.getData());
            } else {
                JsonObjectBuilder simpleBuilder = Json.createObjectBuilder();
                JSONUtilities.add(simpleBuilder, node.getID(), node.getData());
                JSONUtilities.add((JsonArrayBuilder) builder, simpleBuilder);
            }
        } else {
            if (builder instanceof JsonArrayBuilder) {
                JsonObjectBuilder simpleBuilder = Json.createObjectBuilder();
                JSONUtilities.add(simpleBuilder, node.getID(), node.getData());
                JSONUtilities.add((JsonArrayBuilder) builder, simpleBuilder);
            } else {
                JSONUtilities.add((JsonObjectBuilder) builder, node.getID(), node.getData());
            }
        }
    }

    public void renderCompositeStart(TreeNode node) {
        Object builder = builderStack.peek();

        if (node.getParent() != null && node.getParent().isList()) {
            JsonObjectBuilder simpleBuilder = Json.createObjectBuilder();
            pushToStack(simpleBuilder);
        } else {
            JsonObjectBuilder baseBuilder = Json.createObjectBuilder();
            if (builder instanceof JsonArrayBuilder) {
                baseBuilder.add(node.getName(), baseBuilder);
                builder = JSONUtilities.add((JsonArrayBuilder) builder, baseBuilder);
                pushToStack((JsonArrayBuilder) builder);
            } else {
                ((JsonObjectBuilder) builder).add(node.getID(), baseBuilder);
                pushToStack(baseBuilder);
            }
        }
    }

    public void renderStart(TreeNode node) {
        if (node.getParent() == null) {
            JsonObjectBuilder simpleBuilder = Json.createObjectBuilder();
            pushToStack(simpleBuilder);
        } else if (node.hasChildren()) {
            renderCompositeStart(node);
        } else {
            renderLeafStart(node);
        }
    }

    private void pushToStack(JsonObjectBuilder simpleBuilder) {
        builderStack.push(simpleBuilder);
    }

    private void pushToStack(JsonArrayBuilder simpleBuilder) {
        builderStack.push(simpleBuilder);
    }

    private void renderEnd(TreeNode node) {
        if (node.hasChildren()) {
            Object baseBuilder = builderStack.pop();
            Object parentBuilder = builderStack.peek();

            if (parentBuilder instanceof JsonArrayBuilder) {
                JSONUtilities.add((JsonArrayBuilder) parentBuilder, baseBuilder);
            } else {
                JSONUtilities.add((JsonObjectBuilder) parentBuilder, Objects.toString(node.getName(), node.getID()), baseBuilder);
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
