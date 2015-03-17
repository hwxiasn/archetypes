package com.qingbo.ginkgo.ygb.cms.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qingbo.ginkgo.ygb.cms.entity.Article;
import com.qingbo.ginkgo.ygb.cms.entity.ArticleText;
import com.qingbo.ginkgo.ygb.cms.entity.Message;
import com.qingbo.ginkgo.ygb.cms.entity.Subject;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleStatus;
import com.qingbo.ginkgo.ygb.cms.enums.ArticleWeight;
import com.qingbo.ginkgo.ygb.cms.enums.MessageType;
import com.qingbo.ginkgo.ygb.common.result.PageObject;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.result.SpecParam;
import com.qingbo.ginkgo.ygb.common.util.Pager;

public class ContentServiceTester extends BaseServiceTester {
	@Autowired private ContentService contentService;
	@Autowired private ArticlePublishTaskScheduler articlePublishTaskScheduler;
	
	/**
	 * 测试分页查询文章列表
	 */
	public void pageArticle(){
		Pager pager = new Pager();
		pager.setPageSize(2);
		pager.setProperties("weight,publishTime");
		pager.setDirection("desc");
		SpecParam<Article> spec = new SpecParam<Article>();
		Result<PageObject<Article>> result = contentService.listArticles(spec, pager);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			pager.init(result.getObject().getTotal());
			pager.setElements(result.getObject().getList());
			printPage(pager);
		}
	}
	
	/**
	 * 测试查询专题下靠前文章列表
	 */
	public void topArticles(){
		Result<List<Article>> result = contentService.listTopArticles("BYCFX", "000202", 2);
		if(result.getCode() != 0){
			System.out.println(result.getError());
			System.out.println(result.getMessage());	
		}else{
			for(Object object : result.getObject()){
				System.out.println(JSON.toJSON(object));
			}
		}
	}
	
	/**
	 * 测试读取文章内容
	 */
	public void readArticle(){
		Result<ArticleText> result = contentService.readArticle(1L);
		if(result.getCode() != 0){
			System.out.println(result.getError());
			System.out.println(result.getMessage());	
		}else{
			System.out.println(JSON.toJSON(result.getObject()));
		}
		assertNull(result.getObject());
	}
	
	/**
	 * 测试获取文章整体
	 */
	public void getArticle(){
		Result<Article> result = contentService.getArticle(141119141303040005L);
		if(result.getCode() != 0){
			System.out.println(result.getError());
			System.out.println(result.getMessage());	
		}else{
			System.out.println(JSON.toJSON(result.getObject()));
		}
	}
	
	/**
	 * 测试获取文章项
	 */
	public void getArticleItem(){
		Result<Article> result = contentService.getArticleItem(141203085149040021L);
		assertEquals("测试 制度完善将造新一轮牛市", result.getObject().getTitle());
	}
	
	/**
	 * 测试添加文章
	 */
	public void addArticle(){
		Article article = new Article();
		article.setSubject("0102");
		article.setTitle("蓝筹筑底基本成功 制度完善将造新一轮牛市");
		article.setAuthor("Admin");
		article.setCreateTime(new Date());
		article.setStatus(ArticleStatus.READY.getCode());
		article.setWeight(ArticleWeight.ODINARY.getCode());
		ArticleText text = new ArticleText();
		text.setTextStuff("<h2 style=\"text-align:center\"><strong>蓝筹筑底基本成功 制度完善将造新一轮牛市</strong></h2><h4 style=\"text-align:center\">2014-11-21 10:20 中国证券网</h4><p>&nbsp; &nbsp; 中国证券网讯（记者 马婧妤）中国证监会前主席周正庆21日在&ldquo;第十届中国证券市场年会&rdquo;上表示，当前传统蓝筹筑底基本成功，新兴蓝筹股应运而生，混改为资本市场发展提供了强劲驱动力，制度完善将造就新一轮牛市的基础。</p><p>&nbsp; &nbsp; 周正庆表示，当前经济发展进入&ldquo;新常态&rdquo;，经济增速虽然有所减缓，但仍然保持中高速增长，增速下调是结构性的调整。在经济&ldquo;新常态&rdquo;中，有必要采取一切可能的措施推动资本市场健康快速发展。</p><p>&nbsp; &nbsp;&nbsp;他认为，证券市场在经济&ldquo;新常态&rdquo;中具有更大的重要性，搞活<a class=\"a-tips-Article-QQ\" href=\"http://finance.qq.com/stock/\" style=\"text-decoration: none; outline: none; color: rgb(0, 0, 0); border-bottom-width: 1px; border-bottom-style: dotted; border-bottom-color: rgb(83, 109, 166);\" target=\"_blank\">股市</a>是振兴实体经济、深化改革、结构调整、推进创新型经济发展的需要，让牛市到来刻不容缓。</p><p>&nbsp; &nbsp;&nbsp;&ldquo;新形势下，资本市场振兴的条件已经具备。&rdquo;周正庆说，本周沪港通正式开通，意味着资本市场开通了一条对外开放的高速铁路，当前传统蓝筹筑底基本成功，新兴蓝筹股应运而生，混改为资本市场发展提供了强劲驱动力，行业并购重组日益活跃，制度完善将造就新一轮牛市的基础。</p><p>&nbsp; &nbsp;&nbsp;周正庆同时表示，近两年来<a class=\"a-tips-Article-QQ\" href=\"http://stockhtm.finance.qq.com/hcenter/index.htm?page=1020132\" style=\"text-decoration: none; outline: none; color: rgb(0, 0, 0); border-bottom-width: 1px; border-bottom-style: dotted; border-bottom-color: rgb(83, 109, 166);\" target=\"_blank\">新股</a>发行制度实施了一系列改革，尽快推出注册制改革方案已经成为共识，严厉打击违法违规使市场信心得到提振，这些都成为促进股市向好的合力。</p><p>&nbsp; &nbsp;&nbsp;他说，应当出台一系列政策措施，造就一轮新牛市，让<a class=\"a-tips-Article-QQ\" href=\"http://finance.qq.com/l/financenews/domestic/index.htm\" style=\"text-decoration: none; outline: none; color: rgb(0, 0, 0); border-bottom-width: 1px; border-bottom-style: dotted; border-bottom-color: rgb(83, 109, 166);\" target=\"_blank\">宏观经济</a>与股票指数实现双赢，为中国梦提供坚实的经济和物质基础。</p>");
		article.setTextStuff(text);
		
		Result<Boolean> result = contentService.createArticle(article);
		if(result.getCode() != 0){
			System.out.println(result.getError());
			System.out.println(result.getMessage());	
		}else{
			System.out.println(result.getObject());
		}
	}
	
	/**
	 * 测试修改文章
	 */
	public void updateArticle(){
		Article article = new Article();
		article.setId(3L);
		article.setSubject("000202");
		article.setTitle("文章测试了");
		article.setCreateTime(new Date());
		article.setWeight(ArticleWeight.ODINARY.getCode());
		ArticleText text = new ArticleText();
		text.setTextStuff("文章Test文章Test文章测试文章测试");
		article.setTextStuff(text);
		
		Result<Boolean> result = contentService.updateArticle(article, 0);
		System.out.println(result.getObject());
	}
	
	/**
	 * 测试删除文章
	 */
	public void deleteArticle(){
		contentService.deleteArticle(141203085149040021L);
		Result<Article> result1 = contentService.getArticleItem(141203085149040021L);
		assertTrue(result1.getObject().isDeleted());
	}
	
	/**
	 * 测试发布文章
	 */
	public void publishArticle(){
		Result<Boolean> result = contentService.publishArticle(3L, new Date());
		System.out.println(result.getObject());
	}
	
	/**
	 * 测试下线文章
	 */
	public void offlineArticle(){
		Result<Boolean> result = contentService.offlineArticle(3L);
		System.out.println(result.getObject());
	}
	
	/**
	 * 测试调整文章权重
	 */
	public void setArticleWeight(){
		contentService.setArticleWeight(141121133338040001L, 15, false);
		Result<Article> result1 = contentService.getArticleItem(141121133338040001L);
		assertEquals(0, result1.getObject().getWeight().intValue());
	}
	
	/**
	 * 测试列出子专题
	 */
	public void childrenSubject(){
		Result<List<Subject>> result = contentService.listSubjects("BYCFX", "01");
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			for(Object object : result.getObject()){
				System.out.println(JSON.toJSON(object));
			}
		}
	}
	
	/**
	 * 测试列出所有专题
	 */
	public void allSubjects(){
		Result<List<Subject>> result = contentService.listSubjects("BYCFX");
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			for(Object object : result.getObject()){
				System.out.println(JSON.toJSON(object));
			}
		}
	}
	
	/**
	 * 测试添加专题
	 */
	public void addSubject(){
		Subject subject = new Subject();
		subject.setCode("0103");
		subject.setParentCode("01");
		subject.setName("测试专题");
		subject.setLevel(2);
		subject.setLeaf(false);
		subject.setSerial(3);
		
		Result<Boolean> result = contentService.createSubject(subject);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	public void addSubjectBatch(){
		List<Subject> subjects = new ArrayList<Subject>();
		//							平台标志		编码			父编码		标题		  层级	是否单一	同级序列
		subjects.add(generateSubject("BYCFX",		"00",		"-1",	"文章系统",	0,	false,	1));
		subjects.add(generateSubject("BYCFX",		"01",		"00",	"帮助中心",	1,	false,	1));
		subjects.add(generateSubject("BYCFX",		"02",		"00",	"关于我们",	1,	false,	2));
		subjects.add(generateSubject("BYCFX",		"0101",		"01",	"投资须知",	2,	false,	1));
		subjects.add(generateSubject("BYCFX",		"0102",		"01",	"常见问题",	2,	false,	2));
		subjects.add(generateSubject("BYCFX",		"0103",		"01",	"战略伙伴",	2,	false,	3));
		subjects.add(generateSubject("BYCFX",		"0201",		"02",	"公司简介",	2,	false,	1));
		subjects.add(generateSubject("BYCFX",		"0202",		"02",	"平台简介",	2,	false,	2));
		subjects.add(generateSubject("BYCFX",		"0203",		"02",	"网站声明",	2,	false,	3));
		subjects.add(generateSubject("BYCFX",		"0204",		"02",	"平台公告",	2,	false,	4));
		subjects.add(generateSubject("BYCFX",		"0205",		"02",	"行业新闻",	2,	false,	5));
		subjects.add(generateSubject("BYCFX",		"0206",		"02",	"诚聘英才",	2,	false,	6));
		subjects.add(generateSubject("BYCFX",		"0207",		"02",	"联系我们",	2,	false,	7));
		
		for(Subject subject : subjects){
			contentService.createSubject(subject);
		}
	}
	
	private Subject generateSubject(String site, String code, String parentCode, String name, int level, boolean leaf, int serial){
		Subject subject = new Subject();
		subject.setSite(site);
		subject.setCode(code);
		subject.setParentCode(parentCode);
		subject.setName(name);
		subject.setLevel(level);
		subject.setLeaf(leaf);
		subject.setSerial(serial);
		
		return subject;
	}
	
	/**
	 * 测试修改专题
	 */
	public void updateSubject(){
		
		// 准备修改对象 
		Subject subject = new Subject();
		subject.setId(5L);
//		subject.setCode("000203");
		subject.setName("测试专题0");
//		subject.setParentCode("000200");
		subject.setLeaf(true);
		subject.setSerial(3);
		
		// 修改
		Result<Boolean> result = contentService.updateSubject(subject);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(result.getObject());
		}
	}
	
	/**
	 * 测试删除专题
	 */
	public void deleteSubject(){
		
		// 删除前查看
		Result<List<Subject>> resultList = contentService.listSubjects("01");
		for(Object object : resultList.getObject()){
			System.out.println(JSON.toJSON(object));
		}
		
		// 删除
		Result<Boolean> result = contentService.deleteSubject(3L);
		System.out.println(result.getObject());
		
		// 删除后查看
		Result<List<Subject>> resultListTail = contentService.listSubjects("01");
		for(Object object : resultListTail.getObject()){
			System.out.println(JSON.toJSON(object));
		}
	}
	
	/**
	 * 测试获取单个专题
	 */
	public void getSubject(){
		Result<Subject> result = contentService.getSubject(141121133629040002L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			System.out.println(JSON.toJSON(result.getObject()));
		}
	}
	
	public void createMessage(){
		Message message = new Message();
		message.setReceiverId(100000L);
		message.setType(MessageType.GROUP.getCode());
		message.setTitle("恭喜恭喜");
		message.setText("吴掌柜邀请您投资优选项目！");
		Result<Message> result = contentService.createMessage(message);
		if(result.hasObject()){
			System.out.println(JSON.toJSON(result.getObject()));
		}else{
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}
	}
	
	/**
	 * 测试分页查询消息列表
	 */
	public void pageMessages(){
		Pager pager = new Pager();
		pager.setPageSize(2);
		pager.setProperties("createAt");
		pager.setDirection("desc");
		SpecParam<Message> spec = new SpecParam<Message>();
		Result<PageObject<Message>> result = contentService.listMessages(spec, pager, 100000L);
		if(result.getCode() != 0){
			System.out.println("异常结束：" + result.getError() + "--" + result.getMessage());
		}else{
			pager.init(result.getObject().getTotal());
			pager.setElements(result.getObject().getList());
			printPage(pager);
		}
	}
	
	/**
	 * 测试查询专题下靠前消息列表
	 */
	public void topMessages(){
		Result<List<Message>> result = contentService.listTopMessages(100000L, 2);
		if(result.getCode() != 0){
			System.out.println(result.getError());
			System.out.println(result.getMessage());	
		}else{
			for(Object object : result.getObject()){
				System.out.println(JSON.toJSON(object));
			}
		}
	}
	
	/**
	 * 测试读取消息内容
	 */
	public void readMessage(){
		Result<Message> result = contentService.readMessage(150202150722040001L);
		if(result.getCode() != 0){
			System.out.println(result.getError());
			System.out.println(result.getMessage());	
		}else{
			System.out.println(JSON.toJSON(result.getObject()));
		}
		assertNull(result.getObject());
	}
	
	/**
	 * 测试删除消息
	 */
	@Test
	public void deleteMessage(){
		Result<Boolean> result = contentService.deleteMessage(3L);
		System.out.println(result.getObject());
	}
}
