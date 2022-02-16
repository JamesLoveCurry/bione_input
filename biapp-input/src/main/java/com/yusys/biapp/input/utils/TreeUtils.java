package com.yusys.biapp.input.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * <pre>
 * Title:树形数据结构的操作类
 * Description: 判断父子节点关系,获取父子节点等
 * </pre>
 * 
 * @author caijiufa
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class TreeUtils {

	public static List<String> containS(List<String> allTree,
			List<String> subTree) {
		List<String> list = Lists.newArrayList();
		for (String node : subTree) {
			if (allTree.contains(node))
				list.add(node);
		}
		return list;
	}

	/**
	 * 从集合subTree中获取存在于allTree中的节点
	 * 
	 * @param allTree
	 * @param subTree
	 * @return
	 */
	public static List<CommonTreeNode> containT(List<CommonTreeNode> allTree,
			List<String> subTree) {
		List<CommonTreeNode> list = Lists.newArrayList();
		for (CommonTreeNode node : allTree) {
			if (subTree.contains(node.getId()))
				list.add(node);
		}
		return list;
	}

	/**
	 * 判断某个节点是否是另一个节点的子节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param parentId
	 *            上级节点
	 * @param nodeId
	 *            需要判断的节点
	 * @return
	 */
	public static boolean isChildren(List<CommonTreeNode> treeList,
			String parentId, String nodeId) {
		List<CommonTreeNode> childrens = Lists.newArrayList();
		childrens(treeList, parentId, childrens);
		for (CommonTreeNode node : childrens) {
			if (nodeId.equals(node.getId()))
				return true;
		}
		return false;
	}

	/**
	 * 判断某个节点是否是另一个节点的父节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param childrenId
	 *            下级节点
	 * @param nodeId
	 *            需要判断的节点
	 * @return
	 */
	public static boolean isParent(List<CommonTreeNode> treeList,
			String childrenId, String nodeId) {
		List<CommonTreeNode> parents = Lists.newArrayList();
		parents(treeList, childrenId, parents);
		for (CommonTreeNode node : parents) {
			if (nodeId.equals(node.getId()))
				return true;
		}
		return false;
	}

	/**
	 * 获取给定树的某个节点的所有子节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param nodeId
	 *            给定节点
	 * @param childrens
	 *            返回所有子节点，用来保存所有子节点的List
	 */
	public static void childrens(List<CommonTreeNode> treeList, String nodeId,
			List<CommonTreeNode> childrens) {
		for (CommonTreeNode node : treeList) {
			if (nodeId.equals(node.getUpId())) {
				childrens.add(node);
				if (!node.getUpId().equals(node.getId()))
					childrens(treeList, node.getId(), childrens);
			}
		}
	}

	/**
	 * 获取给定树的某个节点的所有子节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param nodeId
	 *            给定节点
	 * @param childrens
	 *            返回所有子节点，用来保存所有子节点的List
	 */
	public static void childrensWithFather(List<CommonTreeNode> treeList,
			String nodeId, List<CommonTreeNode> childrens) {
		childrens(treeList, nodeId, childrens);

		for (CommonTreeNode node : treeList) {
			if (nodeId.equals((node.getParams()).get("realNo"))) {
				childrens.add(node);
				break;
			}
		}
	}

	/**
	 * 获取给定树的某个节点的所有子节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param nodeId
	 *            给定节点
	 * @param childrens
	 *            返回所有子节点，用来保存所有子节点的List
	 */
	public static void childrensCode(List<com.yusys.bione.comp.common.CommonTreeNode> treeList, String nodeId, List<String> childrens) {
		for (com.yusys.bione.comp.common.CommonTreeNode node : treeList) {
			if (nodeId.equals(node.getUpId())) {
				childrens.add(node.getId());
				childrensCode(treeList, node.getId(), childrens);
			}
		}
	}

	/**
	 * 获取给定树的某个节点的所有父节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param nodeId
	 *            给定节点
	 * @param childrens
	 *            返回所有父节点，用来保存所有父节点的List
	 */
	private static void AllParents(List<CommonTreeNode> treeList,
			String nodeId, List<CommonTreeNode> parents) {
		for (CommonTreeNode node : treeList) {
			if (node.getId().equals(nodeId)) {
				parents.add(node);
				if (!node.getUpId().equals(node.getId())
						&& StringUtils.isNotBlank(node.getUpId()))
					AllParents(treeList, node.getUpId(), parents);
			}
		}
	}

	/**
	 * 获取给定树的某个节点的所有父节点
	 * 
	 * @param treeList
	 *            树的所有节点
	 * @param nodeId
	 *            给定节点
	 * @param childrens
	 *            返回所有父节点，用来保存所有父节点的List
	 */
	public static void parents(List<CommonTreeNode> treeList, String nodeId,
			List<CommonTreeNode> parents) {
		AllParents(treeList, nodeId, parents);
		for (CommonTreeNode node : treeList) {
			if (nodeId.equals(node.getId())) {
				parents.remove(node);
				break;
			}
		}
	}

	/**
	 * 把无序的树梳理成有序的树
	 * 
	 * @param treeList
	 * @return
	 */
	public static List<CommonTreeNode> buildTree(List<CommonTreeNode> treeList) {
		List<String> idList = Lists.newArrayList();
		List<CommonTreeNode> orderTreeList = Lists.newArrayList();
		CommonTreeNode root = new CommonTreeNode();
		int order = 1;

		for (CommonTreeNode node : treeList) {
			idList.add(node.getId());
		}

		for (CommonTreeNode node : treeList) {
			if (node.getId().equals(node.getUpId())
					|| !idList.contains(node.getUpId())) {
				root = node;
				root.setLevel(1);
				root.setOrder(order++);
				orderTreeList.add(root);
				order(treeList, root, orderTreeList);
			}
		}

		return orderTreeList;
	}

	/**
	 * @param treeList
	 * @param parentNode
	 * @param orderTreeList
	 */
	private static void order(List<CommonTreeNode> treeList,
			CommonTreeNode parentNode, List<CommonTreeNode> orderTreeList) {
		int order = 1;
		for (CommonTreeNode node : treeList) {
			if (parentNode.getId().equals(node.getUpId())
					&& !parentNode.getId().equals(node.getId())) {
				node.setLevel(parentNode.level + 1);
				node.setOrder(order++);
				orderTreeList.add(node);
				order(treeList, node, orderTreeList);
			}
		}
	}
}
