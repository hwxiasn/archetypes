/**  
 * 身份证15位编码规则：dddddd yymmdd xx p   
 * dddddd：地区码   
 * yymmdd: 出生年月日   
 * xx: 顺序类编码，无法确定   
 * p: 性别，奇数为男，偶数为女  
 * <p />  
 * 身份证18位编码规则：dddddd yyyymmdd xxx y   
 * dddddd：地区码   
 * yyyymmdd: 出生年月日   
 * xxx:顺序类编码，无法确定，奇数为男，偶数为女   
 * y: 校验码，该位数值可通过前17位计算获得  
 * <p />  
 * 18位号码加权因子为(从右到左) Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2,1 ]  
 * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]   
 * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )   
 * i为身份证号码从右往左数的 2...18 位; Y_P为脚丫校验码所在校验码数组位置  
 */
function checkParseIdCard(val) {
	var val = trim(val);
	var msg = checkIdcard(val);
	if (msg != "验证通过!") {
		alert(msg);
		return;
	}

	var birthdayValue;
	var sexId;
	var sexText;
	var age;

	if (15 == val.length) { //15位身份证号码
		birthdayValue = val.charAt(6) + val.charAt(7);
		if (parseInt(birthdayValue) < 10) {
			birthdayValue = '20' + birthdayValue;
		} else {
			birthdayValue = '19' + birthdayValue;
		}
		birthdayValue = birthdayValue + '-' + val.charAt(8) + val.charAt(9)
				+ '-' + val.charAt(10) + val.charAt(11);
		if (parseInt(val.charAt(14) / 2) * 2 != val.charAt(14)) {
			sexId = "1";
			sexText = "男";
		} else {
			sexId = "2";
			sexText = "女";
		}
	}
	if (18 == val.length) { //18位身份证号码
		birthdayValue = val.charAt(6) + val.charAt(7) + val.charAt(8)
				+ val.charAt(9) + '-' + val.charAt(10) + val.charAt(11) + '-'
				+ val.charAt(12) + val.charAt(13);
		if (parseInt(val.charAt(16) / 2) * 2 != val.charAt(16)) {
			sexId = "1";
			sexText = "男";
		} else {
			sexId = "2";
			sexText = "女";
		}
	}
	//年龄
	var dt1 = new Date(birthdayValue);
	var dt2 = new Date();
	var age = dt2.getFullYear() - dt1.getFullYear();
	var m = dt2.getMonth() - dt1.getMonth();
	if (m < 0)
		age--;
	alert(birthdayValue + sexId + sexText + age);
}
function checkIdcard(idcard) {
	var Errors = new Array("验证通过!", "身份证号码位数不对!", "身份证号码出生日期超出范围或含有非法字符!",
			"身份证号码校验错误!", "身份证地区非法!");
	var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",
	  23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",
	  41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",
	  52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",
	  71:"台湾",81:"香港",82:"澳门",91:"国外"} 
	var idcard, Y, JYM;
	var S, M;
	var idcard_array = new Array();
	idcard_array = idcard.split("");
	//地区检验 
	if (area[parseInt(idcard.substr(0, 2))] == null)
		return Errors[4];
	//身份号码位数及格式检验 
	switch (idcard.length) {
	case 15:
		if ((parseInt(idcard.substr(6, 2)) + 1900) % 4 == 0
				|| ((parseInt(idcard.substr(6, 2)) + 1900) % 100 == 0 && (parseInt(idcard
						.substr(6, 2)) + 1900) % 4 == 0)) {
			ereg = /^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/; //测试出生日期的合法性 
		} else {
			ereg = /^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/; //测试出生日期的合法性 
		}
		if (ereg.test(idcard))
			return Errors[0];
		else
			return Errors[2];
		break;
	case 18:
		//18位身份号码检测 
		//出生日期的合法性检查  
		//闰年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9])) 
		//平年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8])) 
		if (parseInt(idcard.substr(6, 4)) % 4 == 0
				|| (parseInt(idcard.substr(6, 4)) % 100 == 0 && parseInt(idcard
						.substr(6, 4)) % 4 == 0)) {
			ereg = /^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/; //闰年出生日期的合法性正则表达式 
		} else {
			ereg = /^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$/; //平年出生日期的合法性正则表达式 
		}
		if (ereg.test(idcard)) {//测试出生日期的合法性 
			//计算校验位 
			S = (parseInt(idcard_array[0]) + parseInt(idcard_array[10])) * 7
					+ (parseInt(idcard_array[1]) + parseInt(idcard_array[11]))* 9
					+ (parseInt(idcard_array[2]) + parseInt(idcard_array[12]))* 10
					+ (parseInt(idcard_array[3]) + parseInt(idcard_array[13]))* 5
					+ (parseInt(idcard_array[4]) + parseInt(idcard_array[14]))* 8
					+ (parseInt(idcard_array[5]) + parseInt(idcard_array[15]))* 4
					+ (parseInt(idcard_array[6]) + parseInt(idcard_array[16]))* 2
					+ parseInt(idcard_array[7]) * 1
					+ parseInt(idcard_array[8]) * 6 
					+ parseInt(idcard_array[9]) * 3;
			Y = S % 11;
			M = "F";
			JYM = "10X98765432";
			M = JYM.substr(Y, 1); //判断校验位 
			if (M == idcard_array[17])
				return Errors[0]; //检测ID的校验位 
			else
				return Errors[3];
		} else
			return Errors[2];
		break;
	default:
		return Errors[1];
		break;
	}
}
/**  
 * 验证18位数身份证号码中的生日是否是有效生日  
 * @param idCard 18位书身份证字符串  
 * @return  
 */  
function isValidityBrithBy18IdCard(idCard18){   
   var year =  idCard18.substring(6,10);   
   var month = idCard18.substring(10,12);   
   var day = idCard18.substring(12,14);   
   var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
   // 这里用getFullYear()获取年份，避免千年虫问题   
   if(temp_date.getFullYear()!=parseFloat(year)   
         ||temp_date.getMonth()!=parseFloat(month)-1   
         ||temp_date.getDate()!=parseFloat(day)){   
           return false;   
   }else{   
       return true;   
   }   
}   
/**  
 * 验证15位数身份证号码中的生日是否是有效生日  
 * @param idCard15 15位书身份证字符串  
 * @return  
 */  
function isValidityBrithBy15IdCard(idCard15){   
    var year =  idCard15.substring(6,8);   
    var month = idCard15.substring(8,10);   
    var day = idCard15.substring(10,12);   
    var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
    // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法   
    if(temp_date.getYear()!=parseFloat(year)   
            ||temp_date.getMonth()!=parseFloat(month)-1   
            ||temp_date.getDate()!=parseFloat(day)){   
              return false;   
      }else{   
          return true;   
      }   
}   
//去掉字符串头尾空格   
function trim(str) {   
  return str.replace(/(^\s*)|(\s*$)/g, "");  
}