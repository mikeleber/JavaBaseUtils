package org.basetools.util.tree.xpath;

import org.basetools.log.LoggerFactory;
import org.basetools.util.tree.TreeNode;
import org.jaxen.JaxenException;
import org.jaxen.UnresolvableException;
import org.jaxen.XPath;
import org.slf4j.Logger;

import javax.xml.transform.TransformerException;
import java.util.List;

public final class TreeNodeXPathExecuterImpl {
    public static long timeConsumption = 0;
    public static int counter = 0;
    private static TreeNodeXPathExecuterImpl _singleton = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(TreeNodeXPathExecuterImpl.class);

    /**
     * XPathExecuterImpl constructor comment.
     */
    private TreeNodeXPathExecuterImpl() {
        super();
    }

    /**
     * processXPath method comment.
     */
    public static TreeNodeXPathExecuterImpl getInstance() {
        if (_singleton == null) {
            _singleton = new TreeNodeXPathExecuterImpl();
            return _singleton;
        } else {
            return _singleton;
        }
    }

    public List<? extends TreeNode> processXPathJaxen(String xpath, TreeNode type)
            throws TransformerException {
        long start = System.currentTimeMillis();
        counter++;
        List<TreeNode> results = null;
        try {

            XPathTreeNodeHandler handler = new XPathTreeNodeHandler();

            DocumentNavigator navigator = new DocumentNavigator(handler);
            XPath typeXpath = new TreeNodeXPath(xpath, navigator);
            results = typeXpath.selectNodes(type);
        } catch (UnresolvableException e) {
            LOGGER.error("XPath not resolvable (" + xpath + ") Reason:" + e.getMessage());
        } catch (org.jaxen.XPathSyntaxException e) {
            LOGGER.error("XPath-Syntax Error for (" + xpath + ") Reason:" + e.getMultilineMessage());
        } catch (JaxenException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ClassCastException e) {
            LOGGER.error("Wrong result type! Get's not a  TreeNode with xpath:" + xpath);
        } catch (Exception e) {
            LOGGER.error("XPath Error for (" + xpath + ") Reason:" + e.getMessage());
        }

        long stop = System.currentTimeMillis();
        timeConsumption += (stop - start);
        return results;
    }
}
