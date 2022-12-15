package com.mj;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import javax.lang.model.util.ElementScanner6;
import javax.management.Query;

import com.mj.printer.BinaryTreeInfo;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeTableCell;

@SuppressWarnings("unchecked")
public class BinarySearchTree<E> implements BinaryTreeInfo {
	private int size;
	private Node<E> root;
	private Comparator<E> comparator;
	
	public BinarySearchTree() {
		this(null);
	}
	
	public BinarySearchTree(Comparator<E> comparator) {
		this.comparator = comparator;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		root = null;
		size = 0;
	}

	public void add(E element) {
		elementNotNullCheck(element);

		// 添加第一个节点
		if ( root == null ) {
			root = new Node<>(element, null);
			size++;
			return;
		}

		// 添加的不是第一个节点
		// 找到父节点
		Node<E> parent = root;
		Node<E> node = root;
		int cmp = 0;

		do {
			cmp = compare(element, node.element);
			parent = node; // 记录父节点，这很重要,因为下面根据父节点去插入新的节点
			if ( cmp > 0 ) {
				node = node.right;
			} else if (cmp < 0) {
				node = node.left;
			} else {
				// 相等
				node.element = element;
				return;
			}
		} while ( node != null );


		// 看看插入到父节点的哪个位置
		Node<E> newNode = new Node<>(element, parent);
		if (cmp > 0) {
			parent.right = newNode;
		} else {
			parent.left = newNode;
		}
		size++;
	}

	private Node<E> node(E element) {
		Node<E> node = root;
		while(node != null) {
			int cmp = compare(element, node.element);
			if (cmp == 0) return node;
			if (cmp > 0) {
				node = node.right;
			} else {
				node = node.left;
			}
		}
		return null;
	}

	/**
	 * @return 返回值等于0，代表e1和e2相等；返回值大于0，代表e1大于e2；返回值小于于0，代表e1小于e2
	 */
	private int compare(E e1, E e2) {
		if (comparator != null) {
			return comparator.compare(e1, e2);
		}
		return ((Comparable<E>)e1).compareTo(e2);
	}

	private static class Node<E> {
		E element;
		Node<E> left;
		Node<E> right;
		Node<E> parent;
		public Node(E element, Node<E> parent) {
			this.element = element;
			this.parent = parent;
		}
		
		public boolean isLeaf() {
			return left == null && right == null;
		}
		
		public boolean hasTwoChildren() {
			return left != null && right != null;
		}
	}


	@Override
	public Object root() {
		return root;
	}

	@Override
	public Object left(Object node) {
		return ((Node<E>)node).left;
	}

	@Override
	public Object right(Object node) {
		return ((Node<E>)node).right;
	}

	@Override
	public Object string(Object node) {
		Node<E> myNode = (Node<E>)node;
		String parentString = "null";
		if (myNode.parent != null) {
			parentString = myNode.parent.element.toString();
		}
		return myNode.element + "_p(" + parentString + ")";
	}

	private void elementNotNullCheck(E element) {
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
	}

	public static abstract class Visitor<E> {
		boolean stop;
		/**
		 * @return 如果返回true，就代表停止遍历
		 */
		public abstract boolean visit(E element);
	}

	public void preorder(Visitor<E> visitor) {
		if (visitor == null) return;
		preorder(root, visitor); 
	}

	private void preorder(Node<E> node, Visitor<E> visitor) {
		if (node == null || visitor.stop) return;
		
		visitor.stop = visitor.visit(node.element);
		preorder(node.left, visitor);
		preorder(node.right, visitor);
	}

	public void inorder(Visitor<E> visitor) {
		if (visitor == null) return;
		inorder(root, visitor); 
	}

	private void inorder(Node<E> node, Visitor<E> visitor) {
		if (node == null || visitor.stop) return;
		
		inorder(node.left, visitor);
		if (visitor.stop) return;
		visitor.stop = visitor.visit(node.element);
		inorder(node.right, visitor);
	}

    public void postorder(Visitor<E> visitor) {
		if (visitor == null) return;
		postorder(root, visitor); 
	}

	private void postorder(Node<E> node, Visitor<E> visitor) {
		if ( node == null || visitor.stop ) return;
		
		postorder(node.left, visitor);
		postorder(node.right, visitor);
		if (visitor.stop) return;
		visitor.stop = visitor.visit(node.element); 
	}

	public void levelOrder(Visitor<E> visitor) {
		if (root == null || visitor == null) return; 

		Queue<Node<E>> queue = new LinkedList<>();
		queue.offer(root);

		while(!queue.isEmpty()) {
			Node<E> node = queue.poll();
			if (visitor.visit(node.element)) return;
			
			if ( node.left != null ) {
				queue.offer(node.left);
			}

			if ( node.right != null ) {
				queue.offer(node.right);
			}
		}
	}
	// // 前序遍历
	// public void preorderTraversal() {
	// 	preorderTraversal(root);
	// }

	// private void preorderTraversal(Node<E> node) {
	// 	if (node == null) return;
		
	// 	System.out.println(node.element);
	// 	preorderTraversal(node.left);
	// 	preorderTraversal(node.right);
	// }

	// // 中序遍历
	// public void inorderTraversal() {
	// 	inorderTraversal(root);
	// }

	// private void inorderTraversal(Node<E> node) {
	// 	if (node == null) return;
		
	// 	inorderTraversal(node.left);
	// 	System.out.println(node.element);
	// 	inorderTraversal(node.right);
	// }

	// // 后序遍历
	// public void postorderTraversal() {
	// 	postorderTraversal(root);
	// }

	// private void postorderTraversal(Node<E> node) {
	// 	if (node == null) return;
	// 	postorderTraversal(node.left);
	// 	postorderTraversal(node.right);
	// 	System.out.println(node.element);
	// }

	// // 层序遍历
	// public void levelOrderTraversal() {
	// 	if (root == null) return;
		
	// 		Queue<Node<E>> queue = new LinkedList<>();
	// 		queue.offer(root);

	// 		while(!queue.isEmpty()) {
	// 			Node<E> node = queue.poll();
	// 			System.out.println(node.element);

	// 			if (node.left != null) {
	// 				queue.offer(node.left);
	// 			}

	// 			if (node.right != null) {
	// 				queue.offer(node.right);
	// 			}
	// 		}
	// }
		
	// public boolean isComplete() {
	// 	if ( root == null ) return false;
		
	// 	Queue<Node<E>> queue = new LinkedList<>();
	// 	queue.offer(root);

	// 	boolean leaf = false;

	// 	while(!queue.isEmpty()) {
	// 		Node<E> node = queue.poll();
	// 		if (leaf && !node.isLeaf()) return false;

	// 		if (node.left != null && node.right != null) {
	// 			queue.offer(node.left);
	// 			queue.offer(node.right);
	// 		} else if (node.left == null && node.right != null) {
	// 			return false;
	// 		} else { // 后面遍历的节点必须是叶子节点
	// 			leaf = true;
	// 			if ( node.left != null ) {
	// 				queue.offer(node.left);
	// 			}
	// 		}
	// 	}

	// 	return true;
	// }

	public boolean isComplete() {
		if ( root == null ) return false;

		Queue<Node<E>> queue = new LinkedList<>();
		queue.offer(root);

		boolean leaf = false;
		while(!queue.isEmpty()) {
			Node<E> node = queue.poll();
			if (leaf && !node.isLeaf()) return false;

			if (node.left != null) {
				queue.offer(node.left);
			} else if(node.right != null) { // node.left == null && node.right != null
				return false;
			}

			// 这里注意， 当 node.left != null s时，会到下面的逻辑中来
			if (node.right != null) {
				queue.offer(node.right);
			} else { // node.right = null
				leaf = true;
			}
		}

		return true;
	}

	@SuppressWarnings("unused")
	private Node<E> predecessor(Node<E> node) {
		if (node == null) return null; 

		// 前驱节点在左字树当中 (left.right.right...)
		Node<E> p = node.left;
		if (p != null) {
			while (p.right != null) {
				p = p.right;
			}
			return p;
		}

		// 从父节点，祖父节点中找寻前驱节点
		while(node.parent != null && node == node.parent.left ) {
			node = node.parent;
		}

		return node.parent;
	}
	
	@SuppressWarnings("unused")
	private Node<E> successor(Node<E> node) {
		if (node == null) return null;
		
		// 后继节点在右子树当中 (right.left.left.left...)
		Node<E> p = node.right;
		if (p != null) {
			while(p.left != null) {
				p = p.left;
			}
			return p;
		}

		// 从父节点，祖父节点中寻找后继节点
		while(node.parent != null && node == node.parent.right) {
			node = node.parent;
		}

		return node.parent;
	}

	public int height() {
		if (root == null) return 0;
		
		// 树的高度 
		int height = 0;
		// 存储着每一层的元素数量
		int levelSize = 1;
		Queue<Node<E>> queue = new LinkedList<>();
		queue.offer(root);

		while(!queue.isEmpty()) {
			Node<E> node = queue.poll();
			levelSize--;

			if (node.left != null) {
				queue.offer(node.left);
			}

			if (node.right != null) {
				queue.offer(node.right);
			}

			if (levelSize == 0) { // 意味着即将要访问下一层
				levelSize = queue.size();
				height++;
			}
		}

		return height;
	}

	public int height2() {
		return height(root);
	}

	private int height(Node<E> node) {
		if (node == null) return 0;
		return 1 + Math.max(height(node.left), height(node.right));
	}
	
}
