package com.icalab.deadcode;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;
import soot.util.dot.DotGraphUtility;
import soot.util.dot.Renderable;

public class DotGraph implements Renderable
{
public final static String DOT_EXTENSION = ".dot";
private HashMap<String, DotGraphNode> nodes;
private boolean isSubGraph;
private List<Renderable> drawElements;
private String graphname;
public DotGraph(String graphname)
{
this.drawElements = new LinkedList<Renderable>();
}
public DotGraph createSubGraph(String label)
{
DotGraph subgraph = new DotGraph(label);
subgraph.isSubGraph = true;
this.drawElements.add(subgraph);
System.out.println(subgraph);
return subgraph;
}
@Override
public void render(OutputStream out, int indent) throws IOException {
String graphname = this.graphname;
    if (!isSubGraph) {
      DotGraphUtility.renderLine(out, "digraph \""+graphname+"\" {",
indent);
    } else {
 DotGraphUtility.renderLine(out, "subgraph \""+graphname+"\" {", indent);

    }
 }
public void plot(String filename) {
    try {
      BufferedOutputStream out =new BufferedOutputStream(new
FileOutputStream(filename));
      render(out, 0);
      out.close();
    } catch (IOException ioe) {
    }
  }
public DotGraphNode getNode(String name){
DotGraphNode node = nodes.get(name);
if (node == null) {
node = new DotGraphNode(name);
nodes.put(name, node);
}
return node;
}
public DotGraphNode drawNode(String name){
DotGraphNode node = getNode(name);
if(node == null)
throw new RuntimeException("Assertion failed.");
if(!this.drawElements.contains(node))
this.drawElements.add(node);
return node;
  }

public DotGraphEdge drawEdge(String from, String to) {
DotGraphNode src = drawNode(from);
DotGraphNode dst = drawNode(to);
DotGraphEdge edge = new DotGraphEdge(src, dst);
this.drawElements.add(edge);
return edge;

  }
}