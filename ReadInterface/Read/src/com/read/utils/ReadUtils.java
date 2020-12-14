package com.read.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.read.utils.MyHttp.OnStreamListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ReadUtils {

	public static void getSearchName(String searchName, String page, HttpServletResponse response) {
		try {
			String url = "https://www.biquge.com.cn/search.php?q=" + URLEncoder.encode(searchName, "UTF-8") + "&p=" + page;
			MyHttp.send(url, new OnStreamListener() {

				@Override
				public void onStreamListener(InputStream inputStream) {
					JSONObject jsonObject = new JSONObject();
					try {
						String readStream = StreamTools.readStream(inputStream);
						Document document = Jsoup.parse(readStream);
						// 每本书的条目
						Elements itemElements = document.getElementsByClass("result-item result-game-item");
						Element pageMainElement = document.getElementsByClass("search-result-page-main").get(0);
						// 搜索总页数
						jsonObject.put("countPage", pageMainElement.children().size());
						jsonObject.put("currentPage", page);
						JSONArray jsonArray = new JSONArray();
						for (Element element : itemElements) {
							JSONObject object = new JSONObject();
							// 获取书名
							Element bookNamElement = element.getElementsByClass("result-game-item-title-link").get(0);
							object.put("name", bookNamElement.text());
							// 获取书的id
							Pattern pattern = Pattern.compile("/book/([\\s\\S]*?)/");
							Matcher matcher = pattern.matcher(bookNamElement.attr("href"));
							if (matcher.find()) {
								object.put("id", matcher.group(1));
							}
							// 拿到图片
							String img = element.getElementsByClass("result-game-item-pic-link-img").get(0).attr("src");
							// 拿到简介
							String desc = element.getElementsByClass("result-game-item-desc").get(0).text();
							// 获取介绍类型
							Element info = element.getElementsByClass("result-game-item-info").get(0);
							object.put("img", img);
							object.put("desc", desc);
							JSONArray array = new JSONArray();
							// 获取标签
							for (Element tag : info.getElementsByClass("result-game-item-info-tag")) {
								String[] text = tag.text().split("：");
								JSONObject textObject = new JSONObject();
								textObject.put("key", text[0]);
								textObject.put("value", text[1]);
								array.add(textObject);
							}
							object.put("info", array);
							jsonArray.add(object);
						}
						jsonObject.put("data", jsonArray);
						jsonObject.put("code", "200");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						jsonObject.put("code", "0");
					} finally {
						try {
							response.getWriter().write(jsonObject.toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("code", "0");
				response.getWriter().write(jsonObject.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void getChapterList(String id, HttpServletResponse response) {
		String url = "https://www.biquge.com.cn/book/" + id + "/";
		MyHttp.send(url, new OnStreamListener() {

			@Override
			public void onStreamListener(InputStream inputStream) {
				JSONObject jsonObject = new JSONObject();
				try {
					String readStream = StreamTools.readStream(inputStream);
					Document document = Jsoup.parse(readStream);
					Element idElement = document.getElementById("list");
					JSONArray jsonArray = new JSONArray();
					Elements tagList = idElement.getElementsByTag("a");
					for (Element tag : tagList) {
						JSONObject object = new JSONObject();
						String chapterName = tag.text();
						String link = tag.attr("href");
						object.put("name", chapterName);
						object.put("url", link);
						jsonArray.add(object);
					}
					jsonObject.put("data", jsonArray);
					jsonObject.put("code", "200");
				} catch (IOException e) {
					e.printStackTrace();
					jsonObject.put("code", "0");
					try {
						response.getWriter().write(jsonObject.toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} finally {
					try {
						response.getWriter().write(jsonObject.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void getChapterText(String url, HttpServletResponse response) {
		String path = "https://www.biquge.com.cn" + url;
		MyHttp.send(path, new OnStreamListener() {

			@Override
			public void onStreamListener(InputStream inputStream) {
				JSONObject jsonObject = new JSONObject();
				try {
					String readStream = StreamTools.readStream(inputStream);
					Document document = Jsoup.parse(readStream);
					// 获取文本内容
					Element elementById = document.getElementById("content");
					// 获取标题
					Element bookNamElement = document.getElementsByClass("bookname").get(0);
					String name = bookNamElement.getElementsByTag("h1").get(0).text();
					jsonObject.put("name", name);
					Element bottem1 = bookNamElement.getElementsByClass("bottem1").get(0);
					Elements elementsByTagA = bottem1.getElementsByTag("a");// 获取所有的a标签
					// 获取上一章
					String previous = elementsByTagA.get(0).attr("href");
					// 获取下一章
					String next = elementsByTagA.get(2).attr("href");
					jsonObject.put("pre", previous);
					jsonObject.put("next", next);
					jsonObject.put("content", elementById.toString());
					jsonObject.put("code", "200");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jsonObject.put("code", "0");
					try {
						response.getWriter().write(jsonObject.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} finally {
					try {
						response.getWriter().write(jsonObject.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 获取排行榜
	 */
	public static void getSort(String type, HttpServletResponse response) {
		Map<String, String> map = new HashedMap();
		// 填入参数
		String url = "https://www.biquge.com.cn/";
		map.put("1", "xuanhuan");// 玄幻小说
		map.put("2", "xiuzhen");// 修真小说
		map.put("3", "dushi");// 都市小说
		map.put("4", "lishi");// 历史小说
		map.put("5", "wangyou");// 网游小说
		map.put("6", "kehuan");// 科幻小说
		map.put("7", "qita");// 其他小说
		String value = map.get(type);
		MyHttp.send(url + value, new OnStreamListener() {

			@Override
			public void onStreamListener(InputStream inputStream) {
				JSONObject jsonObject = new JSONObject();
				try {
					String readStream = StreamTools.readStream(inputStream);
					Document document = Jsoup.parse(readStream);
					Element newscontent = document.getElementsByClass("l").get(0);// 最新
					// 获取最新排行
					Elements liTag = newscontent.getElementsByTag("li");
					JSONArray newJsonArray = new JSONArray();// 最新
					for (Element li : liTag) {
						JSONObject newJsonObject = new JSONObject();
						Elements spanTag = li.getElementsByTag("span");
						Element bookName = spanTag.get(0).getElementsByTag("a").get(0);
						Element chapter = spanTag.get(1).getElementsByTag("a").get(0);
						Element author = spanTag.get(2);
						// 获取书的id
						Pattern pattern = Pattern.compile("/book/([\\s\\S]*?)/");
						Matcher matcher = pattern.matcher(bookName.attr("href"));
						if (matcher.find()) {
							newJsonObject.put("id", matcher.group(1));
						}
						newJsonObject.put("name", bookName.text());
						newJsonObject.put("cheapterUrl", chapter.attr("href"));
						newJsonObject.put("cheapterName", chapter.text());
						newJsonObject.put("author", author.text());
						newJsonArray.add(newJsonObject);
					}
					// 获取推荐排行
					Element hotElement = document.getElementsByClass("r").get(0);
					Elements hotLi = hotElement.getElementsByTag("li");
					JSONArray hotJsonArray = new JSONArray();
					for (Element li : hotLi) {
						JSONObject hotJsonObject = new JSONObject();
						Elements spanTag = li.getElementsByTag("span");
						Element bookName = spanTag.get(0).getElementsByTag("a").get(0);
						Element author = spanTag.get(1);
						// 获取书的id
						Pattern pattern = Pattern.compile("/book/([\\s\\S]*?)/");
						Matcher matcher = pattern.matcher(bookName.attr("href"));
						if (matcher.find()) {
							hotJsonObject.put("id", matcher.group(1));
						}
						hotJsonObject.put("name", bookName.text());
						hotJsonObject.put("author", author.text());
						hotJsonArray.add(hotJsonObject);
					}
					jsonObject.put("code", "200");
					jsonObject.put("new", newJsonArray);
					jsonObject.put("hot", hotJsonArray);
				} catch (Exception e) {
					jsonObject.put("code", "0");
					e.printStackTrace();
				} finally {
					try {
						response.getWriter().write(jsonObject.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
}
