package com.pds.p2p.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TreeNode {
    private static final String GET_DEPTH_KEY = "_getDepth_";

    private static final String PATH_CODE_CODE = "_pathCode_";

    private static final String LEAF_NODES_KEY = "_leafNodes_";

    private static final String MAX_LEN_PATH_TREE_NODES_KEY = "_maxLenPathTreeNodes_";

    private static final String TO_LIST_KEY = "_toList_";

    private static final String COUNT_LEAF_KEY = "_countLeaf_";

    public static void main(String[] args) {
        TreeNode node = new TreeNode();
        TreeNode node1 = new TreeNode();

        node1.addChild(new TreeNode());
        node1.addChild(new TreeNode());
        node1.addChild(new TreeNode());

        node.addChild(new TreeNode());
        node.addChild(new TreeNode());
        node.addChild(new TreeNode());
        node.addChild(new TreeNode());

        node.addChild(node1);
    }

    private List<TreeNode> children = new ArrayList<TreeNode>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private List<Object> values = new ArrayList<Object>();

    private boolean isFirstChild = false;

    private String label;
    private String code;
    private int numOfChild;

    private Map<String, Object> attrs;
    private TreeNode parent;
    private int level = 0;
    private Integer row = null;
    private Integer column = null;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public TreeNode() {

    }

    public TreeNode(String label) {
        this.setLabel(label);
    }

    public TreeNode(String id, String label) {
        super();
        this.label = label;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public Map<String, Object> getAttrs() {
        if (attrs == null) {
            attrs = Maps.newHashMap();
        }
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public void putAttrKey(String key, Object value) {
        this.getAttrs().put(key, value);
    }

    public Object getAttrKey(String key) {
        return this.getAttrs().get(key);
    }

    public void setChildren(List<TreeNode> children) {
        for (TreeNode tn : children) {
            this.addChild(tn);
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public TreeNode addChild(TreeNode node) {
        if (children.size() == 0) {
            node.isFirstChild = true;
        }
        children.add(node);
        node.setParent(this);
        node.setLevel(this.getLevel() + 1);
        return node;
    }

    public boolean isLeaf() {
        return getChildren().size() == 0;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public TreeNode getaLeaf() {
        if (children.size() == 0) {
            return this;
        } else {
            for (int i = 0; i < children.size(); ++i) {
                TreeNode node = children.get(i);
                TreeNode treeNode = node.getaLeaf();
                if (treeNode != null) {
                    return treeNode;
                }
            }
        }
        return null;
    }

    public boolean childHasLabel() {
        for (int i = 0; i < children.size(); ++i) {
            TreeNode node = children.get(i);
            if (node.label != null) {
                return true;
            }
        }
        return false;
    }

    public TreeNode findChild(String id) {
        if (id.equalsIgnoreCase(id)) {
            return this;
        } else {
            for (int i = 0; i < children.size(); ++i) {
                TreeNode node = children.get(i);
                TreeNode treeNode = node.findChild(id);
                if (treeNode != null) {
                    return treeNode;
                }
            }
        }
        return null;
    }

    /**
     * 找到一个有具有值的叶子
     *
     * @return
     */
    public TreeNode getaLeafWithValues() {
        if (getChildren().size() == 0) {
            return this;
        } else {
            for (int i = 0; i < children.size(); ++i) {
                TreeNode node = children.get(i);
                TreeNode treeNode = node.getaLeaf();
                if (treeNode != null) {
                    return treeNode;
                }
            }
        }
        return null;
    }

    public int getAttrIntValue(String key) {
        return MapUtils.getIntValue(this.getAttrs(), key);
    }

    public String getAttrString(String key) {
        return MapUtils.getString(this.getAttrs(), key);
    }

    public int countLeaf() {
        int _countLeaf_ = getAttrIntValue(COUNT_LEAF_KEY);
        if (_countLeaf_ > 0) {
            return _countLeaf_;
        }
        if (children.size() == 0) {
            return 1;
        } else {
            int ivals[] = new int[children.size()];
            for (int i = 0; i < children.size(); ++i) {
                TreeNode node = children.get(i);
                ivals[i] = node.countLeaf();
            }
            int sum = 0;
            for (int i = 0; i < ivals.length; ++i) {
                sum += ivals[i];
            }
            this.putAttrKey(COUNT_LEAF_KEY, sum);
            return sum;
        }
    }

    /***
     * 将tree转换为list
     *
     * @return
     */
    public List<List<TreeNode>> toList() {
        @SuppressWarnings("unchecked")
        List<List<TreeNode>> result = (List<List<TreeNode>>) this.getAttrKey(TO_LIST_KEY);
        if (result != null) {
            return result;
        }
        result = Lists.newArrayList();
        List<TreeNode> tmp = Lists.newArrayList();
        this.treeToList(tmp, result);
        this.putAttrKey(TO_LIST_KEY, result);
        return result;
    }

    /***
     * 获得最长的路径上的各个节点
     *
     * @return
     */
    public List<TreeNode> maxLenPathTreeNodes() {
        @SuppressWarnings("unchecked")
        List<TreeNode> maxLenRowTreeNodes = (List<TreeNode>) this.getAttrKey(MAX_LEN_PATH_TREE_NODES_KEY);
        if (maxLenRowTreeNodes != null) {
            return maxLenRowTreeNodes;
        }
        int max = 0;
        List<List<TreeNode>> allList = this.toList();
        for (List<TreeNode> nodes : allList) {
            if (max < nodes.size()) {
                max = nodes.size();
                maxLenRowTreeNodes = nodes;
            }
        }
        this.putAttrKey(MAX_LEN_PATH_TREE_NODES_KEY, maxLenRowTreeNodes);
        return maxLenRowTreeNodes;
    }

    /***
     * 获得所有叶子节点
     *
     * @return
     */
    public List<TreeNode> leafNodes() {
        @SuppressWarnings("unchecked")
        List<TreeNode> leafNodes = (List<TreeNode>) this.getAttrKey(LEAF_NODES_KEY);
        if (leafNodes != null) {
            return leafNodes;
        }
        leafNodes = Lists.newArrayList();
        this.allLeaf(leafNodes);
        this.putAttrKey(LEAF_NODES_KEY, leafNodes);
        return leafNodes;
    }

    public void treeToList(List<TreeNode> tmpNodes, List<List<TreeNode>> result) {
        if (this.isLeaf()) {
            tmpNodes.add(this);
            result.add(Lists.newArrayList(tmpNodes));
            tmpNodes.remove(tmpNodes.size() - 1);
            return;
        }
        tmpNodes.add(this);
        for (TreeNode ch : this.getChildren()) {
            ch.treeToList(tmpNodes, result);
        }
        tmpNodes.remove(tmpNodes.size() - 1);
    }

    public void allLeaf(List<TreeNode> result) {
        if (children.size() == 0) {
            result.add(this);
        } else {
            for (TreeNode treeNode : children) {
                treeNode.allLeaf(result);
            }
        }
    }

    /***
     * <pre>
     * 计算所在的列数
     * 			------------------A---------------
     * 				 A1   	      A2        A3
     * 			A11	    A12    A21  A22
     * 		 A111  A112 --------------------------
     * </pre>
     */
    /**
     * 水平展示树的情况下，定义节点的行数
     *
     * @param stRow
     */
    public void calRowOriHorizontal(final int stRow) {
        /*
         * TreeNode parent = this.getParent(); if (parent != null) {
		 * this(parent.getRow() + 1); } else { this.setRow(stRow); } for
		 * (TreeNode treeNode : this.getChildren()) {
		 * treeNode.calRowOriHorizontal(stRow); }
		 */
    }

    /**
     * 水平展示树的情况下，定义节点的列
     *
     * @param stRow
     */
    public void calColOriHorizontal(final int stcol) {
        List<TreeNode> leafs = Lists.newArrayList();
        allLeaf(leafs);
        // 设置叶子节点的列
        for (int index = 0; index < leafs.size(); ++index) {
            leafs.get(index).setColumn(index + stcol);
        }
        // 依据叶子节点定义父节点的列
        for (TreeNode treeNode : leafs) {
            TreeNode parent = treeNode.getParent();
            while (parent != null) {
                if (parent.getColumn() == null) {
                    parent.setColumn(treeNode.getColumn());
                }
                parent = parent.getParent();
            }
        }
    }

    /**
     * 垂直展示树的情况下，定义节点的行
     *
     * @param stRow
     */
    public void calRowOriVertical(int stRow) {
        List<TreeNode> leafs = Lists.newArrayList();
        allLeaf(leafs);
        for (int nrow = 0; nrow < leafs.size(); ++nrow) {
            leafs.get(nrow).setRow(nrow + stRow);
        }
        for (TreeNode treeNode : leafs) {
            TreeNode parent = treeNode.getParent();
            while (parent != null) {
                if (parent.getRow() == null) {
                    parent.setRow(treeNode.getRow());
                }
                parent = parent.getParent();
            }
        }
    }

    /**
     * 垂直展示树的情况下，定义节点的列
     *
     * @param stRow
     */
    public void calColOriVertical(int stCol) {
        TreeNode parent = this.getParent();
        if (parent != null) {
            this.setColumn(parent.getColumn() + 1);
        } else {
            this.setColumn(stCol);
        }
        for (TreeNode treeNode : this.getChildren()) {
            treeNode.calColOriVertical(stCol);
        }
    }

    public int getNumOfChilds() {
        numOfChild = 0;
        TreeVisitor visitor = new TreeVisitor() {
            public void access(TreeNode tn) {
                numOfChild++;
            }
        };
        visit(visitor);
        return numOfChild - 1;
    }

    public void visit(TreeVisitor visitor) {
        visitor.access(this);
        for (TreeNode node : children) {
            node.visit(visitor);
        }
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values.clear();
        this.values.addAll(values);
    }

    public void setValues(Object[] values) {
        this.values.clear();
        this.values.addAll(Arrays.asList(values));
    }

    public void addValue(Object element) {
        this.values.add(element);
    }

    public void setValue(int index, Object element) {
        if (index < this.values.size()) {
            this.values.set(index, element);
        } else {
            throw new RuntimeException("index too big");
        }
    }

    public Object getValue(int index) {
        return this.values.get(index);
    }

    /**
     * @return
     */
    public int getDepth() {
        int depth = this.getAttrIntValue(GET_DEPTH_KEY);
        if (depth != 0) {
            return depth;
        }
        if (children.size() == 0) {
            return 0;
        } else {
            int ivals[] = new int[children.size()];
            for (int i = 0; i < children.size(); ++i) {
                TreeNode node = children.get(i);
                ivals[i] = node.getDepth();
            }
            depth = 1 + NumberUtils.max(ivals);
            this.putAttrKey(GET_DEPTH_KEY, depth);
            return depth;
        }
    }

    public boolean isFirstChild() {
        return isFirstChild;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public String pathCode() {
        String path = (String) this.getAttrKey(PATH_CODE_CODE);
        if (path != null) {
            return path;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.getCode());
        TreeNode parent = this.getParent();
        while (parent != null) {
            sb.insert(0, StringUtils.defaultString(parent.getCode()));
            parent = parent.getParent();
        }
        path = sb.toString();
        this.putAttrKey(PATH_CODE_CODE, path);
        return path;

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Object o : values) {
            sb.append(o).append(",");
        }
        return sb.toString();
    }

}
