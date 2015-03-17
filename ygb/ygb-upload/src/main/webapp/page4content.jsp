<%@page contentType="text/html; charset=utf-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>sapling upload for content-service</title>
<script type="text/javascript" src="/upload/jquery-1.11.1/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="/upload/uploadify-3.2.1/uploadify.css" />
<script type="text/javascript" src="/upload/uploadify-3.2.1/jquery.uploadify.min.js"></script>
<script type="text/javascript" src="/upload/validate.js"></script>
<script type="text/javascript">
//var static_url = "<%=request.getScheme()%>://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>";
var upload = "http://<%=com.qingbo.ginkgo.ygb.common.util.GinkgoConfig.getProperty("upload")%>";
var static_url = upload + "/upload";
</script>
<script type="text/javascript" src="/upload/uploadify-3.2.1/uploadify.settings.js"></script>
<style type="text/css">
.fCenter{
	text-align:center;
	margin:0 auto;
}
</style>
<script type="text/javascript">

	function initUpload(fileInputId, pathInputId){
	    $(fileInputId).uploadify(jQuery.extend(settings, {
			onUploadSuccess:function(file, data, response){
				var url = data.replace(",", "");
				$(pathInputId).val(url);
//				editorCallback(url);
			}
		}));
	}
	
	function editorCallback(url){
		alert(url);
		//top.showImageEdit(url);
	}

    $(function() {
    	initUpload("#file_upload1", "#path1");
    });
</script>
</head>
<body>
	<div class="fCenter">
		<div><input type="file"	name="file1" id="file_upload1" /></div>
		<div>
			<textarea name="path1" id="path1" rows="3" cols="30"></textarea>
		</div>
		<!--  
		<div>
			<input type="button" name="copy" id="copy" value="复制" />
			<input type="button" name="exit" id="exit" value="关闭" />
		</div>
		-->
	</div>
</body>
</html>