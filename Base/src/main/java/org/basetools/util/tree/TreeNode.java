package org.basetools.util.tree;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.basetools.util.collection.ValueFacade;
import org.basetools.util.tree.creator.JSONNodeCreator;
import org.basetools.util.tree.creator.NodeCreator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeNode<T, U> {
    public static final String FIND_WILDCARD = "*";
    protected T data;
    protected U userobject;
    protected Object _info;
    protected List<TreeNode<T, U>> children;
    protected TreeNode<T, U> parent;
    protected int depth = -1;
    protected HashMap<String, Object> _properties;
    protected String _id;
    protected String _strucId;
    protected String _description;
    protected String _name;

    protected boolean _isList;
    protected boolean _isRecursive;

    public TreeNode(TreeNode<T, U> parent, T data, U usrobj, boolean add) {
        this(data, usrobj);
        if (parent != null && add) {
            parent.addChild(this);
        }
    }

    public TreeNode(T data, U usrobj) {
        this(data);
        setUserObject(usrobj);
    }

    public TreeNode<T, U> addChild(TreeNode<T, U> child) {
        depth = -1;
        getChildren().add(child);
        child.setParent(this);
        return child;
    }

    private void reset() {
        _strucId = null;
    }

    public TreeNode(T data) {
        this();
        setData(data);
    }

    public List<TreeNode<T, U>> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public TreeNode() {
        super();
    }

    public void setChildren(List<TreeNode<T, U>> children) {
        depth = -1;
        this.children = children;
        for (TreeNode<T, U> child : children) {
            child.setParent(this);
        }
    }

    public TreeNode(T data, U usrobj, String name) {
        this(data);
        setUserObject(usrobj);
        setName(name);
    }

    public TreeNode(String id, TreeNode<T, U> parent, T data, U usrobj) {
        this(id, data, usrobj);
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public TreeNode(String id, T data, U usrobj) {
        this(id, data);
        setUserObject(usrobj);
    }

    public TreeNode(String id, T data) {
        this();
        setId(id);
        setData(data);
    }

    public void setId(String id) {
        _id = id;
    }

    public TreeNode(TreeNode<T, U> parent, T data) {
        this(parent, data, null);
    }

    public TreeNode(TreeNode<T, U> parent, T data, U usrobj) {
        this(data, usrobj);
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public TreeNode<T, U> find(TreeNode<T, U> nodeToFind) {
        TreeNode<T, U> returnNode = auxiliaryFind(this, nodeToFind);
        return returnNode;
    }

    public boolean isChild(TreeNode<T, U> nodeToFind, boolean hierarchy) {
        for (TreeNode<T, U> node : getChildren()) {
            if (Objects.equals(node, nodeToFind)) {
                return true;
            }
            if (hierarchy) {
                if (node.isChild(nodeToFind, hierarchy)) {
                    return true;
                }
            }
        }

        return false;
    }

    public TreeNode<T, U> find(T data, U userObj) {
        TreeNode<T, U> returnNode = auxiliaryFind(this, data, userObj);
        return returnNode;
    }

    public TreeNode<T, U> find(T data) {
        TreeNode<T, U> returnNode = auxiliaryFind(this, data);
        return returnNode;
    }

    private TreeNode<T, U> auxiliaryFind(TreeNode<T, U> currentNode, T data) {
        TreeNode<T, U> returnNode = null;
        int i = 0;
        if (currentNode.getData() == data) {
            returnNode = currentNode;
        } else if (currentNode.hasChildren()) {
            i = 0;
            while (returnNode == null && i < currentNode.size()) {
                returnNode = auxiliaryFind(currentNode.get(i), data);
                i++;
            }
        }
        return returnNode;
    }

    private TreeNode<T, U> auxiliaryFind(TreeNode<T, U> currentNode, T data, U userObj) {
        TreeNode<T, U> returnNode = null;
        int i = 0;
        if (currentNode.getData() == data && (currentNode.getUserObject() == userObj)) {
            returnNode = currentNode;
        } else if (currentNode.hasChildren()) {
            i = 0;
            while (returnNode == null && i < currentNode.size()) {
                returnNode = auxiliaryFind(currentNode.get(i), data, userObj);
                i++;
            }
        }
        return returnNode;
    }

    private TreeNode<T, U> auxiliaryFind(TreeNode<T, U> currentNode, TreeNode<T, U> nodeToFind) {
        TreeNode<T, U> returnNode = null;
        int i = 0;
        if (Objects.equals(currentNode, nodeToFind)) {
            returnNode = currentNode;
        } else if (currentNode.getData() == nodeToFind.getData() && currentNode.getUserObject() == nodeToFind.getUserObject()) {
            returnNode = currentNode;
        } else if (currentNode.hasChildren()) {
            i = 0;
            while (returnNode == null && i < currentNode.size()) {
                returnNode = auxiliaryFind(currentNode.get(i), nodeToFind);
                i++;
            }
        }
        return returnNode;
    }

    public boolean isSame(TreeNode<T, U> other) {
        if (other != null) {
            if (this == other || equals(other)) {
                return true;
            } else {
                return StringUtils.equals(_id, other._id) && getData() == other.getData() && getUserObject() == other.getUserObject();
            }
        }
        return false;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public U getUserObject() {
        return userobject;
    }

    public TreeNode<T, U> setUserObject(U obj) {
        userobject = obj;
        return this;
    }

    public String getID() {
        if (_id == null) {
            return getStructureId();
        }
        return _id;
    }

    public String getStructureId() {
        if (_strucId == null) {
            String id = null;
            if (getParent() != null) {
                int pos = getPos() + 1;
                id = getParent().getStructureId() + "_" + pos;
            } else {
                id = "1";
            }
            _strucId = id;
        }
        return _strucId;
    }

    public int getPos() {
        if (getParent() != null) {
            int pos = getParent().getChildren().indexOf(this);
            return pos;
        }
        return -1;
    }

    public boolean isNodeChild(TreeNode<T, U> aNode) {
        boolean retval;
        if (aNode == null) {
            retval = false;
        } else {
            if (size() == 0) {
                retval = false;
            } else {
                retval = (aNode.getParent() == this);
            }
        }
        return retval;
    }

    public int indexOf(TreeNode<T, U> aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!isNodeChild(aChild)) {
            return -1;
        }
        return children.indexOf(aChild); // linear search
    }

    public Collection<TreeNode<T, U>> getAllChildren() {
        //List<TreeNode<T, U>> childrens = getChildrenOld(this);
        return getChildren(this, new ArrayList<>());
//        if (childrens.size() != childrens2.size()) {
//            System.out.println("error");
//        }
//        return childrens2;
    }

    public <c extends Collection<TreeNode<T, U>>> c getAllChildren(Collection<TreeNode<T, U>> result) {
        //List<TreeNode<T, U>> childrens = getChildrenOld(this);
        return (c) getChildren(this, result);
//        if (childrens.size() != childrens2.size()) {
//            System.out.println("error");
//        }
//        return childrens2;
    }

    private List<TreeNode<T, U>> getChildrenOld(TreeNode<T, U> root) {
        List<TreeNode<T, U>> childrens = new ArrayList<>();
        if (root != null) {
            for (TreeNode<T, U> node : root.getChildren()) {
                childrens.add(node);
                if (node.hasChildren()) {
                    childrens.addAll(getChildrenOld(node));
                }
            }
        }
        return childrens;
    }

    private Collection<TreeNode<T, U>> getChildren(TreeNode<T, U> startNode, Collection<TreeNode<T, U>> childrens) {
        if (startNode != null) {
            for (TreeNode<T, U> node : startNode.getChildren()) {
                childrens.add(node);
                if (node.hasChildren()) {
                    getChildren(node, childrens);
                }
            }
        }
        return childrens;
    }

    public List<TreeNode<T, U>> findChildren(Predicate<TreeNode<T, U>> predicate) {
        List<TreeNode<T, U>> childrens = new ArrayList<>();
        for (TreeNode<T, U> node : getChildren()) {
            if (predicate.test(node)) {
                childrens.add(node);
            }
        }

        return childrens;
    }

    public TreeNode<T, U> findChild(Predicate<TreeNode<T, U>> predicate) {
        for (TreeNode<T, U> node : getChildren()) {
            if (predicate.test(node)) {
                return node;
            }
        }

        return null;
    }


    public boolean hasChildren() {
        return (children != null && size() > 0);
    }

    public TreeNode<T, U> cloneNode(boolean deep) {
        TreeNode<T, U> copy = new TreeNode<>(getData(), getUserObject());
        copy.setName(getName());
        if (deep && children != null) {
            for (TreeNode<T, U> child : children) {
                copy.addChild(child.cloneNode(deep));
            }
        }
        return copy;
    }

    public void addChildren(List<TreeNode<T, U>> childs) {
        depth = -1;
        getChildren().addAll(childs);
        for (TreeNode<T, U> child : childs) {
            child.setParent(this);
        }
    }

    public void clear() {
        if (children != null) {
            depth = -1;
            children.clear();
        }
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public TreeNode<T, U> withName(String name) {
        _name = name;
        return this;
    }

    public boolean pushUp(boolean preservePosition, boolean removeContainerIfEmpty) throws IndexOutOfBoundsException {
        boolean removed = false;
        if (getParent() != null && getParent().getParent() != null) {
            TreeNode<T, U> parent = getParent();
            TreeNode<T, U> pParent = parent.getParent();
            List<TreeNode<T, U>> childrens = parent.getChildren();
            int idx = childrens.indexOf(this);
            if (idx >= 0) {
                depth = -1;
                removed = parent.remove(idx);
                if (removed) {
                    if (preservePosition) {
                        pParent.add(idx, this);
                    } else {
                        pParent.addChild(this);
                    }
                    if (parent.size() == 0) {
                        parent.remove();
                    }
                }
            }
        }
        return removed;
    }

    public boolean moveTo(TreeNode newParent) throws IndexOutOfBoundsException {
        boolean removed = false;
        if (getParent() != null && newParent != null) {
            TreeNode<T, U> parent = getParent();
            List<TreeNode<T, U>> childrens = parent.getChildren();
            int idx = childrens.indexOf(this);
            if (idx >= 0) {
                depth = -1;
                removed = parent.remove(idx);
                if (removed) {
                    newParent.addChild(this);
                    if (parent.size() == 0) {
                        parent.remove();
                    }
                }
            }
        }
        return removed;
    }

    public TreeNode<T, U> inject() throws IndexOutOfBoundsException {
        TreeNode<T, U> node = cloneNode(false);
        inject(node);
        return node;
    }

    /*
    replace the current node with the given node within parent and add the current node to the given.
    Insert given and shit current node down!
    */
    public boolean inject(TreeNode node) throws IndexOutOfBoundsException {
        boolean injected = false;
        if (getParent() != null && getParent().getParent() != null) {
            TreeNode<T, U> parent = getParent();
            List<TreeNode<T, U>> childrens = parent.getChildren();
            int idx = childrens.indexOf(this);
            if (idx >= 0) {
                depth = -1;
                injected = parent.remove(idx);
                if (injected) {
                    node.addChild(this);
                    parent.add(idx, node);
                }
            }
        }
        return injected;
    }

    public TreeNode<T, U> get(int index) throws IndexOutOfBoundsException {
        if (children != null) {
            return children.get(index);
        } else {
            return null;
        }
    }

    public TreeNode<T, U> getChild(TreeNode<T, U> child) throws IndexOutOfBoundsException {
        if (children != null) {
            int idx = children.indexOf(child);
            if (idx >= 0) {
                return children.get(idx);
            }
        }
        return null;
    }

    public TreeNode<T, U> getChildByData(T data) throws IndexOutOfBoundsException {
        if (children != null) {
            List<TreeNode<T, U>> childs = getChildren();
            for (TreeNode<T, U> child : childs) {
                if (child.getData() != null && child.getData().equals(data)) {
                    return child;
                }
            }
        }
        return null;
    }

    public TreeNode<T, U> getChildByDataAndUserObj(T data, U usrObj) throws IndexOutOfBoundsException {
        if (children != null) {
            List<TreeNode<T, U>> childs = getChildren();
            for (TreeNode<T, U> child : childs) {
                if (Objects.equals(child.getData(), data)) {
                    if (Objects.equals(child.getUserObject(), usrObj)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    public TreeNode<T, U> findNodeByData(T data) {
        List<TreeNode<T, U>> returnNodes = null;
        returnNodes = findNodesByData(returnNodes, data);
        return returnNodes != null && returnNodes.size() > 0 ? returnNodes.get(0) : null;
    }

//    public static TreeNode of(TreeNode templateNode) {
//        TreeNode newNode = new TreeNode<>();
//        newNode.data = templateNode.data;
//        newNode.userobject = templateNode.userobject;
//        newNode._info = templateNode._info;
//        newNode._properties = templateNode._properties;
//        newNode._id = templateNode._id;
//        newNode._description = templateNode._description;
//        newNode._name = templateNode._name;
//        newNode._isList = templateNode._isList;
//        newNode._isContainer = templateNode._isContainer;
//        return newNode;
//    }

    public List<TreeNode<T, U>> findNodesByData(T data) {
        List<TreeNode<T, U>> returnNodes = null;
        returnNodes = findNodesByData(returnNodes, data);
        return returnNodes;
    }

    public List<TreeNode<T, U>> findNodesByData(List<TreeNode<T, U>> returnNodes, T data) {
        if (data != null && data.equals(getData())) {
            if (returnNodes == null) {
                returnNodes = new ArrayList<>();
            }
            returnNodes.add(this);
        }
        List<TreeNode<T, U>> childs = getChildren();
        for (int c = 0; c < childs.size(); c++) {
            returnNodes = childs.get(c).findNodesByData(returnNodes, data);
        }
        return returnNodes;
    }

    public TreeNode<T, U> findParentByData(T data) {
        if (getParent() != null && Objects.equals(getParent().getData(), data)) {
            return getParent();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getID() != null) {
            sb.append(getID());
            sb.append(" ");
        }
        sb.append(getData() != null ? getData().toString() : "no Data");
        if (getUserObject() != null && getUserObject().toString().length() > 0) {
            sb.append("=");
            sb.append(getUserObject().toString());
        }
        return sb.toString();
    }

    public String toStringVerbose() {
        String stringRepresentation = (getData() != null ? getData().toString() : "no Data") + ":[";
        if (children != null) {
            for (TreeNode<T, U> node : getChildren()) {
                stringRepresentation += (node.getData() != null ? node.getData().toString() : "no Data") + ", ";
            }
        }
        // Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
        Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(stringRepresentation);
        stringRepresentation = matcher.replaceFirst("");
        stringRepresentation += "]";
        return stringRepresentation;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public TreeNode<T, U> getParent() {
        return parent;
    }

    public TreeNode<T, U> getParent(Predicate<TreeNode<T, U>> condition) {
        if (parent == null) return null;
        if (condition.test(parent)) {
            return parent;
        }
        return parent.getParent(condition);
    }

    public void setParent(TreeNode<T, U> parent) {
        this.parent = parent;
        reset();
    }

    public TreeNode<T, U> getParent(int minChilds) {
        if (parent != null) {
            if (parent.size() >= minChilds) {
                return parent;
            } else {
                return parent.getParent(minChilds);
            }
        }
        return this;
    }

    public int getDepth() {
        if (depth == -1) {
            depth = getDepth(0);
        }
        return depth;
    }

    public int getLevel() {
        int level = 0;
        TreeNode<T, U> aParent = this;
        while ((aParent = aParent.getParent()) != null) {
            level++;
        }
        return level;
    }

    private int getDepth(int depth) {
        int cc = size();
        if (cc > 0) {
            depth++;
            List<TreeNode<T, U>> childs = getChildren();
            for (TreeNode<T, U> child : childs) {
                int cdepth = child.getDepth(depth);
                depth = Math.max(cdepth, depth);
            }
        }
        return depth;
    }

    public List<TreeNode<T, U>> findLeafNodes() {
        List<TreeNode<T, U>> returnNodes = null;
        returnNodes = findLeafNodes(returnNodes);
        return returnNodes;
    }

    public List<TreeNode<T, U>> findInfoNodes() {
        List<TreeNode<T, U>> returnNodes = new ArrayList<>();
        returnNodes = findInfoNodes(returnNodes);
        return returnNodes;
    }

    public boolean hasInfoNodes() {

        List<TreeNode<T, U>> childs = getChildren();
        for (int c = 0; c < childs.size(); c++) {
            boolean has = childs.get(c).hasInfoNodes();
            if (has) {
                return true;
            }
        }
        return false;
    }

    public List<TreeNode<T, U>> findLeafNodes(List<TreeNode<T, U>> returnNodes) {
        if (size() == 0) {
            if (returnNodes == null) {
                returnNodes = new ArrayList<>();
            }
            returnNodes.add(this);
        } else {
            List<TreeNode<T, U>> childs = getChildren();
            for (int c = 0; c < childs.size(); c++) {
                returnNodes = childs.get(c).findLeafNodes(returnNodes);
            }
        }
        return returnNodes;
    }

    public List<TreeNode<T, U>> findInfoNodes(List<TreeNode<T, U>> returnNodes) {

        if (!isLeaf()) {
            List<TreeNode<T, U>> childs = getChildren();
            for (int c = 0; c < childs.size(); c++) {
                returnNodes = childs.get(c).findInfoNodes(returnNodes);
            }
        }
        return returnNodes;
    }

    public void buildChildParentMap(MultiKeyMap result) {
        T parentData = null;
        if (getParent() != null) {
            parentData = getParent().getData();
        }
        result.put(parentData, getData(), this);
        if (size() > 0) {
        }
        List<TreeNode<T, U>> childs = getChildren();
        for (int c = 0; c < childs.size(); c++) {
            childs.get(c).buildChildParentMap(result);
        }
    }

    public List<TreeNode<T, U>> getPathToParent(boolean upstairs) {
        List<TreeNode<T, U>> result = new ArrayList<>();
        result.add(this);
        TreeNode<T, U> aParent = this;
        while ((aParent = aParent.getParent()) != null) {
            result.add(aParent);
        }
        if (!upstairs) {
            Collections.reverse(result);
        }
        return result;
    }

    public List<T> getDataPathToParent() {
        List<T> result = new ArrayList<>();
        result.add(getData());
        TreeNode<T, U> aParent = this;
        while ((aParent = aParent.getParent()) != null) {
            result.add(aParent.getData());
        }
        return result;
    }

    public TreeNode<T, U> getParentByData(T data) {
        if (getData() == data || (data != null && data.equals(getData()))) {
            return this;
        } else if (getParent() != null) {
            return getParent().getParentByData(data);
        }
        return null;
    }

    public TreeNode<T, U> getRootNode() {
        TreeNode<T, U> root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    public boolean hasParent(TreeNode<T, U> aParent) {
        TreeNode<T, U> root = this;
        while (root.getParent() != null) {
            root = root.getParent();
            if (Objects.equals(aParent, root)) {
                return true;
            }
        }
        return false;
    }

    public TreeNode<T, U> goUpToLevel(int level) {
        if (getParent() == null || getLevel() <= level) {
            return this;
        }
        return getParent().goUpToLevel(level);
    }

    public boolean removeFromParent() {
        if (getParent() != null) {
            return getParent().remove(this);
        }
        return false;
    }

    public boolean remove(TreeNode<T, U> child) throws IndexOutOfBoundsException {
        return remove(child, false);
    }

    public boolean remove(TreeNode<T, U> child, boolean shiftChildsUp) throws IndexOutOfBoundsException {
        boolean removed = false;
        if (children != null) {
            int idx = children.indexOf(child);
            if (idx >= 0) {
                depth = -1;
                removed = remove(idx);
                if (removed && shiftChildsUp && child.size() > 0) {
                    add(idx, child.getChildren());
                }
            }
        }
        return removed;
    }

    public boolean remove(int index) throws IndexOutOfBoundsException {
        boolean removed = false;
        if (children != null) {
            TreeNode removedChild = children.get(index);
            removed = children.remove(index) != null;
            if (removed) {
                removedChild.reset();
                depth = -1;
            }
        }
        return removed;
    }

    public int size() {
        if (children == null) {
            return 0;
        }
        return getChildren().size();
    }

    public void add(int index, List<TreeNode<T, U>> childs) {
        depth = -1;
        for (TreeNode<T, U> child : childs) {
            add(index++, child);
        }
    }

    public TreeNode<T, U> add(int index, TreeNode<T, U> child) throws IndexOutOfBoundsException {
        depth = -1;
        getChildren().add(index, child);
        child.setParent(this);
        return child;
    }

    public List<TreeNode<T, U>> remove() {
        if (getParent() != null) {
            getParent().remove(this);
        }
        for (int i = 0; i < size(); i++) {
            get(i).setParent(null);
        }
        return getChildren();
    }

    public boolean isLeaf() {
        return size() == 0;
    }

    public String getPath() {
        StringBuffer sb = new StringBuffer();
        createPath(sb, null);
        return sb.toString();
    }

    public String createPath(Function<TreeNode<T, U>, String> nameGetter) {
        StringBuffer sb = new StringBuffer();
        createPath(sb, nameGetter);
        return sb.toString();
    }

    public void createPath(StringBuffer sb, Function<TreeNode<T, U>, String> nameGetter) {

        String name = nameGetter != null ? nameGetter.apply(this) : getID();
        if (getParent() != null) {
            getParent().createPath(sb, nameGetter);
            if (!isList()) {
                sb.append("/");
                sb.append(name);
                if (getParent() != null && getParent().isList() && getParent().size() > 0) {
                    sb.append("[");
                    sb.append(getParent().getChildren().indexOf(this) + 1);
                    sb.append("]");
                }
            }
        } else {
            sb.append("/");
            sb.append(name);
        }
    }

    public void accept(TreeVisitor<T, U> visitor) {
        if (!visitor.doBreak(this)) {
            visitor.visitStart(this);
            List<TreeNode<T, U>> childs = getChildren();
            for (int c = 0; c < childs.size(); c++) {
                TreeNode<T, U> achild = childs.get(c);
                achild.accept(visitor);
            }
            visitor.visitEnd(this);
        }
    }

    public boolean isList() {
        return _isList;
    }

    public boolean isRecursive() {
        return _isRecursive;
    }

    public void setIsList(boolean isList) {
        _isList = isList;
    }

    public TreeNode<T, U> setIsRecursive(boolean recursive) {
        _isRecursive = recursive;
        return this;
    }

    public void render(TreeRenderer<?, ?> renderer) {
        render(renderer, new HashMap<String, Object>());
    }

    public void render(TreeRenderer renderer, Map<?, ?> traverseData) {
        renderer.render(this, true, traverseData);
        for (TreeNode<T, U> aNode : getChildren()) {
            aNode.render(renderer, traverseData);
        }
        renderer.render(this, false, traverseData);
    }

    private String evalName() {
        String name = (getData() != null ? getData().toString() : "null");
        return name;
    }

    public void putProperty(String key, Object info) {
        getProperties().put(key, info);
    }

    public HashMap<String, Object> getProperties() {
        if (_properties == null) {
            _properties = new HashMap<>();
        }
        return _properties;
    }

    public void toXML(StringBuilder stringRepresentation, ValueFacade<String, T> dataFacade, ValueFacade<String, U> userObjFacade) {
        boolean isLeaf = isLeaf();
        String name;
        if (dataFacade != null) {
            name = dataFacade.getValue(getData());
        } else {
            name = (getData() != null ? StringUtils.replace(getData().toString(), "/", "") : "null");
        }
        name = StringEscapeUtils.escapeXml11(name);
        boolean hasName = !StringUtils.isEmpty(name);
        if (hasName) {
            name = StringUtils.replace(name, " ", "");
            name = StringUtils.replace(name, "[", "");
            name = StringUtils.replace(name, "]", "");
            if (StringUtils.isNumeric(name)) {
                name = "NUM" + name;
            }
            stringRepresentation.append("<");
            stringRepresentation.append(name);
            stringRepresentation.append(">");
        }
        if (isLeaf) {
            if (getUserObject() != null) {
                stringRepresentation.append(StringEscapeUtils.escapeXml10(userObjFacade != null ? userObjFacade.getValue(getUserObject()) : getUserObject().toString()));
            }
        } else {
            for (TreeNode<T, U> aNode : getChildren()) {
                aNode.toXML(stringRepresentation, dataFacade, userObjFacade);
            }
        }
        if (hasName) {
            stringRepresentation.append("</");
            stringRepresentation.append(name);
            stringRepresentation.append(">");
        }
    }

    public <T> T getProperty(String key) {
        if (_properties != null) {
            return (T) getProperties().get(key);
        } else {
            return null;
        }
    }

    public TreeNode<T, U> getChildByID(Object id) throws IndexOutOfBoundsException {
        if (children != null) {
            List<TreeNode<T, U>> childs = getChildren();
            for (TreeNode<T, U> child : childs) {
                if (child != null && Objects.equals(child.getID(), id)) {
                    return child;
                }
            }
        }
        return null;
    }

    public TreeNode<T, U> getChildByName(Object name) throws IndexOutOfBoundsException {
        if (children != null) {
            List<TreeNode<T, U>> childs = getChildren();
            for (TreeNode<T, U> child : childs) {
                if (child != null && Objects.equals(child.getName(), name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void getNodesAtLevel(int level, List<TreeNode<T, U>> result) throws IndexOutOfBoundsException {
        if (children != null) {
            List<TreeNode<T, U>> childs = getChildren();
            if (level == 0) {
                result.addAll(childs);
            } else {
                for (int c = 0; c < childs.size(); c++) {
                    TreeNode<T, U> achild = childs.get(c);
                    achild.getNodesAtLevel(level - 1, result);
                }
            }
        }
    }

    public TreeNode<T, U> findParentByID(String parentID) {
        if (parentID == null) {
            return null;
        }
        if (Objects.equals(getID(), parentID)) {
            return this;
        }
        if (getParent() != null) {
            return getParent().findParentByID(parentID);
        } else {
            return null;
        }
    }

    /**
     * Returns the next sibling of this node in the parent's children array. Returns null if this node has no parent or is the parent's last child. This method performs a linear search that is O(n) where n is the number of children; to traverse the entire array, use the parent's child enumeration
     * instead.
     *
     * @return the sibling of this node that immediately follows this node
     * @see #children
     */
    public TreeNode<T, U> getNextSibling() {
        TreeNode<T, U> retval;
        TreeNode<T, U> myParent = getParent();
        if (myParent == null) {
            retval = null;
        } else {
            retval = myParent.getChildAfter(this); // linear search
        }
        if (retval != null && !isNodeSibling(retval)) {
            throw new Error("child of parent is not a sibling");
        }
        return retval;
    }

    /**
     * Returns the previous sibling of this node in the parent's children array. Returns null if this node has no parent or is the parent's first child. This method performs a linear search that is O(n) where n is the number of children.
     *
     * @return the sibling of this node that immediately precedes this node
     */
    public TreeNode<T, U> getPreviousSibling() {
        TreeNode<T, U> retval;
        TreeNode<T, U> myParent = getParent();
        if (myParent == null) {
            retval = null;
        } else {
            retval = myParent.getChildBefore(this); // linear search
        }
        if (retval != null && !isNodeSibling(retval)) {
            throw new Error("child of parent is not a sibling");
        }
        return retval;
    }

    public TreeNode<T, U> getChildAfter(TreeNode<T, U> aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int index = indexOf(aChild); // linear search
        if (index == -1) {
            throw new IllegalArgumentException("node is not a child");
        }
        if (index < size() - 1) {
            return get(index + 1);
        } else {
            return null;
        }
    }

    /**
     * Returns the child in this node's child array that immediately precedes <code>aChild</code>, which must be a child of this node. If <code>aChild</code> is the first child, returns null. This method performs a linear search of this node's children for <code>aChild</code> and is O(n) where n is
     * the number of children.
     *
     * @return the child of this node that immediately precedes <code>aChild</code>
     * @throws IllegalArgumentException if <code>aChild</code> is null or is not a child of this node
     */
    public TreeNode<T, U> getChildBefore(TreeNode<T, U> aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int index = indexOf(aChild); // linear search
        if (index == -1) {
            throw new IllegalArgumentException("argument is not a child");
        }
        if (index > 0) {
            return get(index - 1);
        } else {
            return null;
        }
    }

    /**
     * Returns true if <code>anotherNode</code> is a sibling of (has the same parent as) this node. A node is its own sibling. If <code>anotherNode</code> is null, returns false.
     *
     * @param anotherNode node to test as sibling of this node
     * @return true if <code>anotherNode</code> is a sibling of this node
     */
    public boolean isNodeSibling(TreeNode<T, U> anotherNode) {
        boolean retval;
        if (anotherNode == null) {
            retval = false;
        } else if (anotherNode == this) {
            retval = true;
        } else {
            TreeNode<T, U> myParent = getParent();
            retval = (myParent != null && myParent == anotherNode.getParent());
            if (retval && !getParent().isNodeChild(anotherNode)) {
                throw new Error("sibling has different parent");
            }
        }
        return retval;
    }

    /**
     * Returns the node that follows this node in a preorder traversal of this node's tree. Returns null if this node is the last node of the traversal. This is an inefficient way to traverse the entire tree; use an enumeration, instead.
     *
     * @return the node that follows this node in a preorder traversal, or null if this node is last
     */
    public TreeNode<T, U> getNextNode() {
        if (size() == 0) {
            // No children, so look for nextSibling
            TreeNode<T, U> nextSibling = getNextSibling();
            if (nextSibling == null) {
                TreeNode<T, U> aNode = getParent();
                do {
                    if (aNode == null) {
                        return null;
                    }
                    nextSibling = aNode.getNextSibling();
                    if (nextSibling != null) {
                        return nextSibling;
                    }
                    aNode = aNode.getParent();
                } while (true);
            } else {
                return nextSibling;
            }
        } else {
            return get(0);
        }
    }

    /**
     * Returns the node that precedes this node in a preorder traversal of this node's tree. Returns <code>null</code> if this node is the first node of the traversal -- the root of the tree. This is an inefficient way to traverse the entire tree; use an enumeration, instead.
     *
     * @return the node that precedes this node in a preorder traversal, or null if this node is the first
     */
    public TreeNode<T, U> getPreviousNode() {
        TreeNode<T, U> previousSibling;
        TreeNode<T, U> myParent = getParent();
        if (myParent == null) {
            return null;
        }
        previousSibling = getPreviousSibling();
        if (previousSibling != null) {
            if (previousSibling.size() == 0) {
                return previousSibling;
            } else {
                return previousSibling.getLastLeaf();
            }
        } else {
            return myParent;
        }
    }

    public TreeNode<T, U> getFirstLeaf() {
        Iterator<TreeNode<T, U>> allChilds = getAllChildren().iterator();
        while (allChilds.hasNext()) {
            TreeNode<T, U> aChild = allChilds.next();
            if (aChild.isLeaf()) {
                return aChild;
            }
        }
        return null;
    }

    /**
     * Finds and returns the last leaf that is a descendant of this node -- either this node or its last child's last leaf. Returns this node if it is a leaf.
     *
     * @return the last leaf in the subtree rooted at this node
     * @see #isLeaf
     */
    public TreeNode<T, U> getLastLeaf() {
        List<TreeNode<T, U>> allChilds = getAllChildren(new ArrayList<>());
        for (int a = allChilds.size() - 1; a >= 0; a--) {
            TreeNode<T, U> aChild = allChilds.get(a);
            if (aChild.isLeaf()) {
                return aChild;
            }
        }
        return null;
    }

    /**
     * Returns this node's first child. If this node has no children, throws NoSuchElementException.
     *
     * @return the first child of this node
     * @throws NoSuchElementException if this node has no children
     */
    public TreeNode<T, U> getFirstChild() {
        List<TreeNode<T, U>> allChilds = getAllChildren(new ArrayList<>());
        if (allChilds.size() > 0) {
            return allChilds.get(0);
        }
        return null;
    }

    /**
     * Returns this node's last child. If this node has no children, throws NoSuchElementException.
     *
     * @return the last child of this node
     * @throws NoSuchElementException if this node has no children
     */
    public TreeNode<T, U> getLastChild() {
        List<TreeNode<T, U>> allChilds = getAllChildren(new ArrayList<>());
        if (allChilds.size() > 0) {
            return allChilds.get(allChilds.size() - 1);
        } else {
            return null;
        }
    }

    public void clearParents() {
        if (getParent() != null) {
            getParent().clearParents();
            setParent(null);
        }
    }

    public void compact(Compactor<T, U> modifier) {
        if (modifier.isRelevant(this)) {
            for (int c = 0; c < size(); c++) {
                get(c).compact(modifier);
            }
            if (modifier.compact(this)) {
                TreeNode<T, U> aParent = getParent();
                if (aParent != null) {
                    int idx = aParent.indexOf(this);
                    List<TreeNode<T, U>> removedChilds = remove();
                    if (removedChilds != null && removedChilds.size() > 0) {
                        if (idx >= 0) {
                            aParent.add(idx, removedChilds);
                        } else {
                            aParent.addChildren(removedChilds);
                        }
                    }
                }
            }
        }
    }

    public void manipulateStructure(Compactor<T, U> modifier) {
        if (modifier.canExpand()) {
            modifier.expand(this);
        }
        if (!modifier.isRelevant(this)) {
            if (getParent() != null) {
                getParent().remove(this);
            }
        } else {
            for (int c = size() - 1; c >= 0; c--) {
                get(c).manipulateStructure(modifier);
            }
        }
    }

    public void addPaths(NodePathFacade<T> facade, Collection<T> data) {
        if (facade != null) {
            if (data != null) {
                for (T item : data) {
                    String[] elems = facade.buildPath(item);
                    addNode(elems, item);
                }
            }
        }
    }

    public void addNode(String[] nodeIDPath, T data) {
        TreeNode<T, U> baseNode = this;
        for (int i = 0; i < nodeIDPath.length; i++) {
            String pNode = nodeIDPath[i];
            if (pNode != null && pNode.length() > 0) {
                TreeNode<T, U> aChild = baseNode.getChildByID(pNode);
                if (aChild == null) {
                    aChild = new TreeNode<>(pNode, null);
                    baseNode.addChild(aChild);
                }
                baseNode = aChild;
                if (i + 1 == nodeIDPath.length) {
                    aChild.setData(data);
                }
            }
        }
    }

    public <R extends TreeNode<T, U>> R addNodeUsingCreator(String[] nodeIDPath, NodeCreator creator) {
        TreeNode<T, U> baseNode = this;
        for (int i = 0; i < nodeIDPath.length; i++) {
            String pNode = nodeIDPath[i];
            if (pNode != null && pNode.length() > 0) {
                TreeNode<T, U> aChild = baseNode.getChildByID(pNode);
                if (aChild == null) {
                    aChild = creator.createNode(pNode, null);
                    baseNode.addChild(aChild);
                }
                baseNode = aChild;
            }
        }
        return (R) baseNode;
    }

    public <R extends TreeNode<T, U>> R addNode(List<R> nodeIDPath, NodeCreator creator) {
        TreeNode<T, U> baseNode = this;
        for (int i = 0; i < nodeIDPath.size(); i++) {
            R pNode = nodeIDPath.get(i);
            if (pNode != null) {
                TreeNode<T, U> aChild = baseNode.getChildByID(pNode.getID());
                if (aChild == null) {
                    aChild = creator.createNode(null, pNode);
                    baseNode.addChild(aChild);
                }
                baseNode = aChild;
            }
        }
        return (R) baseNode;
    }

    public void parse(JsonObject aDef, JSONNodeCreator creator) {
        TreeNode node = creator.createNode(this, aDef);
        Iterator<Map.Entry<String, JsonValue>> entries = aDef.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, JsonValue> aEntry = entries.next();
            JsonValue value = aEntry.getValue();
            node.parse(aEntry.getKey(), value, creator);
        }
    }

    protected void parse(String key, JsonArray array, JSONNodeCreator creator) {
        Iterator<JsonValue> objects = array.iterator();
        while (objects.hasNext()) {
            parse(key, objects.next(), creator);
        }
    }

    public void parse(JsonValue value, JSONNodeCreator creator) {
        parse(null, value, creator);
    }

    protected void parse(String key, JsonValue value, JSONNodeCreator creator) {
        boolean stop = creator.createNode(key, this, value);
        if (!stop) {
            JsonValue.ValueType type = value.getValueType();
            if (type == JsonValue.ValueType.ARRAY) {
                parse(key, value.asJsonArray(), creator);
            } else if (type == JsonValue.ValueType.OBJECT) {
                parse(value.asJsonObject(), creator);
            }
        }
    }

    public void traverseParent(Consumer<TreeNode<T, U>> a) {
        a.accept(this);
        if (getParent() != null) {
            getParent().traverseParent(a);
        }
    }
}
