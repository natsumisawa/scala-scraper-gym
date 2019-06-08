package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.text

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>

    /*
    List(https://www.climbing-net.com/wp-content/uploads/2017/06/fish_1.jpg, https://www.climbing-net.com/wp-content/uploads/2017/06/fish_04.jpg, https://www.climbing-net.com/wp-content/uploads/2017/06/fish_03.jpg, https://www.climbing-net.com/wp-content/uploads/2017/06/fish_02.jpg)
未経験者から上級者まで楽しめる課題と美しい形状のボルダー壁 国内外の大会やルートセットで活躍するスタッフ陣。ボルダリングを長く続けられるように、ステップアップのノウハウを詰め込んだ課題を用意している。
〒158-0094 東京都世田谷区玉川2-24-1キュープラザ二子玉川B1
東急田園都市線・大井町線二子玉川駅より徒歩2分
03-6805-7905
平日 12:00〜23:00 土日祝 10:00〜21:00
【税込価格】 初回登録料：¥1620 1日料金：¥2160 2時間利用：¥1620
シューズ：¥324 チョーク：¥216
-
-
有/小学生〜
http://www.fish-bird.co.jp
クライミングジムSPIDER [港区]
List(https://www.climbing-net.com/gym_detail/spider/)
〒106-0047 東京都港区南麻布1-3-16 岩崎ビル1階
     */
    case class GymDetail(
      name: String,
      content: String,
      address: String,
      imgUrl: String,
//      tell: Char,
//      openTime: Char,
//      price: Char,
//      rental: Char,
      hpLink: String
    )

    val pageNum = (1 to 9).toList
    val gymDetailList: List[GymDetail] =
    pageNum.foldLeft(List[GymDetail]())((acc, num) => {
      val browser = JsoupBrowser()
      val doc = browser.get(s"https://www.climbing-net.com/page/${num}/?search_element_0=276&search_element_1_cnt=9&search_element_2_cnt=1&searchbutton=%E4%B8%8A%E8%A8%98%E3%81%AE%E6%9D%A1%E4%BB%B6%E3%81%8B%E3%82%89%E6%A4%9C%E7%B4%A2&csp=search_add&feadvns_max_line_0=3&fe_form_no=0")
      val titles = doc >> elements("div.jym_archive > ul > li")
      val result = titles.map(t => {
        val name = t >> text("h3.jym_title")
        val a = t >> element("a") >> attrs("href")
        val address = t >> text("p.jym_address")
        val docDetail = browser.get(a.head)
        println(a.head)
        val imgUrl = (docDetail >?> element("div#rg-gallery")).map(g => (g >?> element("img") >> attrs("src")).toString).getOrElse("")
        val content = (docDetail >?> element("div.article_area")).map(a => a >>text("div.article_contents")).getOrElse("")
        val table = (docDetail >?> element("div.property_area > table > tbody") >> elementList("tr"))
        val hpLink = table.map(tr => tr.map { l =>
          l >> text("td")
        }).getOrElse("").toString
        GymDetail(
          name, content, address, imgUrl.toString, hpLink)
      }
      )
      acc ::: result.toList
    })
    Ok(views.html.index(gymDetailList.toString))
  }
}
