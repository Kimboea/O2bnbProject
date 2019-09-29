<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>     
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../../js/board.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="../../css/board_sy.css">
</head>
<body>
	<div id="pay_content_div" class="container">
	<c:if test="${result==0}">
		<table class="table table-bordered" style="width: 600px;">
			<tr>
				<td>
					해당 링크의 유효 기간이 지났습니다.
				</td>
			</tr>
		</table>
	</c:if>
	
	<c:if test="${result==1}">
	<h1 class="my-5 font main_font" style="color : #ff5a5f; margin-bottom: 0;">
		${u_dto.name}님! 예약 정보입니다.
	</h1>
	<table class="table table-bordered" style="width: 600px;">
		<tr>
			<td style="text-align: center;">
				<img src="../../save/${u_dto.thumbnail}" style="width: 150px; height: 150px;">
			</td>
			<td>
				예약자 : ${u_dto.name}<br>
				예약일자 : <fmt:formatDate value="${u_dto.reserveday}" pattern="yyyy-MM-dd HH:mm"/><br>
				숙박일자 : ${u_dto.checkin} ~ ${u_dto.checkout} &#40;${in_out}박 ${in_out+1}일&#41;<br>
				숙소명 : ${u_dto.home_name}<br>
				호스트 : ${u_dto.host_name}<br>
				주소 : ${fn:split(u_dto.addr,'/')[1]}
			</td>
		</tr>
	</table>
	</c:if>
	</div>
</body>
</html>