package induction;

import java.util.*;
import fig.basic.*;
import edu.berkeley.nlp.ling.*;
import edu.berkeley.nlp.util.*;

public class DepTree {
  int[] parent;
  int[] numLeftChildren;
  int[] numRightChildren;
  int[] childRank; // rank of this child
  int rootPosition;
  int N;

  public DepTree(int N) {
    this.parent = ListUtils.newInt(N, -1);
    this.N = N;
  }

  public DepTree(int[] parent) {
    this.parent = parent;
    this.N = parent.length;
    finish();
  }

  void finish() {
    this.numLeftChildren = new int[N];
    this.numRightChildren = new int[N];
    this.childRank = new int[N];
    for(int i = 0; i < N; i++) {
           if(i < parent[i]) childRank[i] = numLeftChildren[parent[i]]++;
      else if(i > parent[i]) childRank[i] = numRightChildren[parent[i]]++;
    }
    for(int i = 0; i < N; i++) {
      if(i < parent[i])
        childRank[i] = numLeftChildren[parent[i]] - childRank[i];
      else if (i > parent[i])
        childRank[i]++;
      else if(i == parent[i])
        rootPosition = i;
    }
  }

  boolean isRoot(int i) { return parent[i] == i; }
  boolean isLeftChild(int i) { return i < parent[i]; }
  boolean isRightChild(int i) { return i > parent[i]; }
  boolean isFirstChild(int i) { return childRank[i] == 1; }
  boolean isLastChild(int i) {
    if(i < parent[i]) return childRank[i] == numLeftChildren[parent[i]];
    if(i > parent[i]) return childRank[i] == numRightChildren[parent[i]];
    throw Exceptions.bad;
  }

  static class Converter {
    int[] parent;
    int currPosition;
    static final HeadFinder headFinder = new CollinsHeadFinder();
    public DepTree toDepTree(Tree<String> tree) {
      this.parent = new int[tree.getYield().size()];
      this.currPosition = 0;
      helper(tree);
      return new DepTree(parent);
    }
    // Return position of head
    public int helper(Tree<String> tree) {
      if(tree.isLeaf()) { return currPosition++; }
      int C = tree.getChildren().size();
      int[] headPositions = new int[C];
      for(int c = 0; c < C; c++)
        headPositions[c] = helper(tree.getChildren().get(c));
      int headc = tree.getChildren().indexOf(headFinder.determineHead(tree));
      for(int c = 0; c < C; c++)
        parent[headPositions[c]] = headPositions[headc];
      return headPositions[headc];
    }
  }

  public String toString(int[] words, fig.basic.Indexer<String> wordIndexer) {
    StringBuilder buf = new StringBuilder();
    for(int i = 0; i < N; i++) {
      if(i > 0) buf.append(' ');
      buf.append(i+":"+wordIndexer.getObject(words[i])+"<-"+parent[i]);
    }
    return buf.toString();
  }
}
