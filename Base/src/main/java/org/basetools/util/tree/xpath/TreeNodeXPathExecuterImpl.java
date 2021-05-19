package org.basetools.util.tree.xpath;

import org.basetools.util.tree.TreeNode;
import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import java.util.List;

public final class TreeNodeXPathExecuterImpl {
    private static final XPathTreeNodeHandler DEFAULT_HANDLER = new XPathTreeNodeHandler();
    public static long timeConsumption = 0;
    public static int counter = 0;
    private static TreeNodeXPathExecuterImpl _singleton = null;

    private TreeNodeXPathExecuterImpl() {
        super();
    }

    public static TreeNodeXPathExecuterImpl getInstance() {
        if (_singleton == null) {
            _singleton = new TreeNodeXPathExecuterImpl();
            return _singleton;
        } else {
            return _singleton;
        }
    }

    public List<? extends TreeNode> processXPathJaxen(String xpath, TreeNode type) {
        TreeDocumentNavigator navigator = new TreeDocumentNavigator(DEFAULT_HANDLER);
        return processXPathJaxen(navigator, xpath, type);
    }

    public List<? extends TreeNode> processXPathJaxen(DefaultNavigator navigator, String xpath, TreeNode type) {
        long start = System.currentTimeMillis();
        counter++;
        List<TreeNode> results = null;
        try {
            XPath typeXpath = new TreeNodeXPath(xpath, navigator);
            results = typeXpath.selectNodes(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        timeConsumption += (stop - start);
        return results;
    }
}
