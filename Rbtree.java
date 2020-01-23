
public class Rbtree {

    private final int RED = 0;
    private final int BLACK = 1;

    class Node {

        Job key ;
        int color = BLACK;
        Node left = nil, right = nil, parent = nil;

        Node(Job key) {
            this.key = key;
        } 
    }

    private final Node nil = new Node(new Job(0,0,0)); 
    private Node root = nil;

    public void printTree(Node node) {
        if (node == nil) {
            return;
        }
        printTree(node.left);
        System.out.print(((node.color==RED)?"Color: Red ":"Color: Black ")+"Key: "+node.key.jobId+" Parent: "+node.parent.key.jobId+"\n");
        printTree(node.right);
    }

    public Node findNode(Node findNode, Node node) {
        if (root == nil) {
            return null;
        }

        if (findNode.key.jobId < node.key.jobId) {
            if (node.left != nil) {
                return findNode(findNode, node.left);
            }
        } else if (findNode.key.jobId > node.key.jobId) {
            if (node.right != nil) {
                return findNode(findNode, node.right);
            }
        } else if (findNode.key.jobId == node.key.jobId) {
            return node;
        }
        return null;
    }

    public void insert(Job job) {
    	Node node = new Node(job);
        Node temp = root;
        if (root == nil) {
            root = node;
            node.color = BLACK;
            node.parent = nil;
        } else {
            node.color = RED;
            while (true) {
                if (node.key.jobId < temp.key.jobId) {
                    if (temp.left == nil) {
                        temp.left = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.left;
                    }
                } else if (node.key.jobId >= temp.key.jobId) {
                    if (temp.right == nil) {
                        temp.right = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.right;
                    }
                }
            }
            fixTree(node);
        }
    }

    //Takes as argument the newly inserted node
    private void fixTree(Node node) {
        while (node.parent.color == RED) {
            Node uncle = nil;
            if (node.parent == node.parent.parent.left) {
                uncle = node.parent.parent.right;

                if (uncle != nil && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                    continue;
                } 
                if (node == node.parent.right) {
                    //Double rotation needed
                    node = node.parent;
                    rotateLeft(node);
                } 
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                //if the "else if" code hasn't executed, this
                //is a case where we only need a single rotation 
                rotateRight(node.parent.parent);
            } else {
                uncle = node.parent.parent.left;
                 if (uncle != nil && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.left) {
                    //Double rotation needed
                    node = node.parent;
                    rotateRight(node);
                }
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                //if the "else if" code hasn't executed, this
                //is a case where we only need a single rotation
                rotateLeft(node.parent.parent);
            }
        }
        root.color = BLACK;
    }

    void rotateLeft(Node node) {
        if (node.parent != nil) {
            if (node == node.parent.left) {
                node.parent.left = node.right;
            } else {
                node.parent.right = node.right;
            }
            node.right.parent = node.parent;
            node.parent = node.right;
            if (node.right.left != nil) {
                node.right.left.parent = node;
            }
            node.right = node.right.left;
            node.parent.left = node;
        } else {//Need to rotate root
            Node right = root.right;
            root.right = right.left;
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = nil;
            root = right;
        }
    }

    void rotateRight(Node node) {
        if (node.parent != nil) {
            if (node == node.parent.left) {
                node.parent.left = node.left;
            } else {
                node.parent.right = node.left;
            }

            node.left.parent = node.parent;
            node.parent = node.left;
            if (node.left.right != nil) {
                node.left.right.parent = node;
            }
            node.left = node.left.right;
            node.parent.right = node;
        } else {//Need to rotate root
            Node left = root.left;
            root.left = root.left.right;
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = nil;
            root = left;
        }
    }

    //Deletes whole tree
    void deleteTree(){
        root = nil;
    }
    
    //Deletion Code .
    
    //This operation doesn't care about the new Node's connections
    //with previous node's left and right. The caller has to take care
    //of that.
    void transplant(Node target, Node with){ 
          if(target.parent == nil){
              root = with;
          }else if(target == target.parent.left){
              target.parent.left = with;
          }else
              target.parent.right = with;
          with.parent = target.parent;
    }
    
    boolean delete(Job jb){
    	Node z = new Node(jb);
        if((z = findNode(z, root))==null)
        	return false;
        Node x;
        Node y = z; // temporary reference y
        int y_original_color = y.color;
        
        if(z.left == nil){
            x = z.right;  
            transplant(z, z.right);  
        }else if(z.right == nil){
            x = z.left;
            transplant(z, z.left); 
        }else{
            y = treeMinimum(z.right);
            y_original_color = y.color;
            x = y.right;
            if(y.parent == z)
                x.parent = y;
            else{
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color; 
        }
        if(y_original_color==BLACK)
            deleteFixup(x);  
        return true;
    }
    
    void deleteFixup(Node x){
        while(x!=root && x.color == BLACK){ 
            if(x == x.parent.left){
                Node w = x.parent.right;
                if(w.color == RED){
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if(w.left.color == BLACK && w.right.color == BLACK){
                    w.color = RED;
                    x = x.parent;
                    continue;
                }
                else if(w.right.color == BLACK){
                    w.left.color = BLACK;
                    w.color = RED;
                    rotateRight(w);
                    w = x.parent.right;
                }
                if(w.right.color == RED){
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            }else{
                Node w = x.parent.left;
                if(w.color == RED){
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if(w.right.color == BLACK && w.left.color == BLACK){
                    w.color = RED;
                    x = x.parent;
                    continue;
                }
                else if(w.left.color == BLACK){
                    w.right.color = BLACK;
                    w.color = RED;
                    rotateLeft(w);
                    w = x.parent.left;
                }
                if(w.left.color == RED){
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK; 
    }
    
    Node treeMinimum(Node subTreeRoot){
        while(subTreeRoot.left!=nil){
            subTreeRoot = subTreeRoot.left;
        }
        return subTreeRoot;
    }
    
    Job findUtil(int node){
    	Node nd = findNode(new Node(new Job(node,-1,-1)),root);
        if(nd == null) return new Job(0,0,0);
        return nd.key;
    }
    
    boolean hasNext(Node node){
    	if(node.left == nil && node.right == nil)
    		return false;
    	return true;
    }
    
    Node inorderSuccessor(Node root,Node curr){
    	if(curr.right != nil)
    		return minValue(curr.right);
    	Node p = curr.parent;
    	while(p!=nil && curr.key.jobId == p.right.key.jobId){
    		curr = p;
    		p = p.parent;
    	}
    	return p;
    }
    
    Node minValue(Node node){
    	Node curr = node;
    	while(curr.left!=nil){
    		curr = curr.left;
    	}
    	return curr;
    }
    
    public Node findGreaterNode(Node findNode, Node node) {
        if (root == nil) {
            return null;
        }

        if (findNode.key.jobId < node.key.jobId) {
            if (node.left != nil) {
                return findGreaterNode(findNode, node.left);
            }
            else{
            	return node;
            }
        } else if (findNode.key.jobId > node.key.jobId) {
            if (node.right != nil) {
                return findGreaterNode(findNode, node.right);
            }
            else if(node.parent.key.jobId > findNode.key.jobId){
            	return node.parent;
            }
            else{
            	return null;
            }
        } else if (findNode.key.jobId == node.key.jobId) {
            return inorderSuccessor(root, node);
        }
        return null;
    }
    
    Job next(int job){
    	Node node = findGreaterNode(new Node(new Job(job,1,1)), root);
    	if(node == null){
                    return new Job(0,0,0);
        }
    	return node.key;
    }
    
    Node nextNode(int job,boolean flag){
        if(findNode(new Node(new Job(job,-1,-1)),root) != null && flag)    return findNode(new Node(new Job(job,-1,-1)),root);
    	Node node = findGreaterNode(new Node(new Job(job,1,1)), root);
    	if(node == null){
    		return new Node(new Job(0,0,0));
    	}
    	return node;
    	
    }
    
    Node nextNode(Node node){
        Node nextNodeTree = inorderSuccessor(root, node); 
        return nextNodeTree;
    }
    
    Node maxValue(Node node){
    	Node curr = node;
    	while(curr.right != nil){
    		curr = curr.right;
    	}
    	return curr;
    }
    
    Node inorderPredecessor(Node root,Node curr){
    	if(curr.left != nil){
    		return maxValue(curr.left);
    	}
    	Node p = curr.parent;
    	while(p!=nil && curr.key.jobId == p.left.key.jobId){
    		curr = p;
    		p = p.parent;
    	}
    	return p;
    }
    
    Node findSmallerNode(Node findNode,Node node){
    	if(root == nil)
    		return null;
    	if(findNode.key.jobId < node.key.jobId){
    		if(node.left != nil){
    			return findSmallerNode(findNode, node.left);
    		}
    		else if(node.parent.key.jobId < node.key.jobId){
    			return node.parent;
    		}
    		else{
    			return null;
    		}
    	}
    	else if(findNode.key.jobId > node.key.jobId){
    		if(node.right != nil){
    			return findSmallerNode(findNode, node.right);
    		}
    		else{
    			return node;
    		}
    	}
    	else if(findNode.key.jobId == node.key.jobId){
    		return inorderPredecessor(root,node);
    	}
    	return null;
    }
    
    Job prev(int job){
    	Node node = findSmallerNode(new Node(new Job(job,1,1)), root);
    	if(node == null){
    		return new Job(0,0,0);
    	}
    	return node.key;
    }
    
//    public static void main(String[] args) {
//        Rbtree rbt = new Rbtree();
//        Job job1 = new Job(12,1,1);
//        Job job2 = new Job(5,1,1);
//        Job job3 = new Job(3,1,1);
//        Job job4 = new Job(6,1,1);
//        Job job5 = new Job(9,1,1);
//        Job job6 = new Job(8,1,1);
//        rbt.insert(job1);
//        rbt.insert(job2);
//        rbt.insert(job3);
//        rbt.insert(job4);
//        rbt.insert(job5);
//        rbt.insert(job6);
//        rbt.printTree(rbt.root);
//        System.out.println(rbt.next(2).jobId);
//        System.out.println(rbt.prev(200).jobId);
//    }
}