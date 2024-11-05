package com.vlad;
import java.util.ArrayList;
import java.util.Objects;

public class MyNode {
    public int x;
    public int y;
    public float value;
    public ArrayList<MyNode> pivNodes = new ArrayList<MyNode>();

    public MyNode(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyNode other = (MyNode) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    private void rightsGeneration(MyNode leftNode, int maxSize){
        if (leftNode.x +1<maxSize) {
            MyNode rightNode = new MyNode(leftNode.x + 1, leftNode.y);
            rightNode.pivNodes.add(leftNode);
            leftNode.pivNodes.add(rightNode);
            rightsGeneration(rightNode, maxSize);
        }
    }

    private ArrayList<MyNode> getLeftRighArrayList(MyNode leftNode, int maxSize){
        ArrayList<MyNode> arrayList = new ArrayList<MyNode>();
        arrayList.add(leftNode);
        leftNode = leftNode.pivNodes.get(0);
        for (int i = 1; i<maxSize; i++){
            arrayList.add(leftNode);
            if (leftNode.pivNodes.size() == 1){
                break;
            }
            leftNode = leftNode.pivNodes.get(1);
        }
        return arrayList;
    }

    private void connectArrays(ArrayList<MyNode> arr1, ArrayList<MyNode> arr2){
        for (int i = 0; i < arr1.size(); i++){
            arr1.get(i).pivNodes.add(arr2.get(i));
            arr2.get(i).pivNodes.add(arr1.get(i));
        }
    }

    public ArrayList<ArrayList<MyNode>> generateGraph(int maxSize){
        ArrayList<ArrayList<MyNode>> allGraph = new ArrayList<ArrayList<MyNode>>();
        rightsGeneration(this, maxSize);
        ArrayList<MyNode> arrayNodeList = new ArrayList<MyNode>();
        arrayNodeList.add(this);
        for (int i = 1; i < maxSize; i++){
            MyNode node = new MyNode(0, i);
            rightsGeneration(node, maxSize);
            arrayNodeList.add(node);
        }
        
        for (int i = 0; i<maxSize; i++){
            allGraph.add(getLeftRighArrayList(arrayNodeList.get(i), maxSize));
        }
        for (int i = 0; i<maxSize-1; i++){
            connectArrays(allGraph.get(i), allGraph.get(i+1));
        }
        return allGraph;
    }
}
