<%@page contentType="text/html; charset=utf-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>sapling upload test</title>
<script type="text/javascript" src="/upload/jquery-1.11.1/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="/upload/uploadify-3.2.1/uploadify.css" />
<script type="text/javascript" src="/upload/uploadify-3.2.1/jquery.uploadify.min.js"></script>
<script type="text/javascript" src="/upload/validate.js"></script>
<script type="text/javascript">
var static_url = "<%=request.getScheme()%>://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>";
var upload = "<%=request.getScheme()%>://<%=request.getServerName()%>:<%=request.getServerPort()%>";
//var upload = "<%=com.qingbo.ginkgo.ygb.common.util.GinkgoConfig.getProperty("upload")%>";
</script>
<script type="text/javascript" src="/upload/uploadify-3.2.1/uploadify.settings.js"></script>
<style type="text/css">
.button-all{
	width:100%;
	height:40px;
	float:left;
	line-height:40px;
	position:relative;
}
.button-left{
	float:left;padding-right:20px;
}
.button-right{
	float:left;
}
.Mtext1-1 {
	height: 25px;
	width: 200px;
	background-color: #f7f7f7;
	border: 1px solid #a0a0a0;
}
</style>
<script type="text/javascript">
    $(function() {
    	uploadify("#file_upload1", "#path1");
    	uploadify("#file_upload2", "#path2");
    	uploadify("#idFrontCopyFile2", "#idFrontCopy2");
    	uploadify("#idFrontCopyFile", "#idFrontCopy");
    	uploadify("#idBackCopyFile", "#idBackCopy");
    	
    	uploadify_multi("#file_upload_multi", "#path_multi");
    });	
</script>
</head>
<body>
	<form action="<%=request.getContextPath()%>/upload" method="post" enctype="multipart/form-data">
		<select name="type">
			<option value="image">image</option>
		</select> <input type="file" name="file"> <input type="submit">
	</form>
	<div class="button-all">
		<div class="button-left">
			<input name="idCard" id="idCard" class="Mtext1-1" placeholder="身份证号"/>
		</div>
		<div class="button-right">
			<input type="button" onclick="checkParseIdCard($('#idCard').val())" value="检查身份证号"/>
		</div>
	</div>
	<div class="button-all">
		<div class="button-left">
			<input name="path1" id="path1" class="Mtext1-1"/><a href="" target="_blank" style="display:none"></a>
		</div>
		<div class="button-right"><input type="file" name="file1" id="file_upload1" /></div>
	</div>
	
	<div class="button-all">
		<div class="button-left">
			<input name="path2" id="path2" class="Mtext1-1"/><a href="" target="_blank" style="display:none"></a>
		</div>
		<div class="button-right"><input type="file" name="file2" id="file_upload2" /></div>
	</div>

<table width="99%" border="0" bgcolor="#edf5f6" cellspacing="0" cellpadding="0" align="center" class="bian2">	
	  <tr>
    <td width="2%" height="40px"  >&nbsp;</td>
    <td width="13%" height="40px" >&nbsp;上传身份证正面照片 ：</td>
    <td width="85%"  >
		<div class="button-all">
    		<div class="button-left">
        		<input id="idFrontCopy2" name="idFrontCopy" class="Mtext1-1" disabled /><a href="" target="_blank"></a>
    		</div>
    		<div class="button-right">
            	<input id="idFrontCopyFile2" type="file" name="idFrontCopyFile" />
			</div>
		</div>		
	</td>
  </tr>
</table>
<hr/>
<table width="99%" border="0" bgcolor="#edf5f6" cellspacing="0" cellpadding="0" align="center" class="bian2">	
	  <tr>
    <td width="2%" height="40px"  >&nbsp;</td>
    <td width="13%" height="40px" >&nbsp;上传身份证正面照片 ：</td>
    <td width="85%"  >
		<div class="button-all">
    		<div class="button-left">
        		<input id="idFrontCopy" name="idFrontCopy" class="Mtext1-1" disabled /><a href="" target="_blank"></a>
    		</div>
    		<div class="button-right">
            	<input id="idFrontCopyFile" type="file" name="idFrontCopyFile" />
			</div>
		</div>		
	</td>
  </tr>
  <tr>
    <td width="2%" height="40px"  >&nbsp;</td>
    <td width="13%" height="40px" >&nbsp;上传身份证反面照片 ：</td>
    <td width="85%" >
		<div class="button-all">
    		<div class="button-left">
        		<input id="idBackCopy" name="idBackCopy" class="Mtext1-1" disabled /><a href="" target="_blank"></a>
    		</div>
    		<div class="button-right">
            	<input id="idBackCopyFile" type="file" name="idBackCopyFile" />
			</div>
		</div>	
	</td>
  </tr>
</table>

	<div class="button-all">
		<div class="button-left">
			<textarea rows="12" cols="50" name="path_multi" id="path_multi" class="Mtext1-1" style="width:500px;height:220px"></textarea>
		</div>
		<div class="button-right"><input type="file" name="file_multi" id="file_upload_multi" /></div>
	</div>
</body>
</html>