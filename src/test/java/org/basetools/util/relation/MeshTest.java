package org.basetools.util.relation;

import org.basetools.util.io.FileUtils;
import org.basetools.util.json.JSONUtilities;
import org.basetools.util.mesh.Mesh;
import org.basetools.util.mesh.RelationalTreeNode;
import org.basetools.util.mesh.creator.RelationalJSONNodeCreator;
import org.basetools.util.mesh.cytoscape.CytoscapeTreeVisitor;
import org.basetools.util.mesh.graphviz.GraphvizTreeVisitor;
import org.basetools.util.mesh.springy.SpringyTreeVisitor;
import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class MeshTest {

    @Test
    void toMermaidGraph() throws IOException, TransformerException {
        String json = FileUtils.readAsStringFromClass(this.getClass(), "systems.json", "utf-8");
        RelationalTreeNode node = new RelationalTreeNode();
        node.setMesh(new Mesh());
        node.getMesh().addRoot(node);
        JsonObject aDef = JSONUtilities.createJson(json);
        node.parse(aDef.entrySet().iterator().next().getValue(), new RelationalJSONNodeCreator(node.getMesh()));
        node.initialize();
        node.hasRelations();
        node.findRelations();
        //  System.out.println(node.getMesh().toMermaidGraph(1));
//        System.out.println("1:" + node.getMesh().toMermaidGraph2(1));
//        System.out.println("1:" + node.getMesh().toMermaidGraphSubsystem(1));
//        System.out.println("2:" + node.getMesh().toMermaidGraph2(2));

        System.out.println(MermaidSubsystemTreeVisitor.toGraph(1, node.getMesh().createSystemTree(Integer.MAX_VALUE, null)));
        List<RelationalTreeNode> selection = new ArrayList<>();
        System.out.println(GraphvizTreeVisitor.toGraph(9, node.getMesh().createSystemTree(9, new HashSet<>(selection)), null));
        //  System.out.println("3:" + node.getMesh().toMermaidGraph2(3));
        // System.out.println("4:" + node.getMesh().toMermaidGraph2(3));
        // System.out.println(json);
    }

    @Test
    void toGraphvizGraphWithSelection() throws IOException, TransformerException {
        String json = FileUtils.readAsStringFromClass(this.getClass(), "systems.json", "utf-8");
        RelationalTreeNode node = new RelationalTreeNode();
        node.setMesh(new Mesh());
        node.getMesh().addRoot(node);
        JsonObject aDef = JSONUtilities.createJson(json);
        node.parse(aDef.entrySet().iterator().next().getValue(), new RelationalJSONNodeCreator(node.getMesh()));
        node.initialize();

        List<RelationalTreeNode> selection = (List<RelationalTreeNode>) TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("/Syrius", node);
        System.out.println(GraphvizTreeVisitor.toGraph(9, node.getMesh().createSystemTree(9, new HashSet<>(selection)), null));
    }

    @Test
    void toStringyGraph() throws IOException, TransformerException {
        String json = FileUtils.readAsStringFromClass(this.getClass(), "systems.json", "utf-8");
        RelationalTreeNode node = new RelationalTreeNode();
        node.setMesh(new Mesh());
        node.getMesh().addRoot(node);
        JsonObject aDef = JSONUtilities.createJson(json);
        node.parse(aDef.entrySet().iterator().next().getValue(), new RelationalJSONNodeCreator(node.getMesh()));
        node.initialize();

        List<RelationalTreeNode> selection = (List<RelationalTreeNode>) TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("/*", node);
        System.out.println(SpringyTreeVisitor.toGraph(9, node.getMesh().createSystemTree(9, new HashSet<>(selection))));
    }

    @Test
    void toCytoscapeGraph() throws IOException, TransformerException {
        String json = FileUtils.readAsStringFromClass(this.getClass(), "systems.json", "utf-8");
        RelationalTreeNode node = new RelationalTreeNode();
        node.setMesh(new Mesh());
        node.getMesh().addRoot(node);
        JsonObject aDef = JSONUtilities.createJson(json);
        node.parse(aDef.entrySet().iterator().next().getValue(), new RelationalJSONNodeCreator(node.getMesh()));
        node.initialize();

        List<RelationalTreeNode> selection = (List<RelationalTreeNode>) TreeNodeXPathExecuterImpl.getInstance().processXPathJaxen("/*", node);
        System.out.println(CytoscapeTreeVisitor.toGraph(9, node.getMesh().createSystemTree(9, new HashSet<>(selection))));
    }
}
