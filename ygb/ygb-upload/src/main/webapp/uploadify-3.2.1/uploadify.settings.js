//define these variables outside is ok
//var static_url = "http://192.168.1.201/upload";
//var upload = "http://192.168.1.201";
var settings = {
	swf : static_url+"/uploadify-3.2.1/uploadify.swf",
	cancelImg : static_url+"/uploadify-3.2.1/uploadify-cancel.png",
	buttonText : "选择文件...",
	uploader : upload+"/upload/temp",
	formData : {type : "image" },
	fileObjName : "file",
	fileTypeExts : "*.png;*.jpg;*.jpeg",
	fileTypeDesc : "图片文件",
	fileSizeLimit : 2048,
	successTimeout : 90
};
var settings_multi = {
		swf : static_url+"/uploadify-3.2.1/uploadify.swf",
		cancelImg : static_url+"/uploadify-3.2.1/uploadify-cancel.png",
		buttonText : "选择文件...",
		uploader : upload+"/upload/temp",
		formData : {type : "image" },
		fileObjName : "file",
		fileTypeExts : "*.png;*.jpg;*.jpeg",
		fileTypeDesc : "图片文件",
		fileSizeLimit : 2048,
		successTimeout : 90,
		multi : true,
};
/**
 * @param fileInputId <input type="file" id="fileInputId"/>
 * @param pathInputId <input id="pathInputId"/>
 */
function uploadify(fileInputId, pathInputId){
    $(fileInputId).uploadify(jQuery.extend(settings, {
		onUploadSuccess:function(file, data, response){
			var paths = data.split(",");
			$(pathInputId).val(paths[1]).next("a").attr("href",paths[0]+paths[1]).text("预览");
		}
	}));
}
function uploadify_multi(fileInputId, pathInputId){
	$(fileInputId).uploadify(jQuery.extend(settings_multi, {
		onUploadSuccess:function(file, data, response){
			var paths = data.split(",");
			$(pathInputId).val($(pathInputId).val()+paths[1]+"\n");
		}
	}));
}