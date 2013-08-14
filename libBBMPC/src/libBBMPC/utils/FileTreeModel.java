package libBBMPC.utils;

import javax.swing.tree.*;
import java.io.File;
import javax.swing.event.*;

/**
 * The methods in this class allow the JTree component to traverse
 * the file system tree, and display the files and directories.
 **/
public class FileTreeModel implements TreeModel {
  // We specify the root directory when we create the model.
  //	protected File root;
  private final static DefaultMutableTreeNode abs_root = new DefaultMutableTreeNode();
  private DefaultMutableTreeNode root = abs_root;

  public FileTreeModel()
  {
	  Thread t = new Thread(new Runnable(){
		  public void run()
		  {
			  //File[] roots = File.listRoots();
			  //for(File f: roots)
			//	  root.add(new DefaultMutableTreeNode(f));
			  root.add(new DefaultMutableTreeNode(new File(System.getProperty("user.home"))));
		  }
	  });
	  t.start();
  }
  // The model knows how to return the root object of the tree
  public Object getRoot() { return root; }

  // Tell JTree whether an object in the tree is a leaf or not
  public boolean isLeaf(Object node) {
	  if (node.equals(root)) {
		return false;
	  }
	  File f = (File) ((DefaultMutableTreeNode) node).getUserObject();
	  return f.isFile();
 }

  // Tell JTree how many children a node has
  public int getChildCount(Object parent) {
		if (parent.equals(root)) {
			return root.getChildCount();
		}
		File parentFile = (File) ((DefaultMutableTreeNode) parent).getUserObject();
		if (parentFile.isDirectory()) {
			int counter =0;
			for (File f: parentFile.listFiles()){
				if (f.isDirectory() || AudioFunctions.isWav(f))
					counter++;
			}
			return counter;
		} else {
			return 0;
		}
  }

  // Fetch any numbered child of a node for the JTree.
  // Our model returns File objects for all nodes in the tree.  The
  // JTree displays these by calling the File.toString() method.
  public Object getChild(Object parent, int index) {
		if (parent.equals(root)) {
			return root.getChildAt(index);
		}
		File directory = (File) ((DefaultMutableTreeNode) parent).getUserObject();
		File[] children = directory.listFiles();
		int counter=0;
		for (File f: children){
			if (f.isDirectory() || AudioFunctions.isWav(f)){
				if (counter==index)
					return new DefaultMutableTreeNode(f);
				counter++;
			}
		}
		return null;

  }

  // Figure out a child's position in its parent node.
  public int getIndexOfChild(Object parent, Object child) {
	  File directory = (File) ((DefaultMutableTreeNode) parent).getUserObject();
		File childFile = (File) ((DefaultMutableTreeNode) child).getUserObject();
		File[] children = directory.listFiles();
		for (int i = 0; i < children.length; ++i) {
			if (AudioFunctions.isWav(children[i])){
				if (childFile.equals(children[i])) {
					return i;
				}
			}
		}
		return -1;
  }

  // This method is only invoked by the JTree for editable trees.  
  // This TreeModel does not allow editing, so we do not implement 
  // this method.  The JTree editable property is false by default.
  public void valueForPathChanged(TreePath path, Object newvalue) {}

  // Since this is not an editable tree model, we never fire any events,
  // so we don't actually have to keep track of interested listeners.
  public void addTreeModelListener(TreeModelListener l) {}
  public void removeTreeModelListener(TreeModelListener l) {}

  public void collapseDown(File fa){
	  if (fa==null || !fa.exists())
		  return;
	  DefaultMutableTreeNode oldRoot = root;
	  root = new DefaultMutableTreeNode(fa);
	  for (File f:fa.listFiles()){
		  if (f.isDirectory() || AudioFunctions.isWav(f))
			  root.add(new DefaultMutableTreeNode(f));
	  }
	  if (root.getChildCount()==0){
		  root.removeAllChildren();
		  root = oldRoot;
	  }
  }
  public void expandUp(){
	  if (root!= abs_root){
		  root = new DefaultMutableTreeNode(((File)root.getUserObject()).getParentFile());
		  if (root.getUserObject() == null){
			  root=abs_root;
		  	  return;
	  	  }	  
		  for(File f:((File)root.getUserObject()).listFiles()){
			  if (f.isDirectory() || AudioFunctions.isWav(f))
				  root.add(new DefaultMutableTreeNode(f));
		  }
		  
	  }
  }
  public void refreshBranch(DefaultMutableTreeNode n){
	  n.removeAllChildren();
	  int count = getChildCount(n);
	  for (int i=0; i<count; i++){
		  n.add((DefaultMutableTreeNode)getChild(n,i));
	  }
  }
}


