<%@page import="com.yusys.bione.frame.base.common.GlobalConstants4frame"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- project context pathƒ -->
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!-- common tree root node number† -->
<c:set var="treeRoot" value="<%=GlobalConstants4frame.DEFAULT_TREE_ROOT_NO%>"/>