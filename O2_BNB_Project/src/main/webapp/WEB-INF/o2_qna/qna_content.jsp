<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>      
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="//code.jquery.com/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../js/qna_sy.js"></script>
<link rel="stylesheet" href="../css/qna.css">
</head>
<body>
<c:import url="/header_black2.do"/>

<div id="qna_content_div" class="container">
	<div>
		<a href="qna_delete.do?num=${dto.num}&pageNum=${pageNum}" 
		 id="qna_del" onclick="return confirm('문의사항을 삭제하시겠습니까?');">
		 	Del
		</a>
		&nbsp;&nbsp;&nbsp;
		<a href="javascript:history.back();" id="qna_list">List</a>
	</div>
	<table class="table table-bordered" style="width: 600px;">
		<tr>
			<th style="width: 100px;">ID</th>
			<td> ${dto.id}</td>
			<th style="width: 100px;">WriteDay</th>
			<td>
				<fmt:formatDate value="${dto.writeday}" pattern="yyyy-MM-dd"/>
			</td>
		</tr>
		<tr>
			<th style="width: 100px;">Subject</th>
			<td colspan="3"> ${dto.subject} </td>
		</tr>
		<tr>
			<th colspan="4" style="text-align: center;">Content</th>
		</tr>
		<tr>
			<td colspan="4">
				<textarea rows="5" cols="60" style="resize:none; border: none;">${dto.content}</textarea>
			</td>
		</tr>
	</table>
	<embed src="http://localhost:3000/qna/review?num=${dto.num}&id=${sessionScope.login_id}" style="width: 800px; height: 800px;">
</div>
</body>
</html>