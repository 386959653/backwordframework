package com.pds.p2p.core.data.validation;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/***
 * <pre>
 * 数据配置在XML中， 格式，示例：
 * <?xml version="1.0" encoding="UTF-8"?>
 * <forms>
 * 	<form id="testForm">
 * 		<options>
 * 	<![CDATA[
 * {
 *     rules: {
 *          username: {
 *              required:true,
 *              stringCheck:true ,
 *              minlength:8
 *          },
 *          email:{
 *              required:true,
 *              email:true
 *          },
 *          phone:{
 *              required:true,
 *              isPhone:true
 *          },
 *          address:{
 *              required:true,
 *              stringCheck:true,
 *              rangelength:[3,100]
 *             }
 *        },
 * 		titles: {
 * 			username:'用户名',
 * 			email:'电子邮件',
 * 			address:'地址'
 *        }
 * }
 * ]]></options>
 * 	</form>
 * </forms>
 * 目前支持如下函数，对应jquery.validation.js的内置函数:
 * (1)required:true                必输字段
 * (3)email:true                    必须输入正确格式的电子邮件
 * (4)url:true                        必须输入正确格式的网址
 * (5)date:true                      必须输入正确格式的日期 日期校验ie6出错，慎用
 * (6)dateISO:true                必须输入正确格式的日期(ISO)，例如：2009-06-23，1998/01/22 只验证格式，不验证有效性
 * (7)number:true                 必须输入合法的数字(负数，小数)
 * (8)digits:true                    必须输入整数
 * (9)creditcard:                   必须输入合法的信用卡号
 * (10)equalTo:"#field"          输入值必须和#field相同
 * (11)accept:                       输入拥有合法后缀名的字符串（上传文件的后缀）
 * (12)maxlength:5               输入长度最多是5的字符串(汉字算一个字符)
 * (13)minlength:10              输入长度最小是10的字符串(汉字算一个字符)
 * (14)rangelength:[5,10]      输入长度必须介于 5 和 10 之间的字符串")(汉字算一个字符)
 * (15)range:[5,10]               输入值必须介于 5 和 10 之间
 * (16)max:5                        输入值不能大于5
 * (17)min:10                       输入值不能小于10
 * </pre>
 *
 * @author Administrator
 */
public class FormValidatorFactory {
    public static void main(String[] args) {
        // 可以在系统初始化调用，能从多个inputstream读取配置数据,示例见：test.xml文件，也
        // 可使用这个文件做测试
        FormValidatorFactory.load(FormValidatorFactory.class.getResourceAsStream("test.xml"));
        // 使用
        List<String> errs = FormValidatorFactory.get("testForm").valid(ImmutableMap.of( //
                "username", "王文", //
                "address", "王文"));
        System.out.println(errs);
        System.out.println(FormValidatorFactory.get("testForm").toJs());
    }

    private static Map<String, FormValidator> formValidators = Maps.newHashMap();

    public static FormValidator get(String formId) {
        return formValidators.get(formId);
    }

    public static void load(InputStream... inputs) {
        for (InputStream input : inputs) {
            XPathParser parser = new XPathParser(input);
            List<XNode> nodes = parser.evalNodes("forms/form");
            for (XNode node : nodes) {
                String formId = node.getStringAttribute("id");
                Validate.notEmpty(formId);
                Validate.isTrue(!formValidators.containsKey(formId));
                FormValidator formValidator = new FormValidator(formId);
                String options = node.evalString("options");
                formValidator.load(options);
                formValidators.put(formId, formValidator);
            }
        }
    }
}
