package com.vlad;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class QLearning {
    private Random rnd = new Random();
    public int catXposition;
    public int catYposition;
    private int mouseXposition;
    private int mouseYposition;
    public float [][] Q;
    private float gamma;
    private MyNode myNode = new MyNode(0, 0);
    private ArrayList<ArrayList<MyNode>> graph = new ArrayList<ArrayList<MyNode>>();
    private ArrayList<MyNode> catMemory = new ArrayList<MyNode>();
    @SuppressWarnings("unused")
    private int fieldSize;
    private Stack<MyNode> path = new Stack<MyNode>();
    public boolean isMouseChatched = false;
    public float maxDeltaQ = 0;

    public void catMemoryReset(){
        maxDeltaQ = 0;
        isMouseChatched = false;
        path.clear();
        catMemory.clear();
        // catXposition = rnd.nextInt(fieldSize);
        // catYposition = rnd.nextInt(fieldSize);
        catXposition = 4;
        catYposition = 4;
        if (catXposition == 0 && catYposition == 0){
            catXposition ++;
        }
        catMemory.add(graph.get(catYposition).get(catXposition));
    }


    public QLearning(float gamma, int mouseXposition, int mouseYposition, int fieldSize){
        this.gamma = gamma;
        this.fieldSize = fieldSize;
        this.mouseXposition = mouseXposition;
        this.mouseYposition = mouseYposition;
        this.Q = new float[fieldSize][fieldSize];
        this.Q[mouseXposition][mouseYposition] = 100;
        myNode.value = 100;
        graph = myNode.generateGraph(fieldSize);
        catMemoryReset();
    }


    private ArrayList<MyNode> maxValueMove(ArrayList<MyNode> possibleMoves){
        MyNode bestMove = possibleMoves.get(0);
        for (MyNode move : possibleMoves){
            if (move.value > bestMove.value){
                bestMove = move;
            }
        }
        if (bestMove.value == (float)0){
            return possibleMoves;
        }
        ArrayList<MyNode> bestMoves = new ArrayList<MyNode>();
        bestMoves.add(bestMove);
        return bestMoves;
    }

    private ArrayList<MyNode> getPossibleMoves(){
        ArrayList<MyNode> possibleMoves = new ArrayList<MyNode>();
        MyNode currentPosition = catMemory.getLast();
        for (MyNode possibelPosition : currentPosition.pivNodes){
            if (!possibleMoves.contains(possibelPosition) && !catMemory.contains(possibelPosition)){
                possibleMoves.add(possibelPosition);
            }
        }
        if (possibleMoves.size()==0){
            path.pop();
            possibleMoves.add(path.pop());
        }

        return possibleMoves;
    }

    private void chanegeQDelta(float lastQValue, float newQValue){
        float result = Math.max(maxDeltaQ, (newQValue - lastQValue)/newQValue * 100);
        if (!Double.isNaN(result)){
            maxDeltaQ = result;
        }
    }

    public MyNode randomCatMove(){
        ArrayList<MyNode> possibleMoves = getPossibleMoves();
        possibleMoves = maxValueMove(possibleMoves);
        MyNode nextCatPosition = possibleMoves.get(rnd.nextInt(possibleMoves.size()));
        catMemory.add(nextCatPosition);
        path.push(nextCatPosition);
        MyNode pastMove = catMemory.get(catMemory.size()-2);
        chanegeQDelta(pastMove.value, nextCatPosition.value);
        Q[pastMove.x][pastMove.y] = (pastMove.value + gamma * nextCatPosition.value);
        pastMove.value = Q[nextCatPosition.x][nextCatPosition.y];
        
        if (catMemory.getLast() == graph.get(mouseXposition).get(mouseYposition)){
            isMouseChatched = true;
            Q[nextCatPosition.x][nextCatPosition.y] = (nextCatPosition.value + gamma*nextCatPosition.value);
            nextCatPosition.value = Q[nextCatPosition.x][nextCatPosition.y];
        }
        return nextCatPosition;
    }



}
