<%@page contentType="text/html; charset=utf-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>sapling upload test</title>
<script type="text/javascript" src="/upload/jquery-1.11.1/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="/upload/uploadify-3.2.1/uploadify.css" />
<script type="text/javascript" src="/upload/uploadify-3.2.1/jquery.uploadify.min.js"></script>
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
table {
width: 90%;
border: 0;
background-color: #edf5f6;
margin: 0;
padding: 0;
text-align: center;
}
</style>
<script type="text/javascript">
    $(function() {
    	uploadify("#file_upload1", "#path1");
    	uploadify("#file_upload2", "#path2");
    	uploadify("#idFrontCopyFile2", "#idFrontCopy2");
    	uploadify("#idFrontCopyFile", "#idFrontCopy");
    	uploadify("#idBackCopyFile", "#idBackCopy");
    });	
</script>
</head>
<body>
	<form action="/upload" method="post" enctype="multipart/form-data">
		<select name="type">
			<option value="image">image</option>
		</select> <input type="file" name="file"> <input type="submit">
	</form>
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

<table class="bian2">	
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
<table class="bian2">	
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
  
</body>
</html>